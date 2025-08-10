package com.sloyardms.trackerapi.note;

import com.sloyardms.trackerapi.note.dto.NoteDto;
import com.sloyardms.trackerapi.note.dto.NoteUpdateDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<NoteDto> getById(@PathVariable UUID uuid){
        return ResponseEntity.ok(noteService.getById(uuid));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<NoteDto> update(@PathVariable UUID uuid, @Valid @RequestBody NoteUpdateDto noteUpdateDto){
        return ResponseEntity.ok(noteService.update(uuid, noteUpdateDto));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid){
        noteService.delete(uuid);
        return ResponseEntity.noContent().build();
    }

}
