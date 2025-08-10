package com.sloyardms.trackerapi.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO class used for updating user details
 * <p>
 * This DTO is typically used in  partial update operations (PATCH request) to
 * modify one or more user settings
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    @NotBlank
    private String username;
    private Boolean darkMode;
    private Boolean keepOriginalImage;

}
