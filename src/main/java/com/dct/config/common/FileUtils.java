package com.dct.config.common;

import com.dct.model.constants.BaseCommonConstants;
import com.dct.model.dto.image.ImageDTO;
import com.dct.model.common.ImageConverter;

import com.dct.model.dto.image.ImageParameterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Provides methods for storing files, compressing images automatically
 * @author thoaidc
 */
@SuppressWarnings("unused")
public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);
    private String uploadDirectory = BaseCommonConstants.UPLOAD_RESOURCES.DEFAULT_DIRECTORY;
    private String prefixPath = BaseCommonConstants.UPLOAD_RESOURCES.DEFAULT_PREFIX_PATH;

    public void setUploadDirectory(String uploadDirectory) {
        this.uploadDirectory = StringUtils.hasText(uploadDirectory) ? uploadDirectory : this.uploadDirectory;
    }

    public void setPrefixPath(String prefixPath) {
        this.prefixPath = StringUtils.hasText(prefixPath) ? prefixPath : this.prefixPath;
    }

    public static boolean invalidUploadFile(MultipartFile file) {
        return file == null || file.isEmpty() || !Objects.nonNull(file.getOriginalFilename());
    }

    public static boolean invalidUploadFiles(MultipartFile[] files) {
        if (files == null || files.length == 0)
            return true;

        for (MultipartFile file : files) {
            if (invalidUploadFile(file))
                return true;
        }

        return false;
    }

    private File getFileToSave(String fileName, boolean isMakeNew) {
        File file = new File(uploadDirectory + File.separator + fileName);

        if (file.exists() || !isMakeNew)
            return file;

        try {
            // Make sure the parent directory exists
            File parentDir = file.getParentFile();

            if (Objects.nonNull(parentDir) && !parentDir.exists() && !parentDir.mkdirs()) {
                log.warn("[SAVE_FILE_ERROR] - Could not create parent directory: {}", parentDir.getAbsolutePath());
                return null;
            }

            return file.createNewFile() ? file : null;
        } catch (Exception e) {
            log.warn("[SAVE_FILE_ERROR] - Could not create new file at: {}", file.getAbsolutePath());
        }

        return null;
    }

    public static String generateUniqueFileName(String fileNameOrFileExtension) {
        String uniqueName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss_"));

        if (Objects.isNull(fileNameOrFileExtension))
            return uniqueName + UUID.randomUUID() + BaseCommonConstants.UPLOAD_RESOURCES.DEFAULT_IMAGE_FORMAT;

        String fileExtension = fileNameOrFileExtension.substring(fileNameOrFileExtension.lastIndexOf("."));
        return uniqueName + UUID.randomUUID() + fileExtension;
    }

    public String save(ImageDTO imageDTO) {
        ImageParameterDTO imageParameterDTO = imageDTO.getImageParameterDTO();
        try {
            String fileName = generateUniqueFileName(imageParameterDTO.getFileExtension());
            File fileToSave = getFileToSave(fileName, true);

            if (imageDTO.getCompressedImage() != null && fileToSave != null) {
                Path sourcePath = imageDTO.getCompressedImage().toPath();
                Path targetPath = fileToSave.toPath();
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                if (!imageDTO.getCompressedImage().delete())
                    log.warn("[CLEAN_UP_ERROR] - Could not clean up temporary for: {}", fileToSave.getAbsolutePath());

                log.debug("[SAVE_FILE_SUCCESS] - Save new file to: {}", fileToSave.getAbsolutePath());
                return prefixPath + fileName;
            }
        } catch (IOException e) {
            log.error("[SAVE_FILE_ERROR] - Could not save file: {}", imageParameterDTO.getOriginalImageFilename(), e);
        }

        return null;
    }

    public String save(MultipartFile file) {
        if (invalidUploadFile(file))
            return null;

        if (Objects.isNull(file.getOriginalFilename())) {
            log.warn("[SAVE_FILE_ERROR] - The uploaded file has an invalid name or is empty");
            return null;
        }

        String fileName = generateUniqueFileName(file.getOriginalFilename());
        File directory = getFileToSave(fileName, true);

        if (Objects.isNull(directory))
            return null;

        try {
            file.transferTo(directory);
            return prefixPath + fileName;
        } catch (IOException e) {
            log.error("[SAVE_FILE_ERROR] - Could not save this file to: {}", directory.getAbsolutePath(), e);
        }

        return null;
    }

    public List<String> save(MultipartFile[] files) {
        if (invalidUploadFiles(files))
            return Collections.emptyList();

        List<String> filePaths = new ArrayList<>();

        for (MultipartFile file : files) {
            String filePath = save(file);

            if (Objects.isNull(filePath))
                filePaths.add(BaseCommonConstants.UPLOAD_RESOURCES.DEFAULT_IMAGE_PATH_FOR_ERROR);
            else
                filePaths.add(filePath);
        }

        return filePaths;
    }

    public String autoCompressImageAndSave(MultipartFile image) {
        if (!ImageConverter.isValidImageFormat(image))
            return null;

        try {
            ImageDTO compressedImageFile = ImageConverter.compressImage(image);

            if (Objects.nonNull(compressedImageFile))
                return save(compressedImageFile);

            return save(image);
        } catch (IOException e) {
            log.error("[SAVE_FILE_ERROR] - Could not auto compress image and save: {}", image.getOriginalFilename(), e);
        }

        return null;
    }

    public List<String> autoCompressImageAndSave(MultipartFile[] images) {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            String imageUrl = autoCompressImageAndSave(image);

            if (StringUtils.hasText(imageUrl)) {
                imageUrls.add(imageUrl);
            }
        }

        return imageUrls;
    }

    public boolean delete(String filePath) {
        if (!StringUtils.hasText(filePath))
            return false;

        int positionPrefixPath = filePath.lastIndexOf(prefixPath);
        int prefixSize = prefixPath.length();
        String fileName = filePath.substring(positionPrefixPath + prefixSize);
        File file = getFileToSave(fileName, false);

        if (Objects.isNull(file))
            return false;

        log.debug("[DELETE_FILE] - Deleting file: {}", file.getAbsolutePath());
        return file.delete();
    }

    public void delete(Collection<String> filePaths) {
        if (Objects.isNull(filePaths) || filePaths.isEmpty())
            return;

        for (String filePath : filePaths) {
            if (!delete(filePath)) {
                log.error("[DELETE_FILE_ERROR] - Could not delete file: {}", filePath);
            }
        }
    }
}
