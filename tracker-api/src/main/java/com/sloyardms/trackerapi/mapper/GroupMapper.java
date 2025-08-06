package com.sloyardms.trackerapi.mapper;

import com.sloyardms.trackerapi.dto.GroupCreateDto;
import com.sloyardms.trackerapi.dto.GroupDto;
import com.sloyardms.trackerapi.dto.GroupUpdateDto;
import com.sloyardms.trackerapi.entity.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    GroupDto toDto(Group group);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "userUuid", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "user", ignore = true)
    Group toEntity(GroupCreateDto groupCreateDto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "userUuid", ignore = true)
    void updateFromDto(GroupUpdateDto groupUpdateDto, @MappingTarget Group group);

}
