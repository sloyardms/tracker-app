package com.sloyardms.trackerapi.user.entity;

import com.sloyardms.trackerapi.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends Auditable {

    @Id
    @Column(name = "user_uuid", nullable = false, updatable = false)
    private UUID uuid;

    @Column(name = "username", nullable = false, unique = true, length = 255)
    private String username;

    @Column(name = "dark_mode", nullable = false)
    private Boolean darkMode = false;

    @Column(name = "bookmark_keep_original_image", nullable = false)
    private Boolean keepOriginalImage = false;

}
