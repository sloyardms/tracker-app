package com.sloyardms.trackerapi.group;

import com.sloyardms.trackerapi.group.dto.GroupCreateDto;
import com.sloyardms.trackerapi.group.dto.GroupDto;
import com.sloyardms.trackerapi.group.dto.GroupUpdateDto;
import com.sloyardms.trackerapi.group.entity.Group;
import com.sloyardms.trackerapi.user.entity.User;
import com.sloyardms.trackerapi.common.exception.ResourceDuplicatedException;
import com.sloyardms.trackerapi.common.exception.ResourceNotFoundException;
import com.sloyardms.trackerapi.group.mapper.GroupMapper;
import com.sloyardms.trackerapi.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository, GroupMapper groupMapper, UserRepository userRepository){
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.userRepository = userRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public GroupDto create(UUID userUuid, GroupCreateDto groupCreateDto) {
        User groupUser = userRepository.findById(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User with UUID " + userUuid + " not found"));

        if(groupRepository.findByUserUuidAndName(userUuid, groupCreateDto.getName()).isPresent()){
            throw new ResourceDuplicatedException("Group with name '" + groupCreateDto.getName() + "' already exists for user " + userUuid);
        }

        Group group = groupMapper.toEntity(groupCreateDto);

        group.setUuid(UUID.randomUUID());
        group.setUser(groupUser);

        Group savedGroup = groupRepository.save(group);
        return groupMapper.toDto(savedGroup);
    }

    @Transactional(readOnly = true)
    public GroupDto getById(UUID uuid) {
        Group groupDb = groupRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Group with UUID " + uuid + " not found"));
        return groupMapper.toDto(groupDb);
    }

    @Transactional(readOnly = true)
    public Page<GroupDto> getAllByUserUuid(UUID userUuid, Pageable pageable) {
        Page<Group> groups = groupRepository.findAllByUserUuid(userUuid, pageable);
        return groups.map(groupMapper::toDto);
    }

    @Transactional(rollbackFor = Exception.class)
    public GroupDto update(UUID groupUuid, GroupUpdateDto groupUpdateDto) {
        Group groupDb = groupRepository.findById(groupUuid)
                .orElseThrow(() -> new ResourceDuplicatedException("Group with UUID " + groupUuid + " not found"));

        if(groupUpdateDto.getName()!=null){
            boolean nameExists = groupRepository.existsByUserUuidAndNameAndUuidNot(groupDb.getUserUuid(), groupUpdateDto.getName(), groupUuid);
            if(nameExists){
                throw new ResourceDuplicatedException("Group name already exists");
            }
        }

        groupMapper.updateFromDto(groupUpdateDto, groupDb);

        Group updatedGroup = groupRepository.save(groupDb);

        return groupMapper.toDto(updatedGroup);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(UUID uuid) {
        groupRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Group with UUID " + uuid + " not found"));
        groupRepository.deleteById(uuid);
    }
}
