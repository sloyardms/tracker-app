package com.sloyardms.trackerapi.user;

import com.sloyardms.trackerapi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

}
