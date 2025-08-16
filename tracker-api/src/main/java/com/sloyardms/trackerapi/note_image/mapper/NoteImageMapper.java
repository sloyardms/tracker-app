package com.sloyardms.trackerapi.note_image.mapper;

import com.sloyardms.trackerapi.note_image.dto.NoteImageDto;
import com.sloyardms.trackerapi.note_image.entity.NoteImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoteImageMapper {

    NoteImageDto toDto(NoteImage noteImage);

}
