package com.sbms.common.file;

import com.sbms.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.InvalidPathException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private static final Set<String> ALLOWED_DOCUMENT_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif", "pdf", "doc", "docx");

    @Value("${file.upload-dir:src/main/resources/uploads/}")
    private String uploadDir;

    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Image file is required");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = extractExtension(originalName);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Only image files are allowed");
        }

        try {
            Path root = resolveUploadRoot();
            Files.createDirectories(root);

            String storedName = buildStoredFileName(originalName, extension);
            Path target = root.resolve(storedName).normalize();
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return storedName;
        } catch (IOException ex) {
            throw new BadRequestException("Failed to store image file");
        }
    }

    public Resource loadAsResource(String fileName) {
        String safeFileName = sanitizeFileName(fileName);

        for (Path root : resolveReadableRoots()) {
            try {
                Path filePath = root.resolve(safeFileName).normalize();
                if (!filePath.startsWith(root)) {
                    continue;
                }
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists() && resource.isReadable()) {
                    return resource;
                }
            } catch (MalformedURLException ignored) {
                // Try the next fallback root.
            }
        }
        throw new BadRequestException("Uploaded file not found");
    }

    public String storeDocument(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Document file is required");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = extractExtension(originalName);

        if (!ALLOWED_DOCUMENT_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Only image, PDF or Word documents are allowed");
        }

        try {
            Path root = resolveUploadRoot();
            Files.createDirectories(root);

            String storedName = buildStoredFileName(originalName, extension);
            Path target = root.resolve(storedName).normalize();
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return storedName;
        } catch (IOException ex) {
            throw new BadRequestException("Failed to store document file");
        }
    }

    public String buildFileUrl(String fileName) {
        return "/api/files/images/" + fileName;
    }

    public String buildDocumentUrl(String fileName) {
        return "/api/files/documents/" + fileName;
    }

    public String displayFileName(String fileName) {
        return sanitizeFileName(fileName)
                .replaceFirst("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}_", "");
    }

    private Path resolveUploadRoot() {
        Path configuredRoot = Paths.get(uploadDir);
        if (configuredRoot.isAbsolute()) {
            return configuredRoot.normalize();
        }

        Path currentDirectoryRoot = configuredRoot.toAbsolutePath().normalize();
        Path workspaceBackendRoot = Paths.get("stable-sbms-backend")
                .resolve(configuredRoot)
                .toAbsolutePath()
                .normalize();

        if (!Files.exists(currentDirectoryRoot) && Files.exists(workspaceBackendRoot)) {
            return workspaceBackendRoot;
        }
        return currentDirectoryRoot;
    }

    private List<Path> resolveReadableRoots() {
        List<Path> roots = new ArrayList<>();
        addRoot(roots, resolveUploadRoot());
        addRoot(roots, Paths.get("stable-sbms-backend", "src", "main", "resources", "uploads").toAbsolutePath().normalize());
        addRoot(roots, Paths.get("src", "main", "resources", "uploads").toAbsolutePath().normalize());
        return roots;
    }

    private void addRoot(List<Path> roots, Path root) {
        try {
            if (root != null && !roots.contains(root)) {
                roots.add(root);
            }
        } catch (InvalidPathException ignored) {
            // Ignore invalid fallback paths.
        }
    }

    private String sanitizeFileName(String fileName) {
        String safeFileName = StringUtils.cleanPath(fileName == null ? "" : fileName);
        if (safeFileName.isBlank() || safeFileName.contains("..")) {
            throw new BadRequestException("Uploaded file not found");
        }
        return safeFileName;
    }

    private String buildStoredFileName(String originalName, String extension) {
        String safeOriginalName = originalName.replace('\\', '/');
        int lastSlash = safeOriginalName.lastIndexOf('/');
        if (lastSlash >= 0) {
            safeOriginalName = safeOriginalName.substring(lastSlash + 1);
        }

        safeOriginalName = safeOriginalName
                .replaceAll("[^A-Za-z0-9._ -]", "_")
                .replaceAll("\\s+", " ")
                .trim();

        if (safeOriginalName.isBlank()) {
            safeOriginalName = "uploaded-file." + extension;
        }
        return UUID.randomUUID() + "_" + safeOriginalName;
    }

    private String extractExtension(String originalName) {
        int lastDot = originalName.lastIndexOf('.');
        if (lastDot < 0 || lastDot == originalName.length() - 1) {
            throw new BadRequestException("File extension is required");
        }
        return originalName.substring(lastDot + 1).toLowerCase(Locale.ROOT);
    }
}
