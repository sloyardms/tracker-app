package com.sloyardms.trackerapi.service.interfaces;

import com.sloyardms.trackerapi.dto.TagCreateDto;
import com.sloyardms.trackerapi.dto.TagDto;
import com.sloyardms.trackerapi.dto.TagUpdateDto;
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
