package org.example.mybooksbot.user;

import lombok.RequiredArgsConstructor;
import org.example.mybooksbot.user.entity.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;


    public User getById(Long id) {
        return repository.findById(id).orElse(null);

    }

    public void save(User user) {
        repository.save(user);
    }

}
