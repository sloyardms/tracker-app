package com.sloyardms.trackerapi.group;

import com.sloyardms.trackerapi.group.dto.GroupCreateDto;
import com.sloyardms.trackerapi.group.dto.GroupDto;
import com.sloyardms.trackerapi.group.dto.GroupUpdateDto;
import com.sloyardms.trackerapi.group.entity.Group;
import com.sloyardms.trackerapi.group.exception.GroupNameAlreadyExistsException;
import com.sloyardms.trackerapi.group.exception.GroupNotFoundException;
import com.sloyardms.trackerapi.group.mapper.GroupMapper;
import com.sloyardms.trackerapi.user.exception.UserNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    public GroupService(GroupRepository groupRepository, GroupMapper groupMapper){
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public GroupDto create(UUID userUuid, GroupCreateDto groupCreateDto) {
        Group newGroup = groupMapper.toEntity(groupCreateDto);
        newGroup.setUserUuid(userUuid);
        newGroup.setUuid(UUID.randomUUID());

        Group savedGroup = saveGroupChanges(newGroup);
        return groupMapper.toDto(savedGroup);
    }

    @Transactional(readOnly = true)
    public GroupDto getById(UUID uuid) {
        Group groupDb = groupRepository.findById(uuid)
                .orElseThrow(() -> new GroupNotFoundException(uuid));
        return groupMapper.toDto(groupDb);
    }

    @Transactional(readOnly = true)
    public Page<GroupDto> getAllByUserUuid(UUID userUuid, Pageable pageable) {
        Page<Group> groups = groupRepository.findAllByUserUuid(userUuid, pageable);
        return groups.map(groupMapper::toDto);
    }

    @Transactional(rollbackFor = Exception.class)
    public GroupDto update(UUID uuid, GroupUpdateDto groupUpdateDto) {
        Group groupDb = groupRepository.findById(uuid).orElseThrow(() -> new GroupNotFoundException(uuid));
        groupMapper.updateFromDto(groupUpdateDto, groupDb);
        Group savedGroup = saveGroupChanges(groupDb);
        return groupMapper.toDto(savedGroup);
    }

    private Group saveGroupChanges(Group group ) throws GroupNameAlreadyExistsException{
        try {
            return groupRepository.saveAndFlush(group);
        }catch (DataIntegrityViolationException e){
            if(e.getMessage().contains("groups_user_uuid_name_unique")){
                throw new GroupNameAlreadyExistsException(group.getName(), e);
            } else if (e.getMessage().contains("groups_user_uuid_foreign")) {
                throw new UserNotFoundException(group.getUserUuid(), e);
            }
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(UUID uuid) {
        groupRepository.findById(uuid).orElseThrow(() -> new GroupNotFoundException(uuid));
        groupRepository.deleteById(uuid);
    }
}
