package com.sloyardms.trackerapi.tag.entity;

import com.sloyardms.trackerapi.common.entity.Auditable;
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

    @Column(name = "user_uuid",  nullable = false)
    private UUID userUuid;

    @Column(name = "name", nullable = false)
    private String name;

}
