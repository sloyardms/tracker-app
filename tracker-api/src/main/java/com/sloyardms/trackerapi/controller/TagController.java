package com.sloyardms.trackerapi.controller;

import com.sloyardms.trackerapi.dto.TagCreateDto;
import com.sloyardms.trackerapi.dto.TagDto;
import com.sloyardms.trackerapi.dto.TagUpdateDto;
import com.sloyardms.trackerapi.security.AuthUtils;
import com.sloyardms.trackerapi.service.interfaces.TagService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<TagDto> getById(@PathVariable UUID uuid){
        return ResponseEntity.ok(tagService.getById(uuid));
    }

    @GetMapping("/user/{userUuid}")
    public ResponseEntity<Page<TagDto>> getAllByUserUuid(
            @PathVariable UUID userUuid,
            @PageableDefault(size = 10, sort = "name") Pageable pageable){
        Page<TagDto> tags = tagService.getAllByUserUuid(userUuid, pageable);
        return ResponseEntity.ok(tags);
    }

    @PostMapping
    public ResponseEntity<TagDto> create(@Valid @RequestBody TagCreateDto tagCreateDto){
        UUID userUuid = AuthUtils.getCurrentUserId();
        TagDto createdTag = tagService.create(userUuid, tagCreateDto);
        URI location = URI.create("/api/v1/tags/" + createdTag.getUuid());
        return ResponseEntity.created(location).body(createdTag);
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<TagDto> update(@PathVariable UUID uuid, @RequestBody TagUpdateDto tagUpdateDto){
        return ResponseEntity.ok(tagService.update(uuid, tagUpdateDto));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid){
        tagService.delete(uuid);
        return ResponseEntity.noContent().build();
    }

}
