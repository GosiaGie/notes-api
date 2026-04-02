package com.notes_api.repository;

import com.notes_api.security.Role;
import com.notes_api.entity.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends CrudRepository<Item, UUID> {
    @Query("SELECT DISTINCT i FROM Item i " +
            "LEFT JOIN i.permissions p " +
            "WHERE i.isDeleted = false " +
            "AND (i.owner.id = :userId OR (p.user.id = :userId AND p.role IN :roles))")
    List<Item> findAllByOwnerOrPermissions(
            @Param("userId") UUID userId,
            @Param("roles") List<Role> roles
    );
}
