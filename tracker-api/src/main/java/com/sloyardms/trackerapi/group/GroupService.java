package com.sloyardms.trackerapi.group;

import com.sloyardms.trackerapi.group.dto.GroupCreateDto;
import com.sloyardms.trackerapi.group.dto.GroupDto;
import com.sloyardms.trackerapi.group.dto.GroupUpdateDto;
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
