package com.sbms.transaction.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.common.aop.AopRequestContext;
import com.sbms.config.RequiresPermission;
import com.sbms.transaction.controller.ITransactionController;
import com.sbms.transaction.dto.request.*;
import com.sbms.transaction.dto.response.StandingInstructionResponse;
import com.sbms.transaction.dto.response.TransactionDashboardSummaryResponse;
import com.sbms.transaction.dto.response.TransactionResponse;
import com.sbms.transaction.service.ITransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiresPermission("TRANSACTIONS_ACCESS")
public class TransactionController implements ITransactionController {

    @Autowired
    private ITransactionService transactionService;

    @RequiresPermission("TRANSACTION_DEPOSIT")
    @Override
    @PostMapping("/deposit")
    public ApiResponse<TransactionResponse> deposit(@RequestBody DepositRequest request) {
        return ResponseBuilder.success("Cash deposit posted successfully", transactionService.deposit(request, actor("SYSTEM_TELLER")));
    }

    @RequiresPermission("TRANSACTION_WITHDRAW")
    @Override
    @PostMapping("/withdraw")
    public ApiResponse<TransactionResponse> withdraw(@RequestBody WithdrawalRequest request) {
        return ResponseBuilder.success("Cash withdrawal posted successfully", transactionService.withdraw(request, actor("SYSTEM_TELLER")));
    }

    @RequiresPermission("TRANSACTION_TRANSFER")
    @Override
    @PostMapping("/transfer")
    public ApiResponse<TransactionResponse> transfer(@RequestBody FundTransferRequest request) {
        return ResponseBuilder.success("Fund transfer posted successfully", transactionService.transfer(request, actor("SYSTEM_TELLER")));
    }

    @RequiresPermission("TRANSACTION_CHEQUE_CLEARING")
    @Override
    @PostMapping("/cheque-clearing")
    public ApiResponse<TransactionResponse> chequeClearing(@RequestBody ChequeClearingRequest request) {
        return ResponseBuilder.success("Cheque clearing posted successfully", transactionService.chequeClearing(request, actor("SYSTEM_TELLER")));
    }

    @RequiresPermission("TRANSACTION_STANDING_INSTRUCTION_CREATE")
    @Override
    @PostMapping("/standing-instruction/create")
    public ApiResponse<StandingInstructionResponse> createStandingInstruction(@RequestBody StandingInstructionRequest request) {
        return ResponseBuilder.success("Standing instruction created successfully", transactionService.createStandingInstruction(request, actor("SYSTEM_TELLER")));
    }

    @Override
    @GetMapping("/standing-instruction/list")
    public ApiResponse<List<StandingInstructionResponse>> listStandingInstructions() {
        return ResponseBuilder.success("Standing instruction list fetched successfully", transactionService.listStandingInstructions());
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<TransactionResponse>> list() {
        return ResponseBuilder.success("Transaction list fetched successfully", transactionService.list());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<TransactionResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Transaction fetched successfully", transactionService.getById(id));
    }

    @Override
    @GetMapping("/search")
    public ApiResponse<List<TransactionResponse>> search(@RequestParam(required = false) String keyword) {
        return ResponseBuilder.success("Transaction search fetched successfully", transactionService.search(keyword));
    }

    @RequiresPermission("TRANSACTION_REVERSE")
    @Override
    @PostMapping("/{id}/reverse")
    public ApiResponse<TransactionResponse> reverse(@PathVariable Long id,
                                                    @RequestBody TransactionReversalRequest request,
                                                    HttpServletRequest servletRequest) {
        TransactionResponse response = transactionService.reverse(id, request, actor("SYSTEM_TELLER"));
        return ResponseBuilder.success("Transaction reversed successfully", response);
    }

    @Override
    @GetMapping("/{id}/journal")
    public ApiResponse<TransactionResponse> journal(@PathVariable Long id) {
        return ResponseBuilder.success("Transaction journal fetched successfully", transactionService.journal(id));
    }

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<TransactionDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("Transaction dashboard summary fetched successfully", transactionService.dashboardSummary());
    }

    @Override
    @GetMapping("/{id}/voucher/preview")
    public ResponseEntity<byte[]> previewVoucher(@PathVariable Long id) {
        return transactionService.previewVoucher(id);
    }

    @Override
    @GetMapping("/{id}/voucher/download")
    public ResponseEntity<byte[]> downloadVoucher(@PathVariable Long id) {
        return transactionService.downloadVoucher(id);
    }

    private String actor(String fallback) {
        String username = AopRequestContext.currentUsername();
        return username == null || username.trim().isEmpty() ? fallback : username.trim();
    }
}
