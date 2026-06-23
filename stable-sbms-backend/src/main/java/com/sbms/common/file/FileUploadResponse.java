package com.sbms.common.file;

public record FileUploadResponse(
        String fileName,
        String fileUrl,
        long size
) {
}
