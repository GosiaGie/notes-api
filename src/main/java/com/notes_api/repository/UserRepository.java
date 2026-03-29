package com.notes_api.repository;

import com.notes_api.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID>, UserRepositoryCustom<User, UUID> {
    default User getOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }
}