package com.sloyardms.trackerapi.controller;

import com.sloyardms.trackerapi.dto.UserCreateDto;
import com.sloyardms.trackerapi.dto.UserDto;
import com.sloyardms.trackerapi.dto.UserUpdateDto;
import com.sloyardms.trackerapi.entity.User;
import com.sloyardms.trackerapi.service.interfaces.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestControllerAdvice
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
    public ResponseEntity<UserDto> getById(String uuid){
        UserDto user = userService.findByUuid(UUID.fromString(uuid));
        if(user != null) {
            return ResponseEntity.ok(user);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserCreateDto userDto){
        UserDto createdUser = userService.create(userDto);
        if(createdUser != null) {
            return ResponseEntity.ok(createdUser);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<UserDto> update(@PathVariable UUID uuid, @RequestBody UserUpdateDto userDto){
        UserDto updatedUser = userService.update(uuid,userDto);
        if(updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{uuid}")
    public void delete(@PathVariable UUID uuid){
        userService.delete(uuid);
    }

}
