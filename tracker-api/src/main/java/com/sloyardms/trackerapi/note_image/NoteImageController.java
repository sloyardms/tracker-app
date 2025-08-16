package com.sloyardms.trackerapi.note_image;

import com.sloyardms.trackerapi.note_image.dto.NoteImageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/note-images")
public class NoteImageController {

    private final NoteImageService noteImageService;

    public NoteImageController(NoteImageService noteImageService) {
        this.noteImageService = noteImageService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<NoteImageDto> getById(@PathVariable UUID uuid){
        return ResponseEntity.ok(noteImageService.getById(uuid));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid){
        noteImageService.delete(uuid);
        return ResponseEntity.noContent().build();
    }

}
