package com.sloyardms.trackerapi.service;

import com.sloyardms.trackerapi.dto.UserCreateDto;
import com.sloyardms.trackerapi.dto.UserDto;
import com.sloyardms.trackerapi.dto.UserUpdateDto;
import com.sloyardms.trackerapi.entity.User;
import com.sloyardms.trackerapi.mapper.UserMapper;
import com.sloyardms.trackerapi.repository.UserRepository;
import com.sloyardms.trackerapi.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public UserDto create(UserCreateDto userDto) {
        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(timeout = 10, readOnly = true)
    public UserDto findByUuid(UUID uuid) {
        User userDb = userRepository.findById(uuid).orElse(null);
        return userMapper.toDto(userDb);
    }

    @Override
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public UserDto update(UUID uuid, UserUpdateDto userDto) {
        User userDb = userRepository.findById(uuid).orElse(null);

        if(userDb != null) {
            userMapper.updateFromDto(userDto, userDb);

            User updatedUser = userRepository.save(userDb);
            return userMapper.toDto(updatedUser);
        }
        return null;
    }

    @Override
    @Transactional(timeout = 10, rollbackFor = Exception.class)
    public void delete(UUID uuid) {
        userRepository.deleteById(uuid);
    }

}
