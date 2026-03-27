package com.notes_api.user.register;

import com.notes_api.entity.User;
import com.notes_api.repository.UserRepository;
import com.notes_api.user.register.datetime.DateTime;
import com.notes_api.user.exceptions.AlreadyExistsException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DateTime dateTime;

    @Autowired
    public RegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder, DateTime dateTime) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.dateTime = dateTime;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByLogin(request.getLogin())) {
            throw new AlreadyExistsException("login already registered");
        }

        User user = User.builder()
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User userSaved = userRepository.save(user);

        return RegisterResponse.builder().
                id(String.valueOf(userSaved.getId()))
                .login(userSaved.getLogin())
                .createdAt(dateTime.getDateTime())
                .build();
    }

}