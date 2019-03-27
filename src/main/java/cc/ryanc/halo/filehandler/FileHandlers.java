package cc.ryanc.halo.filehandler;

import cc.ryanc.halo.exception.FileUploadException;
import cc.ryanc.halo.model.enums.AttachmentType;
import cc.ryanc.halo.model.support.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.LinkedList;

/**
 * File handler manager.
 *
 * @author johnniang
 * @date 3/27/19
 */
@Slf4j
@Component
public class FileHandlers {

    /**
     * File handler container.
     */
    private final Collection<FileHandler> fileHandlers = new LinkedList<>();

    public FileHandlers(ApplicationContext applicationContext) {
        // Add all file handler
        addFileHandlers(applicationContext.getBeansOfType(FileHandler.class).values());
    }

    public UploadResult upload(MultipartFile file, AttachmentType attachmentType) {
        for (FileHandler fileHandler : fileHandlers) {
            if (fileHandler.supportType(attachmentType)) {
                return fileHandler.upload(file);
            }
        }

        log.error("There is no available file handle for attachment type: [{}]", attachmentType);
        throw new FileUploadException("No available file handler to filehandler the file").setErrorData(attachmentType);
    }

    public boolean delete(String key, AttachmentType attachmentType) {
        for (FileHandler fileHandler : fileHandlers) {
            if (fileHandler.supportType(attachmentType)) {
                return fileHandler.delete(key);
            }
        }

        log.error("There is no available file handle for attachment type: [{}]", attachmentType);
        throw new FileUploadException("No available file handler to delete the file").setErrorData(attachmentType);
    }

    /**
     * Adds file handlers.
     *
     * @param fileHandlers file handler collection
     * @return current file handlers
     */
    @NonNull
    public FileHandlers addFileHandlers(@Nullable Collection<FileHandler> fileHandlers) {
        if (!CollectionUtils.isEmpty(fileHandlers)) {
            this.fileHandlers.addAll(fileHandlers);
        }
        return this;
    }
}
