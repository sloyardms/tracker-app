package com.sloyardms.trackerapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDto {

    private UUID uuid;
    private UUID userUuid;
    private String name;
    private String description;

}
