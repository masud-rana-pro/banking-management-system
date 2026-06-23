package com.sbms.common.file;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
public class FileUploadController {

    private final ImageStorageService imageStorageService;

    public FileUploadController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        String storedName = imageStorageService.storeImage(file);
        return ResponseBuilder.success(
                "Image uploaded successfully",
                new FileUploadResponse(storedName, imageStorageService.buildFileUrl(storedName), file.getSize())
        );
    }

    @PostMapping(value = "/upload-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileUploadResponse> uploadDocument(@RequestParam("file") MultipartFile file) {
        String storedName = imageStorageService.storeDocument(file);
        return ResponseBuilder.success(
                "Document uploaded successfully",
                new FileUploadResponse(storedName, imageStorageService.buildDocumentUrl(storedName), file.getSize())
        );
    }

    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<Resource> viewImage(@PathVariable String fileName) {
        Resource resource = imageStorageService.loadAsResource(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, inlineFileName(fileName))
                .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resource);
    }

    @GetMapping("/documents/{fileName:.+}")
    public ResponseEntity<Resource> viewDocument(@PathVariable String fileName) {
        Resource resource = imageStorageService.loadAsResource(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, inlineFileName(fileName))
                .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resource);
    }

    private String inlineFileName(String fileName) {
        return ContentDisposition
                .inline()
                .filename(imageStorageService.displayFileName(fileName), StandardCharsets.UTF_8)
                .build()
                .toString();
    }
}
