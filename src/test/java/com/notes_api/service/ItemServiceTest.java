package com.notes_api.service;

import com.notes_api.security.Role;
import com.notes_api.entity.Item;
import com.notes_api.entity.ItemPermission;
import com.notes_api.entity.User;
import com.notes_api.item.ItemService;
import com.notes_api.item.patch.PatchItemRequest;
import com.notes_api.item.patch.PatchItemResponse;
import com.notes_api.item.share.ShareItemRequest;
import com.notes_api.item.share.ShareItemResponse;
import com.notes_api.repository.ItemRepository;
import com.notes_api.repository.UserRepository;
import com.notes_api.security.UserPrincipal;
import com.notes_api.user.exceptions.ValidationException;
import com.notes_api.user.register.datetime.DateTime;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    private static final UUID OWNER_ID = UUID.fromString("f353ca91-4fc5-49f2-9b9e-304f83d11914");
    private static final UUID EDITOR_ID = UUID.fromString("e942bbe9-afdc-4c62-a438-4034870f7d54");
    private static final UUID VIEWER_ID = UUID.fromString("2d9a6907-791e-450f-a39c-85a219808169");
    private static final UUID RANDOM_ID = UUID.randomUUID();
    private static final UUID ITEM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String OLD_TITLE = "some old title";
    private static final String NEW_TITLE = "some new title";
    private static final String OLD_CONTENT = "some old content";
    private static final String NEW_CONTENT = "some new content";
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2026, Month.JANUARY,1,0,0);
    private static final Long VERSION_1 = 1L;
    private static final Long VERSION_2 = 2L;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DateTime dateTime;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ItemService itemService;

    //permissions tests
    @Test
    void patchItemTestOwner() {
        when(dateTime.toLocalDateTime(any(Instant.class))).thenReturn(DATE_TIME);

        when(itemRepository.findById(any(UUID.class))).thenReturn(Optional.of(
                Item.builder()
                        .id(ITEM_ID)
                        .owner(User.builder().id(OWNER_ID).build()) //user from request is owner
                        .version(VERSION_1)
                        .title(OLD_TITLE)
                        .content(OLD_CONTENT)
                        .build()));

        doAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            item.setUpdatedAt(DATE_TIME.toInstant(ZoneOffset.UTC));
            return item;
        }).when(itemRepository).save(any(Item.class));

        UserPrincipal userPrincipal = UserPrincipal.builder().id(OWNER_ID).build();
        PatchItemRequest request = PatchItemRequest.builder()
                .version(1L)
                .title(NEW_TITLE) //change only in 'title'
                .build();

        PatchItemResponse response = itemService.patchItem(ITEM_ID, request, userPrincipal);

        assertEquals(ITEM_ID, response.getId());
        assertEquals(NEW_TITLE, response.getTitle());
        assertEquals(OLD_CONTENT, response.getContent());
        assertEquals(VERSION_1, response.getVersion());
        assertEquals(DATE_TIME, response.getUpdatedAt());

        verify(itemRepository, times(1)).findById(any(UUID.class));
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(dateTime, times(1)).toLocalDateTime(any(Instant.class));
    }

    @Test
    void patchItemTestEditor() {
        when(dateTime.toLocalDateTime(any(Instant.class))).thenReturn(DATE_TIME);

        //user from request is not owner, but has permission do edit, this case is editing 'content'
        when(itemRepository.findById(any(UUID.class))).thenAnswer(item -> {
            //permission to edit
            ItemPermission permission = ItemPermission.builder()
                    .user(User.builder().id(EDITOR_ID).build())
                    .role(Role.EDITOR)
                    .build();
            return Optional.of(Item.builder()
                    .id(ITEM_ID)
                    .owner(User.builder().id(OWNER_ID).build()) //user from request is NOT owner
                    .version(VERSION_1)
                    .title(OLD_TITLE)
                    .content(OLD_CONTENT)
                    .permissions(Set.of(permission))
                    .build());
        });


        doAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            item.setUpdatedAt(DATE_TIME.toInstant(ZoneOffset.UTC));
            return item;
        }).when(itemRepository).save(any(Item.class));

        UserPrincipal userPrincipal = UserPrincipal.builder().id(EDITOR_ID).build();
        PatchItemRequest request = PatchItemRequest.builder()
                .version(VERSION_1)
                .content(NEW_CONTENT) //change in 'content'
                .build();

        PatchItemResponse response = itemService.patchItem(ITEM_ID, request, userPrincipal);

        assertEquals(ITEM_ID, response.getId());
        assertEquals(OLD_TITLE, response.getTitle()); //no changes in 'title'
        assertEquals(NEW_CONTENT, response.getContent()); //changes in 'content'
        assertEquals(VERSION_1, response.getVersion());
        assertEquals(DATE_TIME, response.getUpdatedAt());

        verify(itemRepository, times(1)).findById(any(UUID.class));
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(dateTime, times(1)).toLocalDateTime(any(Instant.class));
    }

    @Test
    void patchItemTestViewer() {
        //user from request is not owner and doesn't have permission to edit
        when(itemRepository.findById(any(UUID.class))).thenAnswer(item -> {
            //permission to edit
            ItemPermission permission = ItemPermission.builder()
                    .user(User.builder().id(EDITOR_ID).build())
                    .role(Role.EDITOR)
                    .build();
            return Optional.of(Item.builder()
                    .owner(User.builder().id(OWNER_ID).build()) //user from request is NOT owner
                    .version(VERSION_1)
                    .title(OLD_TITLE)
                    .content(OLD_CONTENT)
                    .permissions(Set.of(permission))
                    .build());
        });

        UserPrincipal userPrincipal = UserPrincipal.builder().id(VIEWER_ID).build();  //user from request is viewer
        PatchItemRequest request = PatchItemRequest.builder()
                .version(1L)
                .content(NEW_CONTENT) //change in 'content'
                .build();

        try {
            itemService.patchItem(ITEM_ID, request, userPrincipal);
            fail();
        } catch (AccessDeniedException e) {
            assertEquals(AccessDeniedException.class, e.getClass());
        }

        verify(itemRepository, times(1)).findById(any(UUID.class));
        verify(itemRepository, times(0)).save(any(Item.class));
        verify(dateTime, times(0)).toLocalDateTime(any(Instant.class));
    }

    @Test
    void patchItemTestUserWithoutAnyPermission() {
        //user from request is not owner and doesn't have any permission
        when(itemRepository.findById(any(UUID.class))).thenAnswer(item -> {
            //permission to edit
            ItemPermission permissionToEdit = ItemPermission.builder()
                    .user(User.builder().id(EDITOR_ID).build())
                    .role(Role.EDITOR)
                    .build();
            ItemPermission permissionToView = ItemPermission.builder()
                    .user(User.builder().id(VIEWER_ID).build())
                    .role(Role.VIEWER)
                    .build();
            return Optional.of(Item.builder()
                    .owner(User.builder().id(OWNER_ID).build()) //user from request is NOT owner
                    .version(VERSION_1)
                    .title(OLD_TITLE)
                    .content(OLD_CONTENT)
                    .permissions(Set.of(permissionToEdit, permissionToView))
                    .build());
        });

        UserPrincipal userPrincipal = UserPrincipal.builder().id(RANDOM_ID).build();
        PatchItemRequest request = PatchItemRequest.builder()
                .version(VERSION_1)
                .title(NEW_TITLE) //change in 'title'
                .build();

        try {
            itemService.patchItem(ITEM_ID, request, userPrincipal);
            fail();
        } catch (AccessDeniedException e) {
            assertEquals(AccessDeniedException.class, e.getClass());
        }

        verify(itemRepository, times(1)).findById(any(UUID.class));
        verify(itemRepository, times(0)).save(any(Item.class));
        verify(dateTime, times(0)).toLocalDateTime(any(Instant.class));
    }

    //version conflict test
    @Test
    void setItemServiceTestVersionConflict() {
        when(itemRepository.findById(any(UUID.class))).thenReturn(Optional.of(
                Item.builder()
                        .owner(User.builder().id(OWNER_ID).build()) //user from request is owner
                        .version(VERSION_2) //new version form db
                        .title(OLD_TITLE)
                        .build()));

        UserPrincipal userPrincipal = UserPrincipal.builder().id(OWNER_ID).build();
        PatchItemRequest request = PatchItemRequest.builder()
                .version(VERSION_1) //old version from client
                .title(NEW_TITLE) //change in 'content'
                .build();

        try {
            itemService.patchItem(ITEM_ID, request, userPrincipal);
            fail();
        } catch (ObjectOptimisticLockingFailureException e) {
            assertEquals(ObjectOptimisticLockingFailureException.class, e.getClass());
        }

        verify(itemRepository, times(1)).findById(any(UUID.class));
        verify(itemRepository, times(0)).save(any(Item.class));
        verify(dateTime, times(0)).toLocalDateTime(any(Instant.class));
    }

    @Test
    void itemServiceTestShareItemToOwner() {
        //request trying to share Item to its owner
        when(itemRepository.findById(any(UUID.class))).thenReturn(Optional.of(
                Item.builder()
                        .id(ITEM_ID)
                        .owner(User.builder().id(OWNER_ID).build()) //user from request is owner
                        .version(VERSION_1)
                        .title(OLD_TITLE)
                        .build()));

        UserPrincipal userPrincipal = UserPrincipal.builder().id(OWNER_ID).build();

        ShareItemRequest request = ShareItemRequest.builder()
                .userId(OWNER_ID) //ownerId = userId from request
                .role(Role.EDITOR)
                .build();

        try {
            itemService.shareItem(ITEM_ID, request, userPrincipal);
            fail();
        } catch (ValidationException e) {
            assertEquals(ValidationException.class, e.getClass());
        }
        verify(itemRepository, times(1)).findById(any(UUID.class));
        verify(itemRepository, times(0)).save(any(Item.class));
        verify(dateTime, times(0)).toLocalDateTime(any(Instant.class));
    }

    @Test
    void itemServiceTestShareItemOverridingRole() {
        when(itemRepository.findById(any(UUID.class))).thenAnswer(item -> {
            //permission to view already exists
            ItemPermission permissionToView = ItemPermission.builder()
                    .user(User.builder().id(VIEWER_ID).build())
                    .role(Role.VIEWER)
                    .build();
            return Optional.of(Item.builder()
                    .id(ITEM_ID)
                    .owner(User.builder().id(OWNER_ID).build())
                    .version(VERSION_1)
                    .title(OLD_TITLE)
                    .content(OLD_CONTENT)
                    .permissions(Set.of(permissionToView))
                    .build());
        });
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(
                User.builder().id(VIEWER_ID).build()));
        when(dateTime.getDateTime()).thenReturn(DATE_TIME);

        UserPrincipal userPrincipal = UserPrincipal.builder().id(OWNER_ID).build();
        ShareItemRequest request = ShareItemRequest.builder()
                .userId(VIEWER_ID) //ownerId = userId from request
                .role(Role.EDITOR)
                .build();

        ShareItemResponse response = itemService.shareItem(ITEM_ID, request, userPrincipal);

        assertEquals(ITEM_ID, response.getItemID());
        assertEquals(VIEWER_ID, response.getUserID()); //userId in response = viewer who got permission to edit
        assertEquals(Role.EDITOR.name(), response.getRole()); //role changed to EDITOR
        assertEquals(DATE_TIME, response.getGrantedAt());

        verify(itemRepository, times(1)).findById(any(UUID.class));
        verify(dateTime, times(1)).getDateTime();
    }

}