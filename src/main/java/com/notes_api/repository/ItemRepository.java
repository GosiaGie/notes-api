package com.notes_api.repository;

import com.notes_api.entity.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ItemRepository extends CrudRepository<Item, UUID> {
}
