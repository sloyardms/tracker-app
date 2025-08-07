package com.sloyardms.trackerapi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO class used for exposing user details to clients
 * <p>
 * This DTO is typically used in API responses to return info about a user
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private UUID uuid;
    private String username;
    private Boolean darkMode;
    private Boolean keepOriginalImage;
    private Instant createdAt;
    private Instant updatedAt;

}
