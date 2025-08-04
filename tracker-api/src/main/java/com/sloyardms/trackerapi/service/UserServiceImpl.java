package com.sloyardms.trackerapi.service;

import com.sloyardms.trackerapi.dto.UserCreateDto;
import com.sloyardms.trackerapi.dto.UserDto;
import com.sloyardms.trackerapi.dto.UserUpdateDto;
import com.sloyardms.trackerapi.entity.User;
import com.sloyardms.trackerapi.exception.ConstraintViolationDatabaseException;
import com.sloyardms.trackerapi.exception.ResourceDuplicatedException;
import com.sloyardms.trackerapi.exception.ResourceNotFoundException;
import com.sloyardms.trackerapi.mapper.UserMapper;
import com.sloyardms.trackerapi.repository.UserRepository;
import com.sloyardms.trackerapi.service.interfaces.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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
    @Transactional(rollbackFor = Exception.class)
    public UserDto create(UserCreateDto userDto) {
        User user = userMapper.toEntity(userDto);

        try {
            User savedUser = userRepository.save(user);
            return userMapper.toDto(savedUser);
        }catch (DataIntegrityViolationException e){
            if(e instanceof DuplicateKeyException dupEx){
                throw new ResourceDuplicatedException("Username or UUID already exists", dupEx);
            }
            throw new ConstraintViolationDatabaseException("Constraint violation", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByUuid(UUID uuid) {
        User userDb = userRepository.findById(uuid).orElseThrow(() -> new ResourceNotFoundException("User with UUID " + uuid + " not found"));
        return userMapper.toDto(userDb);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDto update(UUID uuid, UserUpdateDto userDto) {
        User userDb = userRepository.findById(uuid).orElseThrow(() -> new ResourceNotFoundException("User with UUID " + uuid + " not found"));

        userMapper.updateFromDto(userDto, userDb);

        try {
            User updatedUser = userRepository.save(userDb);
            return userMapper.toDto(updatedUser);
        }catch (DataIntegrityViolationException e){
            if(e instanceof DuplicateKeyException dupEx){
                throw new ResourceDuplicatedException("Username already exists", e);
            }
            throw new ConstraintViolationDatabaseException("Constraint violation", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(UUID uuid) {
        userRepository.findById(uuid).orElseThrow(() -> new ResourceNotFoundException("User with UUID " + uuid + " not found"));
        userRepository.deleteById(uuid);
    }

}
