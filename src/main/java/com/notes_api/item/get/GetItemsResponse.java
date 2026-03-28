package com.notes_api.item.get;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class GetItemsResponse {

    List<ItemResponse> list;

}