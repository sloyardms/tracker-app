package com.sloyardms.trackerapi.bookmark;

import com.sloyardms.trackerapi.bookmark.dto.BookmarkCreateDto;
import com.sloyardms.trackerapi.bookmark.dto.BookmarkDto;
import com.sloyardms.trackerapi.bookmark.dto.BookmarkUpdateDto;
import com.sloyardms.trackerapi.note.dto.NoteCreateDto;
import com.sloyardms.trackerapi.note.dto.NoteDto;
import com.sloyardms.trackerapi.security.AuthUtils;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<BookmarkDto> getById(@PathVariable UUID uuid){
        return ResponseEntity.ok(bookmarkService.getById(uuid));
    }

    @GetMapping("/user/{userUuid}")
    public ResponseEntity<Page<BookmarkDto>> getAllByUserUuid(
            @PathVariable UUID userUuid,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<BookmarkDto> bookmarks = bookmarkService.getAllBookmarksByUserUuid(userUuid, pageable);
        return ResponseEntity.ok(bookmarks);
    }

    @PostMapping
    public ResponseEntity<BookmarkDto> create(@Valid @RequestBody BookmarkCreateDto bookmarkCreateDto){
        UUID userUuid = AuthUtils.getCurrentUserId();
        BookmarkDto createdBookmark = bookmarkService.create(userUuid, bookmarkCreateDto);
        URI location = URI.create("/api/v1/bookmark/" + createdBookmark.getUuid());
        return ResponseEntity.created(location).body(createdBookmark);
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<BookmarkDto> update(@PathVariable UUID uuid, @RequestBody BookmarkUpdateDto bookmarkUpdateDto){
        return ResponseEntity.ok(bookmarkService.update(uuid, bookmarkUpdateDto));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid){
        bookmarkService.delete(uuid);
        return ResponseEntity.noContent().build();
    }

    // NOTE ENDPOINTS
    @PostMapping("/{uuid}/notes")
    public ResponseEntity<NoteDto> createNote(@PathVariable UUID uuid, @Valid @RequestBody NoteCreateDto noteCreateDto){
        NoteDto savedNote = bookmarkService.createNote(uuid, noteCreateDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{noteUuid}")
                .buildAndExpand(savedNote.getUuid())
                .toUri();
        return ResponseEntity.created(location).body(savedNote);
    }

    @GetMapping("/{uuid}/notes")
    public ResponseEntity<Page<NoteDto>> getAllNotesByBookmarkUuid(
            @PathVariable UUID uuid,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<NoteDto> notes = bookmarkService.getAllNotesByBookmarkUuid(uuid, pageable);
        return ResponseEntity.ok(notes);
    }

}
