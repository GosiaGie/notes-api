package com.notes_api.item;

import com.notes_api.Role;
import com.notes_api.entity.CustomRevisionEntity;
import com.notes_api.entity.Item;
import com.notes_api.entity.User;
import com.notes_api.item.get.GetItemsResponse;
import com.notes_api.item.get.ItemResponse;
import com.notes_api.item.history.ItemHistory;
import com.notes_api.item.history.ItemHistoryResponse;
import com.notes_api.item.patch.PatchItemRequest;
import com.notes_api.item.patch.PatchItemResponse;
import com.notes_api.item.post.PostItemRequest;
import com.notes_api.item.post.PostItemResponse;
import com.notes_api.repository.ItemRepository;
import com.notes_api.security.UserPrincipal;
import com.notes_api.user.register.datetime.DateTime;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final DateTime dateTime;
    private final EntityManager entityManager;

    @Autowired
    public ItemService(ItemRepository itemRepository, DateTime dateTime, EntityManager entityManager) {
        this.itemRepository = itemRepository;
        this.dateTime = dateTime;
        this.entityManager = entityManager;
    }


    @Transactional
    public PostItemResponse postItem(PostItemRequest request, UserPrincipal userPrincipal) {

        User user = User
                .builder()
                .id(userPrincipal.getId())
                .build();

        Item savedItem = itemRepository.save(Item.builder()
                .title(request.getTitle())
                .owner(user)
                .content(request.getContent())
                .createdAt(dateTime.getInstant())
                .updatedAt(dateTime.getInstant())
                .build());

        return PostItemResponse.builder()
                .id(savedItem.getId())
                .title(savedItem.getTitle())
                .content(savedItem.getContent() != null ? savedItem.getContent() : null)
                .version(savedItem.getVersion())
                .ownerId(savedItem.getOwner().getId())
                .createdAt(dateTime.toLocalDateTime(savedItem.getCreatedAt()))
                .updatedAt(dateTime.toLocalDateTime(savedItem.getUpdatedAt()))
                .build();
    }

    @Transactional
    public GetItemsResponse getItems(UserPrincipal userPrincipal) {

        List<ItemResponse> items = itemRepository.findAllByOwnerOrPermissions(
                userPrincipal.getId(), List.of(Role.EDITOR, Role.VIEWER))
                .stream()
                .map(item -> ItemResponse.builder()
                        .id(item.getOwner().getId())
                        .title(item.getTitle())
                        .content(item.getContent())
                        .version(item.getVersion())
                        .ownerId(item.getOwner().getId())
                        .myRole(item.getOwner().getId().equals(userPrincipal.getId()) ?
                                Role.OWNER.name() :
                                item.getPermissions().stream().filter(
                                        p-> p.getUser().getId().equals(userPrincipal.getId()))
                                        .findFirst().orElseThrow().toString())
                        .updatedAt(dateTime.toLocalDateTime(item.getUpdatedAt()))
                        .build()) .toList();

        return GetItemsResponse.builder()
                .list(items)
                .build();

    }

    @Transactional
    public PatchItemResponse patchItem(UUID id, PatchItemRequest request, UserPrincipal userPrincipal) {

        User user = User
                .builder()
                .id(userPrincipal.getId())
                .build();

        Item item = itemRepository.findById(id)
                .filter(i -> !i.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("item not found"));

        if (!item.getOwner().getId().equals(user.getId())) {
            boolean hasEditorRole = item.getPermissions().stream()
                    .anyMatch(p -> p.getUser().getId().equals(user.getId()) && p.getRole() == Role.EDITOR);

            if (!hasEditorRole) {
                throw new AccessDeniedException("access denied");
            }
        }

        if (!item.getVersion().equals(request.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(Item.class, id);
        }

        item.setTitle(request.getTitle());
        item.setContent(request.getContent());
        item.setVersion(request.getVersion());

        Item updatedItem = itemRepository.save(item);

        return PatchItemResponse.builder()
                .id(updatedItem.getId())
                .title(updatedItem.getTitle())
                .content(updatedItem.getContent())
                .version(updatedItem.getVersion())
                .updatedAt(dateTime.toLocalDateTime(updatedItem.getUpdatedAt()))
                .build();

    }

    @Transactional
    public ItemHistoryResponse getItemHistory(UUID itemId, UserPrincipal user) {

        Item currentItem = itemRepository.findById(itemId)
                .filter(i -> !i.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("item not found"));

        if (!hasAccess(currentItem, user)) {
            throw new AccessDeniedException("you don't have access to this note");
        }

        AuditReader reader = AuditReaderFactory.get(entityManager);
        List<Object[]> results = reader.createQuery()
                .forRevisionsOfEntity(Item.class, false, true)
                .add(AuditEntity.id().eq(itemId))
                .getResultList();

        List<ItemHistory> history = results.stream().map(row -> {
            Item item = (Item) row[0];
            CustomRevisionEntity revision = (CustomRevisionEntity) row[1];
            RevisionType type = (RevisionType) row[2];

            return ItemHistory.builder()
                    .revision(revision.getId())
                    .revisionType(type.name())
                    .timestamp(dateTime.toLocalDateTime(revision.getTimestamp()))
                    .changedBy(revision.getChangedBy())
                    .title(item.getTitle())
                    .content(item.getTitle())
                    .build();
        }).toList();

        return ItemHistoryResponse.builder()
                .history(history)
                .build();
    }

    private boolean hasAccess(Item item, UserPrincipal user) {
        if (item.getOwner().getId().equals(user.getId())) {
            return true;
        }
        return item.getPermissions().stream().anyMatch(p-> p.getUser().getId().equals(user.getId())
                && (p.getRole().equals(Role.EDITOR) || p.getRole().equals(Role.VIEWER)));
    }

}