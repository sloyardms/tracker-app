package com.sloyardms.trackerapi.user.mapper;

import com.sloyardms.trackerapi.user.dto.UserCreateDto;
import com.sloyardms.trackerapi.user.dto.UserDto;
import com.sloyardms.trackerapi.user.dto.UserUpdateDto;
import com.sloyardms.trackerapi.user.entity.User;
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
