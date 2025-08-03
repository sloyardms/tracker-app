package com.sloyardms.trackerapi.service.interfaces;

import com.sloyardms.trackerapi.dto.UserCreateDto;
import com.sloyardms.trackerapi.dto.UserDto;
import com.sloyardms.trackerapi.dto.UserUpdateDto;
import com.sloyardms.trackerapi.entity.User;

import java.util.UUID;

public interface UserService {

    UserDto create(UserCreateDto userDto);

    UserDto findByUuid(UUID uuid);

    UserDto update(UUID uuid, UserUpdateDto userDto);

    void delete(UUID uuid);

}
