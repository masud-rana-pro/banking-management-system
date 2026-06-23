package com.sbms.report.dto.response;

public class ReportFileResponse {

    private Long id;
    private String fileName;
    private String originalFileName;
    private String filePath;
    private String fileType;
    private Long fileSize;

    public ReportFileResponse(Long id, String fileName, String originalFileName, String filePath, String fileType, Long fileSize) {
        this.id = id;
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public Long getId() { return id; }
    public String getFileName() { return fileName; }
    public String getOriginalFileName() { return originalFileName; }
    public String getFilePath() { return filePath; }
    public String getFileType() { return fileType; }
    public Long getFileSize() { return fileSize; }
}
