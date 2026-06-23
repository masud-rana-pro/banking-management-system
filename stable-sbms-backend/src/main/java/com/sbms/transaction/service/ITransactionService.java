package com.sbms.transaction.service;

import com.sbms.transaction.dto.request.*;
import com.sbms.transaction.dto.response.StandingInstructionResponse;
import com.sbms.transaction.dto.response.TransactionDashboardSummaryResponse;
import com.sbms.transaction.dto.response.TransactionResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ITransactionService {
    TransactionResponse deposit(DepositRequest request, String username);
    TransactionResponse withdraw(WithdrawalRequest request, String username);
    TransactionResponse transfer(FundTransferRequest request, String username);
    TransactionResponse chequeClearing(ChequeClearingRequest request, String username);
    StandingInstructionResponse createStandingInstruction(StandingInstructionRequest request, String username);
    List<StandingInstructionResponse> listStandingInstructions();
    List<TransactionResponse> list();
    TransactionResponse getById(Long id);
    List<TransactionResponse> search(String keyword);
    TransactionResponse reverse(Long id, TransactionReversalRequest request, String username);
    TransactionResponse journal(Long id);
    TransactionDashboardSummaryResponse dashboardSummary();
    ResponseEntity<byte[]> previewVoucher(Long id);
    ResponseEntity<byte[]> downloadVoucher(Long id);
}
