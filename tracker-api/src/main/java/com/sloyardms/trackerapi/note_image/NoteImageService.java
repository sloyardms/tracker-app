package com.sloyardms.trackerapi.note_image;

import com.sloyardms.trackerapi.common.enums.ImageType;
import com.sloyardms.trackerapi.common.exception.ImageDeletionException;
import com.sloyardms.trackerapi.common.service.ImageStorageService;
import com.sloyardms.trackerapi.note.NoteRepository;
import com.sloyardms.trackerapi.note_image.dto.NoteImageDto;
import com.sloyardms.trackerapi.note_image.entity.NoteImage;
import com.sloyardms.trackerapi.note_image.exception.NoteImageNotFoundException;
import com.sloyardms.trackerapi.note_image.mapper.NoteImageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class NoteImageService {

    private static final Logger log = LoggerFactory.getLogger(NoteImageService.class);

    private final ImageStorageService imageStorageService;
    private final NoteImageRepository noteImageRepository;
    private final NoteImageMapper noteImageMapper;
    private final NoteRepository noteRepository;

    public NoteImageService(NoteImageRepository noteImageRepository, ImageStorageService imageStorageService, NoteImageMapper noteImageMapper, NoteRepository noteRepository) {
        this.noteImageRepository = noteImageRepository;
        this.imageStorageService = imageStorageService;
        this.noteImageMapper = noteImageMapper;
        this.noteRepository = noteRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public NoteImageDto create(UUID userUuid, UUID bookmarkUuid, UUID noteUuid, MultipartFile imageFile) throws Exception{
        NoteImage noteImage = new NoteImage();
        noteImage.setUuid(UUID.randomUUID());
        noteImage.setNote(noteRepository.getReferenceById(noteUuid));

        // Generate thumbnail data
        String originalFilename = Optional.ofNullable(imageFile.getOriginalFilename())
                .orElseThrow(() -> new IllegalArgumentException("File name is missing"));

        noteImage.setOriginalImagePath(imageStorageService.generateImagePath(ImageType.ORIGINAL_IMAGE, userUuid, bookmarkUuid, noteUuid, noteImage.getUuid(), imageFile));
        noteImage.setThumbnailPath(imageStorageService.generateImagePath(ImageType.THUMBNAIL, userUuid, bookmarkUuid, noteUuid, noteImage.getUuid(), imageFile));
        noteImage.setOriginalImageMimeType(imageFile.getContentType());
        noteImage.setThumbnailMimeType(imageStorageService.getThumbnailMimeTypeFromExtension(originalFilename));

        imageStorageService.storeNoteImages(noteUuid, noteImage, imageFile, originalFilename);

        // Save the note image
        NoteImage savedNote = noteImageRepository.save(noteImage);

        return noteImageMapper.toDto(savedNote);
    }

    @Transactional(readOnly = true)
    public NoteImageDto getById(UUID uuid) {
        NoteImage noteImage = noteImageRepository.findById(uuid).orElseThrow(()-> new NoteImageNotFoundException(uuid));
        return noteImageMapper.toDto(noteImage);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(UUID uuid){
        NoteImage noteImage = noteImageRepository.findById(uuid).orElseThrow(() -> new NoteImageNotFoundException(uuid));
        String originalImagePath = noteImage.getOriginalImagePath();
        String thumbnailPath = noteImage.getThumbnailPath();

        //First delete the image files
        String currentImage = originalImagePath;
        try {
            imageStorageService.removeFile(originalImagePath);
            currentImage = thumbnailPath;
            imageStorageService.removeFile(thumbnailPath);
        } catch (IOException e) {
            log.error("Failed to delete image file '{}'", currentImage, e);
            throw new ImageDeletionException(e);
        }

        //Delete the db record
        noteImageRepository.deleteById(uuid);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByNoteUuid(UUID noteUuid){
        noteImageRepository.deleteAllByNoteUuid(noteUuid);
    }


}
