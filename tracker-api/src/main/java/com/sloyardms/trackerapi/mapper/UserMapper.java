package com.sloyardms.trackerapi.mapper;

import com.sloyardms.trackerapi.dto.UserCreateDto;
import com.sloyardms.trackerapi.dto.UserDto;
import com.sloyardms.trackerapi.dto.UserUpdateDto;
import com.sloyardms.trackerapi.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserCreateDto userCreateDto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UserUpdateDto userUpdateDto, @MappingTarget User user);

}
