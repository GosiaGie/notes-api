package com.notes_api.user;

import com.notes_api.entity.User;
import com.notes_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByLogin(String login) {
        User user = userRepository.findUserByLogin(login);
        return user != null ? Optional.of(user): Optional.empty();
    }

}