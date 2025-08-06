package com.sloyardms.trackerapi.service.interfaces;

import com.sloyardms.trackerapi.dto.GroupCreateDto;
import com.sloyardms.trackerapi.dto.GroupDto;
import com.sloyardms.trackerapi.dto.GroupUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GroupService {

    GroupDto create(UUID userUuid, GroupCreateDto groupCreateDto);

    GroupDto getById(UUID uuid);

    Page<GroupDto> getAllByUserUuid(UUID userUuid, Pageable pageable);

    GroupDto update(UUID groupUuid, GroupUpdateDto GroupUpdateDto);

    void delete(UUID uuid);

}
