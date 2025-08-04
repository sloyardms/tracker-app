package com.sloyardms.trackerapi.repository;

import com.sloyardms.trackerapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

}
