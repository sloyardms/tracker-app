package com.sloyardms.trackerapi.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO class used for creating a user from an OIDC provider
 * <p>
 * This DTO is typically populated with user information received after successful
 * authentication via OpenID Connect (OIDC) identity provider.
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

    @NotNull
    private UUID uuid;

    @NotBlank
    private String username;

    private Boolean darkMode = false;
    private Boolean keepOriginalImage = false;

}
