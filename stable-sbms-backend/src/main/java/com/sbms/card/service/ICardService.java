package com.sbms.card.service;

import com.sbms.card.dto.request.CardPinEventRequest;
import com.sbms.card.dto.request.CardRequest;
import com.sbms.card.dto.request.CardWorkflowActionRequest;
import com.sbms.card.dto.response.CardDashboardSummaryResponse;
import com.sbms.card.dto.response.CardEventLogResponse;
import com.sbms.card.dto.response.CardPinEventResponse;
import com.sbms.card.dto.response.CardResponse;
import com.sbms.card.dto.response.CardTransactionResponse;

import java.util.List;

public interface ICardService {
    CardResponse create(CardRequest request, String username);
    List<CardResponse> list();
    CardResponse getById(Long id);
    CardResponse update(Long id, CardRequest request, String username);
    CardResponse archive(Long id, String username);
    CardResponse restore(Long id, String username);
    CardResponse activate(Long id, CardWorkflowActionRequest request, String username);
    CardResponse block(Long id, CardWorkflowActionRequest request, String username);
    CardResponse unblock(Long id, CardWorkflowActionRequest request, String username);
    CardResponse replace(Long id, CardWorkflowActionRequest request, String username);
    CardResponse renew(Long id, CardWorkflowActionRequest request, String username);
    List<CardEventLogResponse> getEvents(Long id);
    List<CardPinEventResponse> getPinEvents(Long id);
    CardPinEventResponse addPinEvent(Long id, CardPinEventRequest request, String username);
    List<CardTransactionResponse> getAtmCdmTransactions();
    CardDashboardSummaryResponse dashboardSummary();
}
