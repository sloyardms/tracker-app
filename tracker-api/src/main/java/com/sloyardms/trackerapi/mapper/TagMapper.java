package com.sloyardms.trackerapi.mapper;

import com.sloyardms.trackerapi.dto.TagCreateDto;
import com.sloyardms.trackerapi.dto.TagDto;
import com.sloyardms.trackerapi.dto.TagUpdateDto;
import com.sloyardms.trackerapi.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagDto toDto(Tag tag);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "userUuid", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "user", ignore = true)
    Tag toEntity(TagCreateDto tagCreateDto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "userUuid", ignore = true)
    void updateFromDto(TagUpdateDto tagUpdateDto, @MappingTarget Tag tag);

}
