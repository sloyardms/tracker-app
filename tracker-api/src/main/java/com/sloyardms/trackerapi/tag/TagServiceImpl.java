package com.sloyardms.trackerapi.tag;

import com.sloyardms.trackerapi.tag.dto.TagCreateDto;
import com.sloyardms.trackerapi.tag.dto.TagDto;
import com.sloyardms.trackerapi.tag.dto.TagUpdateDto;
import com.sloyardms.trackerapi.tag.entity.Tag;
import com.sloyardms.trackerapi.user.entity.User;
import com.sloyardms.trackerapi.exception.ResourceDuplicatedException;
import com.sloyardms.trackerapi.exception.ResourceNotFoundException;
import com.sloyardms.trackerapi.tag.mapper.TagMapper;
import com.sloyardms.trackerapi.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final TagMapper tagMapper;

    public TagServiceImpl(TagRepository tagRepository, TagMapper tagMapper, UserRepository userRepository) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TagDto create(UUID userUuid, TagCreateDto tagCreateDto) {
        User groupUser = userRepository.findById(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User with UUID " + userUuid + " not found"));

        if(tagRepository.findByUserUuidAndName(userUuid, tagCreateDto.getName()).isPresent()){
            throw new ResourceDuplicatedException("Tag with name '" + tagCreateDto.getName() + "' already exists for user " + groupUser.getUuid());
        }

        Tag tag = tagMapper.toEntity(tagCreateDto);

        tag.setUuid(UUID.randomUUID());
        tag.setUser(groupUser);

        Tag savedTag = tagRepository.save(tag);
        return tagMapper.toDto(savedTag);
    }

    @Override
    @Transactional(readOnly = true)
    public TagDto getById(UUID uuid) {
        Tag tagDb = tagRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with UUID " + uuid + " not found"));
        return tagMapper.toDto(tagDb);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagDto> getAllByUserUuid(UUID userUuid, Pageable pageable) {
        Page<Tag> tags = tagRepository.findAllByUserUuid(userUuid, pageable);
        return tags.map(tagMapper::toDto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TagDto update(UUID uuid, TagUpdateDto tagUpdateDto) {
       Tag tagDb = tagRepository.findById(uuid)
               .orElseThrow(() -> new ResourceNotFoundException("Tag with UUID " + uuid + " not found"));

       if(tagUpdateDto.getName()!=null){
           boolean nameExists = tagRepository.existsByUserUuidAndNameAndUuidNot(tagDb.getUserUuid(), tagUpdateDto.getName(), uuid);
           if(nameExists){
               throw new ResourceDuplicatedException("Tag name already exists");
           }
       }

       tagMapper.updateFromDto(tagUpdateDto, tagDb);

       Tag updatedTag = tagRepository.save(tagDb);

       return tagMapper.toDto(updatedTag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(UUID uuid) {
        tagRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with UUID " + uuid + " not found"));
        tagRepository.deleteById(uuid);
    }
}
