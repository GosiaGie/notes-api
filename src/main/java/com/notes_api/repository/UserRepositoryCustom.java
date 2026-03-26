package com.notes_api.repository;

import com.notes_api.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositoryCustom <T,S> {
    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);
}