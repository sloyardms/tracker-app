package com.sloyardms.trackerapi.service.interfaces;

import com.sloyardms.trackerapi.entity.User;

import java.util.UUID;

public interface UserService {

    User create(User user);

    User findByUuid(UUID uuid);

    User update(User user);

    void delete(UUID uuid);

}
