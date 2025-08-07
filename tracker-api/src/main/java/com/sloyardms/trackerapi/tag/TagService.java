package com.sloyardms.trackerapi.tag;

import com.sloyardms.trackerapi.tag.dto.TagCreateDto;
import com.sloyardms.trackerapi.tag.dto.TagDto;
import com.sloyardms.trackerapi.tag.dto.TagUpdateDto;
import com.sloyardms.trackerapi.tag.entity.Tag;
import com.sloyardms.trackerapi.tag.exception.TagNameAlreadyExistsException;
import com.sloyardms.trackerapi.tag.exception.TagNotFoundException;
import com.sloyardms.trackerapi.user.entity.User;
import com.sloyardms.trackerapi.tag.mapper.TagMapper;
import com.sloyardms.trackerapi.user.UserRepository;
import com.sloyardms.trackerapi.user.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final TagMapper tagMapper;

    public TagService(TagRepository tagRepository, TagMapper tagMapper, UserRepository userRepository) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
        this.userRepository = userRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public TagDto create(UUID userUuid, TagCreateDto tagCreateDto) {
        User groupUser = userRepository.findById(userUuid).orElseThrow(() -> new UserNotFoundException(userUuid));

        Tag tag = tagMapper.toEntity(tagCreateDto);
        tag.setUser(groupUser);
        tag.setUuid(UUID.randomUUID());

        Tag savedTag = saveTagChanges(tag);
        return tagMapper.toDto(savedTag);
    }

    @Transactional(readOnly = true)
    public TagDto getById(UUID uuid) {
        Tag tagDb = tagRepository.findById(uuid).orElseThrow(() -> new TagNotFoundException(uuid));
        return tagMapper.toDto(tagDb);
    }

    @Transactional(readOnly = true)
    public Page<TagDto> getAllByUserUuid(UUID userUuid, Pageable pageable) {
        Page<Tag> tags = tagRepository.findAllByUserUuid(userUuid, pageable);
        return tags.map(tagMapper::toDto);
    }

    @Transactional(rollbackFor = Exception.class)
    public TagDto update(UUID uuid, TagUpdateDto tagUpdateDto) {
       Tag tagDb = tagRepository.findById(uuid).orElseThrow(() -> new TagNotFoundException(uuid));
        tagMapper.updateFromDto(tagUpdateDto, tagDb);
        Tag savedTag = saveTagChanges(tagDb);
        return tagMapper.toDto(savedTag);
    }

    private Tag saveTagChanges(Tag tag) throws TagNameAlreadyExistsException{
        try {
            return tagRepository.saveAndFlush(tag);
        }catch (org.springframework.dao.DataIntegrityViolationException e){
            if(e.getMessage().contains("tags_user_uuid_name_unique")){
                throw new TagNameAlreadyExistsException(tag.getName());
            }
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(UUID uuid) {
        tagRepository.findById(uuid).orElseThrow(() -> new TagNotFoundException(uuid));
        tagRepository.deleteById(uuid);
    }
}
