package com.sloyardms.trackerapi.user.entity;

import com.sloyardms.trackerapi.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "users_username_unique", columnNames = {"username"})
})
public class User extends Auditable {

    @Id
    @Column(name = "user_uuid", nullable = false, updatable = false)
    private UUID uuid;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "dark_mode", nullable = false)
    private Boolean darkMode = false;

    @Column(name = "bookmark_keep_original_image", nullable = false)
    private Boolean keepOriginalImage = false;

}
