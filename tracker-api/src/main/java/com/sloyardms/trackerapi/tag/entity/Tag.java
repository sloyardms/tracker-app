package com.sloyardms.trackerapi.tag.entity;

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
@Table(name = "tags",
        uniqueConstraints = {
                @UniqueConstraint(name = "tags_user_uuid_name_unique", columnNames = {"user_uuid", "name"})
        },
        indexes = {
                @Index(name = "tags_user_uuid_index", columnList = "user_uuid")
        })
public class Tag extends Auditable {

    @Id
    @Column(name = "tag_uuid", nullable = false, updatable = false)
    private UUID uuid;

    @Column(name = "user_uuid", nullable = false)
    private UUID userUuid;

    @Column(name = "name", nullable = false)
    private String name;

}
