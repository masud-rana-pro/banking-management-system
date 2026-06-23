package com.sbms.transaction.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.transaction.dto.request.*;
import com.sbms.transaction.dto.response.StandingInstructionResponse;
import com.sbms.transaction.dto.response.TransactionDashboardSummaryResponse;
import com.sbms.transaction.dto.response.TransactionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ITransactionController {
    ApiResponse<TransactionResponse> deposit(DepositRequest request);
    ApiResponse<TransactionResponse> withdraw(WithdrawalRequest request);
    ApiResponse<TransactionResponse> transfer(FundTransferRequest request);
    ApiResponse<TransactionResponse> chequeClearing(ChequeClearingRequest request);
    ApiResponse<StandingInstructionResponse> createStandingInstruction(StandingInstructionRequest request);
    ApiResponse<List<StandingInstructionResponse>> listStandingInstructions();
    ApiResponse<List<TransactionResponse>> list();
    ApiResponse<TransactionResponse> getById(Long id);
    ApiResponse<List<TransactionResponse>> search(String keyword);
    ApiResponse<TransactionResponse> reverse(Long id, TransactionReversalRequest request, HttpServletRequest servletRequest);
    ApiResponse<TransactionResponse> journal(Long id);
    ApiResponse<TransactionDashboardSummaryResponse> dashboardSummary();
    ResponseEntity<byte[]> previewVoucher(Long id);
    ResponseEntity<byte[]> downloadVoucher(Long id);
}
