package com.sloyardms.trackerapi.user;

import com.sloyardms.trackerapi.user.dto.UserCreateDto;
import com.sloyardms.trackerapi.user.dto.UserDto;
import com.sloyardms.trackerapi.user.dto.UserUpdateDto;

import java.util.UUID;

public interface UserService {

    UserDto create(UserCreateDto userDto);

    UserDto findByUuid(UUID uuid);

    UserDto update(UUID uuid, UserUpdateDto userDto);

    void delete(UUID uuid);

}
