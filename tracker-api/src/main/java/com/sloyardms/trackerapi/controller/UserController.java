package com.sloyardms.trackerapi.controller;

import com.sloyardms.trackerapi.entity.User;
import com.sloyardms.trackerapi.service.interfaces.UserService;
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
    public ResponseEntity<User> getById(String uuid){
        User user = userService.findByUuid(UUID.fromString(uuid));
        if(user != null) {
            return ResponseEntity.ok(user);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<User> update(@PathVariable UUID uuid, @RequestBody User user){
        User updatedUser = userService.update(user);
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
