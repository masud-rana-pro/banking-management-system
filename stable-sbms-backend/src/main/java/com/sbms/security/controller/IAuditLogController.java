package com.sbms.security.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.security.dto.response.AuditLogResponse;

import java.util.List;

public interface IAuditLogController {

    ApiResponse<List<AuditLogResponse>> listAuditLogs(String moduleName, String keyword);

    ApiResponse<AuditLogResponse> getAuditLogById(Long id);
}
