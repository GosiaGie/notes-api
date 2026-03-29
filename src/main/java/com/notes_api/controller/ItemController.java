package com.notes_api.controller;

import com.notes_api.item.ItemService;
import com.notes_api.item.get.GetItemsResponse;
import com.notes_api.item.history.ItemHistoryResponse;
import com.notes_api.item.patch.PatchItemRequest;
import com.notes_api.item.patch.PatchItemResponse;
import com.notes_api.item.post.PostItemRequest;
import com.notes_api.item.post.PostItemResponse;
import com.notes_api.item.share.ShareItemRequest;
import com.notes_api.item.share.ShareItemResponse;
import com.notes_api.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public PostItemResponse postItem(@Valid @RequestBody PostItemRequest request,
                                     @AuthenticationPrincipal UserPrincipal user) {

        return itemService.postItem(request, user);

    }

    @GetMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    public GetItemsResponse getItems(@AuthenticationPrincipal UserPrincipal user) {

        return itemService.getItems(user);

    }

    @PatchMapping("/items/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PatchItemResponse patchItem(@PathVariable UUID id, @Valid @RequestBody PatchItemRequest request,
                                       @AuthenticationPrincipal UserPrincipal user) {

        return itemService.patchItem(id, request, user);
    }

    @GetMapping("/items/{id}/history")
    @ResponseStatus(HttpStatus.OK)
    public ItemHistoryResponse history(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal user) {
        return itemService.getItemHistory(id, user);
    }

    @DeleteMapping("/items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal user) {
        itemService.deleteItem(id, user);
    }

    @PostMapping("/items/{id}/share")
    @ResponseStatus(HttpStatus.CREATED)
    public ShareItemResponse shareItem(@PathVariable UUID id, @Valid @RequestBody ShareItemRequest request,
                                       @AuthenticationPrincipal UserPrincipal user) {

        return itemService.shareItem(id, request, user);

    }

    @DeleteMapping("/items/{id}/share{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePermission(@PathVariable UUID id, @PathVariable UUID userId,
                                 @AuthenticationPrincipal UserPrincipal userPrincipal) {


        itemService.deletePermission(id, userId, userPrincipal);

    }

}