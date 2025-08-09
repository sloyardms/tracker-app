package com.sloyardms.trackerapi.tag.mapper;

import com.sloyardms.trackerapi.tag.dto.TagCreateDto;
import com.sloyardms.trackerapi.tag.dto.TagDto;
import com.sloyardms.trackerapi.tag.dto.TagUpdateDto;
import com.sloyardms.trackerapi.tag.entity.Tag;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagDto toDto(Tag tag);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "userUuid", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    Tag toEntity(TagCreateDto tagCreateDto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "userUuid", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(TagUpdateDto tagUpdateDto, @MappingTarget Tag tag);

}
