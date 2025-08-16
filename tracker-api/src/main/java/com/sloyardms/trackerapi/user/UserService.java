package com.sloyardms.trackerapi.user;

import com.sloyardms.trackerapi.user.dto.UserCreateDto;
import com.sloyardms.trackerapi.user.dto.UserDto;
import com.sloyardms.trackerapi.user.dto.UserUpdateDto;
import com.sloyardms.trackerapi.user.entity.User;
import com.sloyardms.trackerapi.user.exception.UserIdAlreadyExistsException;
import com.sloyardms.trackerapi.user.exception.UserNotFoundException;
import com.sloyardms.trackerapi.user.exception.UsernameAlreadyExistsException;
import com.sloyardms.trackerapi.user.mapper.UserMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public UserDto create(UserCreateDto userDto) {
        if(userRepository.existsById(userDto.getUuid())){
            throw new UserIdAlreadyExistsException(userDto.getUuid());
        }
        User newUser = userMapper.toEntity(userDto);
        User savedUser  = saveUserChanges(newUser);
        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDto findByUuid(UUID uuid) {
        User userDb = userRepository.findById(uuid).orElseThrow(() -> new UserNotFoundException(uuid));
        return userMapper.toDto(userDb);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserDto update(UUID uuid, UserUpdateDto userDto) {
        User userDb = userRepository.findById(uuid).orElseThrow(() -> new UserNotFoundException(uuid));
        userMapper.updateFromDto(userDto, userDb);
        User savedUser =  saveUserChanges(userDb);
        return userMapper.toDto(savedUser);
    }

    private User saveUserChanges(User user) throws UsernameAlreadyExistsException{
        try {
            return userRepository.saveAndFlush(user);
        }catch (DataIntegrityViolationException e){
            if(e.getMessage().contains("users_username_unique")){
                throw new UsernameAlreadyExistsException(user.getUsername(), e);
            }
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(UUID uuid) {
        userRepository.findById(uuid).orElseThrow(() -> new UserNotFoundException(uuid));
        userRepository.deleteById(uuid);
    }

}
