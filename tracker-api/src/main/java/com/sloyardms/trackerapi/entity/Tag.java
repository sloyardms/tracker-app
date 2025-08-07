package com.sloyardms.trackerapi.entity;

import com.sloyardms.trackerapi.user.entity.User;
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
@Table(name = "tags", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_uuid", "name"})
})
public class Tag extends Auditable {

    @Id
    @Column(name = "tag_uuid", nullable = false, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_uuid", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    // Read only FK to avoid fetching the user on every query
    @Column(name = "user_uuid", nullable = false, insertable = false, updatable = false)
    private UUID userUuid;

}
