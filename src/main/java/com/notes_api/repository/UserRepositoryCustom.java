package com.notes_api.repository;

import com.notes_api.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositoryCustom <T,S> {
    User findUserByLogin(String login);
}