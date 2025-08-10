package com.sloyardms.trackerapi.bookmark;

import com.sloyardms.trackerapi.bookmark.dto.BookmarkCreateDto;
import com.sloyardms.trackerapi.bookmark.dto.BookmarkDto;
import com.sloyardms.trackerapi.bookmark.dto.BookmarkUpdateDto;
import com.sloyardms.trackerapi.bookmark.entity.Bookmark;
import com.sloyardms.trackerapi.bookmark.exception.BookmarkNotFoundException;
import com.sloyardms.trackerapi.bookmark.exception.BookmarkTitleAlreadyExistsException;
import com.sloyardms.trackerapi.bookmark.exception.BookmarkUrlAlreadyExistsException;
import com.sloyardms.trackerapi.bookmark.mapper.BookmarkMapper;
import com.sloyardms.trackerapi.group.GroupRepository;
import com.sloyardms.trackerapi.group.entity.Group;
import com.sloyardms.trackerapi.group.exception.GroupNotFoundException;
import com.sloyardms.trackerapi.note.NoteService;
import com.sloyardms.trackerapi.note.dto.NoteCreateDto;
import com.sloyardms.trackerapi.note.dto.NoteDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BookmarkService {

    //Services
    private final NoteService noteService;

    //Repositories
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkMapper bookmarkMapper;
    private final GroupRepository groupRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository, BookmarkMapper bookmarkMapper, GroupRepository groupRepository, NoteService bookmarkService) {
        this.bookmarkRepository = bookmarkRepository;
        this.bookmarkMapper = bookmarkMapper;
        this.groupRepository = groupRepository;
        this.noteService = bookmarkService;
    }

    @Transactional(rollbackFor = Exception.class)
    public BookmarkDto create(UUID userUuid, BookmarkCreateDto bookmarkCreateDto) {
        Bookmark newBookmark = new Bookmark();

        //Set base attributes
        newBookmark.setUuid(UUID.randomUUID());
        newBookmark.setUserUuid(userUuid);
        newBookmark.setTitle(bookmarkCreateDto.getTitle());
        newBookmark.setUrl(bookmarkCreateDto.getUrl());
        newBookmark.setDescription(bookmarkCreateDto.getDescription());
        newBookmark.setFavorite(bookmarkCreateDto.isFavorite());

        //Set group
        assignGroupToBookmark(newBookmark, bookmarkCreateDto.getGroupUuid());

        Bookmark savedBookmark = saveBookmarkChanges(newBookmark);
        return bookmarkMapper.toDto(savedBookmark);
    }

    @Transactional(readOnly = true)
    public BookmarkDto getById(UUID uuid) {
        Bookmark bookmarkDb = bookmarkRepository.findById(uuid).orElseThrow(() -> new BookmarkNotFoundException(uuid));
        return bookmarkMapper.toDto(bookmarkDb);
    }

    @Transactional(readOnly = true)
    public Page<BookmarkDto> getAllBookmarksByUserUuid(UUID userUuid, Pageable pageable) {
        Page<Bookmark> bookmarks = bookmarkRepository.findAllByUserUuid(userUuid, pageable);
        return bookmarks.map(bookmarkMapper::toDto);
    }

    @Transactional(rollbackFor = Exception.class)
    public BookmarkDto update(UUID uuid, BookmarkUpdateDto bookmarkUpdateDto) {
        Bookmark bookmarkDb = bookmarkRepository.findById(uuid).orElseThrow(()-> new BookmarkNotFoundException(uuid));

        //Set base attributes to update if any
        bookmarkMapper.updateFromDto(bookmarkUpdateDto, bookmarkDb);

        //Set group
        assignGroupToBookmark(bookmarkDb, bookmarkUpdateDto.getGroupUuid());

        Bookmark savedBookmark = saveBookmarkChanges(bookmarkDb);
        return bookmarkMapper.toDto(savedBookmark);
    }

    private Bookmark saveBookmarkChanges(Bookmark bookmark){
        try {
            return bookmarkRepository.saveAndFlush(bookmark);
        }catch (DataIntegrityViolationException e){
            if(e.getMessage().contains("bookmarks_user_uuid_title_unique")){
                throw new BookmarkTitleAlreadyExistsException(bookmark.getTitle());
            }else if(e.getMessage().contains("bookmarks_user_uuid_url_unique")){
                throw new BookmarkUrlAlreadyExistsException(bookmark.getUrl());
            }
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(UUID uuid){
        bookmarkRepository.findById(uuid).orElseThrow(() -> new BookmarkNotFoundException(uuid));
        bookmarkRepository.deleteById(uuid);
    }

    /**
     * Assigns a Group to the given Bookmark based on the provided group UUID.
     * <p>
     *     If the {@code groupUuid} is not {@code null}, this method attempts to find
     *     the corresponding Group entity from the repository. If the Group is found,
     *     it is assigned to the Bookmark. Otherwise, an exception is thrown.
     * </p>
     * @param bookmark the Bookmark object to which the Group will be assigned
     * @param groupUuid the UUID of the Group to assign to the Bookmark; may be {@code null}
     * @throws GroupNotFoundException if no Group with the given UUID is found
     */
    private void assignGroupToBookmark(Bookmark bookmark, UUID groupUuid){
        if(groupUuid != null){
            Group foundGroup = groupRepository.findById(groupUuid)
                    .orElseThrow(()-> new GroupNotFoundException(groupUuid));
            bookmark.setGroup(foundGroup);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public NoteDto createNote(UUID bookmarkUuid, NoteCreateDto noteCreateDto) {
        //Verify bookmark exists
        if(!bookmarkRepository.existsById(bookmarkUuid)){
            throw new BookmarkNotFoundException(bookmarkUuid);
        }

        //Pass the request to NoteService
        return noteService.create(bookmarkUuid, noteCreateDto);
    }

    @Transactional(readOnly = true)
    public Page<NoteDto> getAllNotesByBookmarkUuid(UUID bookmarkUuid, Pageable pageable) {
        //Verify bookmark exists
        if(!bookmarkRepository.existsById(bookmarkUuid)){
            throw new BookmarkNotFoundException(bookmarkUuid);
        }

        return noteService.getAllNotesByBookmarkUuid(bookmarkUuid, pageable);
    }

}
