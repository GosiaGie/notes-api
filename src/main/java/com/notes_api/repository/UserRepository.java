package com.notes_api.repository;

import com.notes_api.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID>, UserRepositoryCustom<User, UUID> {

}