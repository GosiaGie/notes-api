package com.notes_api.item.history;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class ItemHistoryResponse {

    List<ItemHistory> history;

}