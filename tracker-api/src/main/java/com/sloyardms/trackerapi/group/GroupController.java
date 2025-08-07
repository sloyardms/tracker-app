package com.sloyardms.trackerapi.group;

import com.sloyardms.trackerapi.group.dto.GroupCreateDto;
import com.sloyardms.trackerapi.group.dto.GroupDto;
import com.sloyardms.trackerapi.group.dto.GroupUpdateDto;
import com.sloyardms.trackerapi.security.AuthUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<GroupDto> getById(@PathVariable UUID uuid){
        return ResponseEntity.ok(groupService.getById(uuid));
    }

    @GetMapping("/user/{userUuid}")
    public ResponseEntity<Page<GroupDto>> getAllByUserUuid(
            @PathVariable UUID userUuid,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<GroupDto> groups = groupService.getAllByUserUuid(userUuid, pageable);
        return ResponseEntity.ok(groups);
    }

    @PostMapping
    public ResponseEntity<GroupDto> create(@Valid @RequestBody GroupCreateDto groupCreateDto){
        UUID userUuid = AuthUtils.getCurrentUserId();
        GroupDto createdGroup = groupService.create(userUuid, groupCreateDto);
        URI location = URI.create("/api/v1/groups/" + createdGroup.getUuid());
        return ResponseEntity.created(location).body(createdGroup);
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<GroupDto> update(@PathVariable UUID uuid, @RequestBody GroupUpdateDto groupUpdateDto){
        return ResponseEntity.ok(groupService.update(uuid, groupUpdateDto));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid){
        groupService.delete(uuid);
        return ResponseEntity.noContent().build();
    }

}
