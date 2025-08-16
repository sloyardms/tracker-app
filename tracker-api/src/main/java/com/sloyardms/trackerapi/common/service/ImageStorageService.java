package com.sloyardms.trackerapi.common.service;

import com.sloyardms.trackerapi.common.enums.ImageType;
import com.sloyardms.trackerapi.common.exception.ImageStorageException;
import com.sloyardms.trackerapi.note_image.entity.NoteImage;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final static Logger log = LoggerFactory.getLogger(ImageStorageService.class);

    private static final Map<String, String> SUPPORTED_EXT_TO_MIME = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "webp", "image/webp"
    );

    private final String BASE_DIR;
    private final String BOOKMARKS_FOLDER = "bookmarks";
    private final String NOTES_FOLDER = "notes";

    // Thumbnail
    private final int THUMBNAIL_WIDTH;
    private final int THUMBNAIL_HEIGHT;
    private final double THUMBNAIL_QUALITY;
    private final String THUMBNAIL_RESIZE_MODE;
    private final String THUMBNAIL_FORMAT;
    private final String THUMBNAIL_PREFIX;

    // Original image
    private final double ORIGINAL_QUALITY;
    private final String ORIGINAL_PREFIX;
    private final double ORIGINAL_DEFAULT_SCALE = 1.0;

    public ImageStorageService(
            @Value("${app.storage.user-data-path}") String userDataPath, @Value("${app.image.thumbnail.width}") int thumbnailWidth,
            @Value("${app.image.thumbnail.height}") int thumbnailHeight, @Value("${app.image.thumbnail.quality}") double thumbnailQuality,
            @Value("${app.image.thumbnail.resize-mode}") String thumbnailResizeMode,
            @Value("${app.image.thumbnail.format}") String thumbnailFormat, @Value("${app.image.thumbnail.prefix}") String thumbnailPrefix,
            @Value("${app.image.original.quality}") double originalQuality, @Value("${app.image.original.prefix}") String originalPrefix
    ) {
        this.BASE_DIR = userDataPath;
        this.THUMBNAIL_WIDTH = thumbnailWidth;
        this.THUMBNAIL_HEIGHT = thumbnailHeight;
        this.THUMBNAIL_QUALITY = thumbnailQuality;
        this.THUMBNAIL_RESIZE_MODE = thumbnailResizeMode;
        this.THUMBNAIL_FORMAT = thumbnailFormat;
        this.THUMBNAIL_PREFIX = thumbnailPrefix;
        this.ORIGINAL_QUALITY = originalQuality;
        this.ORIGINAL_PREFIX = originalPrefix;

        // Make sure the thumbnail extension is supported
        if (!SUPPORTED_EXT_TO_MIME.containsKey(THUMBNAIL_FORMAT)) {
            String message = "Unsupported thumbnail file extension: " + THUMBNAIL_FORMAT;
            log.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Stores the given image file in the user's data directory as a NoteImage. Creates a thumbnail and original image file.
     *
     * @param noteUuid         UUID of the note to store the image for. Used to verify the image path is correct.
     * @param noteImage        NoteImage object to store the image for. Used to set the image paths.
     * @param imageFile        MultipartFile containing the image to store.
     * @param originalFilename Name of the original image file (the one from the request)
     * @throws Exception if either the thumbnail or the original image could not be written
     */
    public void storeNoteImages(UUID noteUuid, NoteImage noteImage, MultipartFile imageFile, String originalFilename) throws Exception {
        // Read the image
        byte[] imageBytes = readImageBytes(imageFile);

        // Verify we are in the right folder (note folder)
        Path parentFolder = Paths.get(noteImage.getOriginalImagePath()).getParent();
        if (parentFolder == null || !parentFolder.endsWith(noteUuid.toString())) {
            throw new IllegalArgumentException("The image path does not match the note UUID");
        }

        try {
            writeThumbnail(noteImage.getThumbnailPath(), originalFilename, imageBytes);
            writeOriginalImage(noteImage.getOriginalImagePath(), originalFilename, imageBytes);
        } catch (ImageStorageException e) {
            log.error("Failed to store image files for '{}'", originalFilename, e);

            //Cleanup: delete the entire note folder recursively
            try {
                deleteFolderRecursively(parentFolder);
                log.info("Deleted folder '{}' due to failed thumbnail/original image storage", parentFolder);
            } catch (IOException ex) {
                log.error("Failed to delete folder '{}' after storage failure", parentFolder, ex);
            }
            throw e;
        }
    }

    /**
     * Write the thumbnail image to the specified path. Applies image resizing and quality settings.
     *
     * @param outputImagePath  Path to write the thumbnail image to
     * @param originalFileName Name of the original image file (the one from the request)
     * @param imageBytes       Image bytes to write
     * @throws ImageStorageException if the thumbnail image could not be written
     */
    private void writeThumbnail(String outputImagePath, String originalFileName, byte[] imageBytes) throws ImageStorageException {
        File outputFile = new File(outputImagePath);

        try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            Thumbnails.Builder<?> builder = Thumbnails.of(inputStream)
                    .outputFormat(THUMBNAIL_FORMAT)
                    .outputQuality(THUMBNAIL_QUALITY);

            // Apply resizing mode based on property
            switch (THUMBNAIL_RESIZE_MODE.toLowerCase()) {
                case "fit-width":
                    builder.width(THUMBNAIL_WIDTH);
                    break;
                case "fit-height":
                    builder.height(THUMBNAIL_HEIGHT);
                    break;
                default: // default to fitting both dimensions
                    builder.size(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
                    break;
            }

            // Create directories if needed
            Files.createDirectories(outputFile.toPath().getParent());

            // Write the thumbnail file
            builder.toFile(outputFile);
        } catch (IOException e) {
            log.error("Failed to write thumbnail file '{}'", originalFileName, e);
            throw new ImageStorageException(originalFileName, e);
        }
    }

    /**
     * Read the image file from MultipartFile and return the bytes.
     *
     * @param imageFile MultipartFile containing the image
     * @return byte array containing the image
     * @throws IOException if the image file could not be read
     */
    private byte[] readImageBytes(MultipartFile imageFile) throws IOException {
        try {
            return imageFile.getBytes();
        } catch (IOException e) {
            log.error("Failed to read bytes from image file '{}'", imageFile.getOriginalFilename(), e);
            throw e;
        }
    }

    /**
     * Write the original image to the specified path. Applies image quality settings.
     *
     * @param outputImagePath  Path to write the original image to
     * @param originalFileName Name of the original image file (the one from the request)
     * @param imageBytes       Image bytes to write
     * @throws ImageStorageException if the original image could not be written
     */
    private void writeOriginalImage(String outputImagePath, String originalFileName, byte[] imageBytes) throws ImageStorageException {
        File outputFile = new File(outputImagePath);
        String fileExtension = getFileExtension(originalFileName);

        try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {

            // Create directories if needed
            Files.createDirectories(outputFile.toPath().getParent());

            // Write the original image file
            Thumbnails.of(inputStream)
                    .scale(ORIGINAL_DEFAULT_SCALE)
                    .outputFormat(fileExtension)
                    .outputQuality(ORIGINAL_QUALITY)
                    .toFile(outputFile);
        } catch (IOException e) {
            log.error("Failed to write original image file '{}'", originalFileName, e);
            throw new ImageStorageException(originalFileName, e);
        }
    }

    /**
     * Get the file extension from the given filename.
     *
     * @param filename Filename to get the extension from
     * @return File extension, or empty string if no extension was found
     */
    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) return "";
        return filename.substring(dotIndex + 1).toLowerCase();
    }

    /**
     * Generate the path for an image file based on the given parameters.
     *
     * @param imageType    {@link ImageType} to generate the path for. Used to determine the file destination
     * @param userUuid     UUID of the user owner of the bookmark
     * @param bookmarkUuid UUID of the bookmark to which the image belongs to
     * @param noteUuid     UUID of the note to which the image belongs to. May be null if the image is not associated with a note.
     * @param imageUuid    UUID of the image to generate the path for
     * @param imageFile    MultipartFile containing the image to generate the path for
     * @return String containing the path to the image file
     */
    public String generateImagePath(ImageType imageType, UUID userUuid, UUID bookmarkUuid, UUID noteUuid, UUID imageUuid, MultipartFile imageFile) {
        String originalFileExtension = getFileExtension(imageFile.getOriginalFilename());
        String fileName = (imageType == ImageType.THUMBNAIL
                ? getThumbnailFileName(imageUuid, originalFileExtension)
                : generateOriginalImageOutputFilename(imageUuid, originalFileExtension));

        Path path = Path.of(BASE_DIR, userUuid.toString(), BOOKMARKS_FOLDER, bookmarkUuid.toString());
        if (noteUuid != null) {
            path = path.resolve(NOTES_FOLDER).resolve(noteUuid.toString());
        }

        return path.resolve(fileName).toString();
    }

    /**
     * Generate the output filename for the original image file based on the given parameters. Applies a prefix if configured.
     *
     * @param imageUuid     UUID of the image
     * @param fileExtension File extension of the original image file.
     * @return String containing the output filename for the original image file
     */
    private String generateOriginalImageOutputFilename(UUID imageUuid, String fileExtension) {
        return (ORIGINAL_PREFIX != null ? ORIGINAL_PREFIX : "") + imageUuid + "." + fileExtension;
    }

    /**
     * Generate the output filename for the thumbnail image file based on the given parameters. Applies a prefix if configured.
     *
     * @param imageUuid     UUID of the image
     * @param fileExtension File extension of the original image file.
     * @return String containing the output filename for the thumbnail image file
     */
    private String getThumbnailFileName(UUID imageUuid, String fileExtension) {
        // Check if we need to override the thumbnail format
        String outputFileExtension = (THUMBNAIL_FORMAT != null && !THUMBNAIL_FORMAT.isBlank())
                ? THUMBNAIL_FORMAT
                : fileExtension;

        return (THUMBNAIL_PREFIX != null ? THUMBNAIL_PREFIX : "") + imageUuid + "." + outputFileExtension;
    }

    /**
     * Get the MIME type for the given file extension. Returns null if the extension is not supported.
     *
     * @param fileName Filename to get the MIME type for.
     * @return MIME type for the given file extension, or null if the extension is not supported.
     */
    public String getThumbnailMimeTypeFromExtension(String fileName) {
        String ext = getFileExtension(fileName);
        return SUPPORTED_EXT_TO_MIME.getOrDefault(ext, null);
    }

    /**
     * Delete the file at the given path. Does nothing if the file does not exist.
     *
     * @param filePath String with the path to the file to delete
     * @throws IOException if the file could not be deleted
     */
    public void removeFile(String filePath) throws IOException {
        if (filePath == null || filePath.isBlank()) {
            log.warn("Skipping image deletion because file path is null or blank");
            return;
        }
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            log.warn("Skipping image deletion because file does not exist: {}", filePath);
            return;
        }
        Files.deleteIfExists(path);
    }

    /**
     * Recursively delete the given folder and all its contents. Does nothing if the folder does not exist.
     *
     * @param path Path to the folder to delete
     * @throws IOException if the folder or a file could not be deleted
     */
    private void deleteFolderRecursively(Path path) throws IOException {
        if (!Files.exists(path)) return;

        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Delete the base directory for all user data.
     * @throws IOException if the directory could not be deleted
     */
    public void deleteBaseDirectory() throws IOException {
        Path path = Paths.get(BASE_DIR);
        deleteFolderRecursively(path);
    }

    /**
     * Delete the note folder for the given user, bookmark and note.
     * @param userUuid UUID of the user to delete the note folder for
     * @param bookmarkUuid UUID of the bookmark to delete the note folder for
     * @param noteUuid UUID of the note to delete the note folder for
     * @throws IOException if the folder or a file could not be deleted
     */
    public void deleteNoteDirectory(UUID userUuid, UUID bookmarkUuid, UUID noteUuid) throws IOException {
        Path path = Path.of(BASE_DIR, userUuid.toString(), BOOKMARKS_FOLDER, bookmarkUuid.toString(), NOTES_FOLDER, noteUuid.toString());
        deleteFolderRecursively(path);
    }

}
