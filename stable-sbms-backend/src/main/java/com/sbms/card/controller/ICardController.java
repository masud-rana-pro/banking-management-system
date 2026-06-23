package com.sbms.card.controller;

import com.sbms.card.dto.request.CardPinEventRequest;
import com.sbms.card.dto.request.CardRequest;
import com.sbms.card.dto.request.CardWorkflowActionRequest;
import com.sbms.card.dto.response.CardDashboardSummaryResponse;
import com.sbms.card.dto.response.CardEventLogResponse;
import com.sbms.card.dto.response.CardPinEventResponse;
import com.sbms.card.dto.response.CardResponse;
import com.sbms.card.dto.response.CardTransactionResponse;
import com.sbms.common.response.ApiResponse;

import java.util.List;

public interface ICardController {
    ApiResponse<CardResponse> create(CardRequest request);
    ApiResponse<List<CardResponse>> list();
    ApiResponse<CardResponse> getById(Long id);
    ApiResponse<CardResponse> update(Long id, CardRequest request);
    ApiResponse<CardResponse> archive(Long id);
    ApiResponse<CardResponse> restore(Long id);
    ApiResponse<CardResponse> activate(Long id, CardWorkflowActionRequest request);
    ApiResponse<CardResponse> block(Long id, CardWorkflowActionRequest request);
    ApiResponse<CardResponse> unblock(Long id, CardWorkflowActionRequest request);
    ApiResponse<CardResponse> replace(Long id, CardWorkflowActionRequest request);
    ApiResponse<CardResponse> renew(Long id, CardWorkflowActionRequest request);
    ApiResponse<List<CardEventLogResponse>> events(Long id);
    ApiResponse<List<CardPinEventResponse>> pinEvents(Long id);
    ApiResponse<CardPinEventResponse> addPinEvent(Long id, CardPinEventRequest request);
    ApiResponse<List<CardTransactionResponse>> atmCdmTransactions();
    ApiResponse<CardDashboardSummaryResponse> dashboardSummary();
}
