package com.sloyardms.trackerapi.service;

import com.sloyardms.trackerapi.entity.User;
import com.sloyardms.trackerapi.repository.UserRepository;
import com.sloyardms.trackerapi.service.interfaces.UserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findByUuid(UUID uuid) {
        return userRepository.findById(uuid).orElse(null);
    }

    @Override
    public User update(User user) {
        User userDb = userRepository.findById(user.getUuid()).orElse(null);

        if(userDb != null) {
            if(user.getUsername() != null) {
                userDb.setUsername(user.getUsername());
            }

            if (user.getDarkMode() != null) {
                userDb.setDarkMode(user.getDarkMode());
            }

            if (user.getKeepOriginalImage() != null) {
                userDb.setKeepOriginalImage(user.getKeepOriginalImage());
            }

            return userRepository.save(userDb);
        }
        return null;
    }

    @Override
    public void delete(UUID uuid) {
        userRepository.deleteById(uuid);
    }
}
