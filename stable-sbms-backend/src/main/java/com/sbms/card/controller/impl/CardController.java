package com.sbms.card.controller.impl;

import com.sbms.card.controller.ICardController;
import com.sbms.card.dto.request.CardPinEventRequest;
import com.sbms.card.dto.request.CardRequest;
import com.sbms.card.dto.request.CardWorkflowActionRequest;
import com.sbms.card.dto.response.CardDashboardSummaryResponse;
import com.sbms.card.dto.response.CardEventLogResponse;
import com.sbms.card.dto.response.CardPinEventResponse;
import com.sbms.card.dto.response.CardResponse;
import com.sbms.card.dto.response.CardTransactionResponse;
import com.sbms.card.service.ICardService;
import com.sbms.common.aop.AopRequestContext;
import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiresPermission("CARD_MANAGEMENT_ACCESS")
public class CardController implements ICardController {

    @Autowired
    private ICardService cardService;

    @Override
    @RequiresPermission("CARD_CREATE")
    @PostMapping("/create")
    public ApiResponse<CardResponse> create(@RequestBody CardRequest request) {
        return ResponseBuilder.success("Card created successfully", cardService.create(request, actor("SYSTEM")));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<CardResponse>> list() {
        return ResponseBuilder.success("Card list fetched successfully", cardService.list());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<CardResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Card fetched successfully", cardService.getById(id));
    }

    @Override
    @RequiresPermission("CARD_EDIT")
    @PutMapping("/{id}")
    public ApiResponse<CardResponse> update(@PathVariable Long id, @RequestBody CardRequest request) {
        return ResponseBuilder.success("Card updated successfully", cardService.update(id, request, actor("SYSTEM")));
    }

    @Override
    @RequiresPermission("CARD_ARCHIVE")
    @DeleteMapping("/{id}")
    public ApiResponse<CardResponse> archive(@PathVariable Long id) {
        return ResponseBuilder.success("Card archived successfully", cardService.archive(id, actor("SYSTEM")));
    }

    @Override
    @RequiresPermission("CARD_RESTORE")
    @PutMapping("/{id}/restore")
    public ApiResponse<CardResponse> restore(@PathVariable Long id) {
        return ResponseBuilder.success("Card restored successfully", cardService.restore(id, actor("SYSTEM")));
    }

    @Override
    @RequiresPermission("CARD_ACTIVATE")
    @PostMapping("/{id}/activate")
    public ApiResponse<CardResponse> activate(@PathVariable Long id, @RequestBody(required = false) CardWorkflowActionRequest request) {
        return ResponseBuilder.success("Card activated successfully", cardService.activate(id, request, actor("SYSTEM")));
    }

    @Override
    @RequiresPermission("CARD_BLOCK")
    @PostMapping("/{id}/block")
    public ApiResponse<CardResponse> block(@PathVariable Long id, @RequestBody(required = false) CardWorkflowActionRequest request) {
        return ResponseBuilder.success("Card blocked successfully", cardService.block(id, request, actor("SYSTEM")));
    }

    @Override
    @RequiresPermission("CARD_UNBLOCK")
    @PostMapping("/{id}/unblock")
    public ApiResponse<CardResponse> unblock(@PathVariable Long id, @RequestBody(required = false) CardWorkflowActionRequest request) {
        return ResponseBuilder.success("Card unblocked successfully", cardService.unblock(id, request, actor("SYSTEM")));
    }

    @Override
    @RequiresPermission("CARD_REPLACE")
    @PostMapping("/{id}/replace")
    public ApiResponse<CardResponse> replace(@PathVariable Long id, @RequestBody(required = false) CardWorkflowActionRequest request) {
        return ResponseBuilder.success("Replacement card issued successfully", cardService.replace(id, request, actor("SYSTEM")));
    }

    @Override
    @RequiresPermission("CARD_RENEW")
    @PostMapping("/{id}/renew")
    public ApiResponse<CardResponse> renew(@PathVariable Long id, @RequestBody(required = false) CardWorkflowActionRequest request) {
        return ResponseBuilder.success("Renewed card issued successfully", cardService.renew(id, request, actor("SYSTEM")));
    }

    @Override
    @GetMapping("/{id}/events")
    public ApiResponse<List<CardEventLogResponse>> events(@PathVariable Long id) {
        return ResponseBuilder.success("Card event log fetched successfully", cardService.getEvents(id));
    }

    @Override
    @GetMapping("/{id}/pin-events")
    public ApiResponse<List<CardPinEventResponse>> pinEvents(@PathVariable Long id) {
        return ResponseBuilder.success("Card PIN events fetched successfully", cardService.getPinEvents(id));
    }

    @Override
    @RequiresPermission("CARD_PIN_EVENT")
    @PostMapping("/{id}/pin-events")
    public ApiResponse<CardPinEventResponse> addPinEvent(@PathVariable Long id, @RequestBody CardPinEventRequest request) {
        return ResponseBuilder.success("Card PIN event recorded successfully", cardService.addPinEvent(id, request, actor("SYSTEM")));
    }

    @Override
    @GetMapping("/atm-cdm-transactions")
    public ApiResponse<List<CardTransactionResponse>> atmCdmTransactions() {
        return ResponseBuilder.success("ATM/CDM transactions fetched successfully", cardService.getAtmCdmTransactions());
    }

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<CardDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("Card dashboard summary fetched successfully", cardService.dashboardSummary());
    }

    private String actor(String fallback) {
        String username = AopRequestContext.currentUsername();
        return username == null || username.trim().isEmpty() ? fallback : username.trim();
    }
}
