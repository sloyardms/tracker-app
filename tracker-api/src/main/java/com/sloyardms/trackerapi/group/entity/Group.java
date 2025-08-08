package com.sloyardms.trackerapi.group.entity;

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
@Table(name = "groups", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_uuid", "name"})
})
public class Group extends Auditable {

    @Id
    @Column(name = "group_uuid", nullable = false, updatable = false)
    private UUID uuid;

    @Column(name = "user_uuid", nullable = false)
    private UUID userUuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

}
