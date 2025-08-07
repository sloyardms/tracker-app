package com.sloyardms.trackerapi.tag;

import com.sloyardms.trackerapi.tag.dto.TagCreateDto;
import com.sloyardms.trackerapi.tag.dto.TagDto;
import com.sloyardms.trackerapi.tag.dto.TagUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TagService {

    TagDto create(UUID userUuid, TagCreateDto tagCreateDto);

    TagDto getById(UUID uuid);

    Page<TagDto> getAllByUserUuid(UUID userUuid, Pageable pageable);

    TagDto update(UUID uuid, TagUpdateDto tagUpdateDto);

    void delete(UUID uuid);

}
