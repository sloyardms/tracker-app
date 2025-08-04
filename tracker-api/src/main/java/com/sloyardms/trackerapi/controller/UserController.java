package com.sloyardms.trackerapi.controller;

import com.sloyardms.trackerapi.dto.UserCreateDto;
import com.sloyardms.trackerapi.dto.UserDto;
import com.sloyardms.trackerapi.dto.UserUpdateDto;
import com.sloyardms.trackerapi.service.interfaces.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<UserDto> getById(@PathVariable UUID uuid){
        return ResponseEntity.ok(userService.findByUuid(uuid));
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserCreateDto userDto){
        UserDto createdUser = userService.create(userDto);
        URI location = URI.create("/api/v1/users/" + createdUser.getUuid());
        return ResponseEntity.created(location).body(createdUser);
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<UserDto> update(@PathVariable UUID uuid, @RequestBody UserUpdateDto userDto){
        return ResponseEntity.ok(userService.update(uuid, userDto));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid){
        userService.delete(uuid);
        return ResponseEntity.noContent().build();
    }

}
