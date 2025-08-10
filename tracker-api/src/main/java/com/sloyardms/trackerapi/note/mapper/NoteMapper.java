package com.sloyardms.trackerapi.note.mapper;

import com.sloyardms.trackerapi.note.dto.NoteCreateDto;
import com.sloyardms.trackerapi.note.dto.NoteDto;
import com.sloyardms.trackerapi.note.dto.NoteUpdateDto;
import com.sloyardms.trackerapi.note.entity.Note;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    NoteDto toDto(Note note);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "bookmark", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Note toEntity(NoteCreateDto noteCreateDto);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "bookmark", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(NoteUpdateDto noteUpdateDto, @MappingTarget Note note);

}
