package com.sbms.card.service.impl;

import com.sbms.account.entity.Account;
import com.sbms.account.enums.AccountStatus;
import com.sbms.account.repository.AccountRepository;
import com.sbms.card.dto.request.CardPinEventRequest;
import com.sbms.card.dto.request.CardRequest;
import com.sbms.card.dto.request.CardWorkflowActionRequest;
import com.sbms.card.dto.response.CardDashboardSummaryResponse;
import com.sbms.card.dto.response.CardEventLogResponse;
import com.sbms.card.dto.response.CardPinEventResponse;
import com.sbms.card.dto.response.CardResponse;
import com.sbms.card.dto.response.CardTransactionResponse;
import com.sbms.card.entity.Card;
import com.sbms.card.entity.CardEventLog;
import com.sbms.card.entity.CardPinEvent;
import com.sbms.card.enums.CardEventType;
import com.sbms.card.enums.CardPinEventType;
import com.sbms.card.enums.CardStatus;
import com.sbms.card.repository.CardEventLogRepository;
import com.sbms.card.repository.CardPinEventRepository;
import com.sbms.card.repository.CardRepository;
import com.sbms.card.service.ICardService;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.common.mail.AutomatedMailService;
import com.sbms.customer.entity.Customer;
import com.sbms.customer.enums.CustomerStatus;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CardService implements ICardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardEventLogRepository cardEventLogRepository;

    @Autowired
    private CardPinEventRepository cardPinEventRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AutomatedMailService automatedMailService;

    @Override
    public CardResponse create(CardRequest request, String username) {
        ValidatedContext context = validateRequest(request, null);

        Card card = new Card();
        mapRequest(request, card, context.customer(), context.account(), null);
        card.setCardRefNo(generateCardRefNo());

        Card saved = cardRepository.save(card);
        saveEvent(saved, CardEventType.ISSUED, username, "Card issued");
        if (saved.getCardStatus() == CardStatus.ACTIVE) {
            saveEvent(saved, CardEventType.ACTIVATED, username, "Card activated during issuance");
        } else if (saved.getCardStatus() == CardStatus.BLOCKED) {
            saveEvent(saved, CardEventType.BLOCKED, username, resolveReason(saved.getBlockReason(), "Card blocked during issuance"));
        }
        sendCardStatusMail(saved, "Issued", "Card issued successfully");
        return mapResponse(saved);
    }

    @Override
    public List<CardResponse> list() {
        return cardRepository.findAll().stream().map(this::mapResponse).toList();
    }

    @Override
    public CardResponse getById(Long id) {
        return mapResponse(getEntity(id));
    }

    @Override
    public CardResponse update(Long id, CardRequest request, String username) {
        Card card = getEntity(id);
        if (card.getStatus() == RecordStatus.ARCHIVED) {
            throw new BadRequestException("Archived card cannot be updated");
        }

        ValidatedContext context = validateRequest(request, id);
        CardStatus previousStatus = card.getCardStatus();

        mapRequest(request, card, context.customer(), context.account(), id);
        Card updated = cardRepository.update(card);

        if (previousStatus != updated.getCardStatus()) {
            if (updated.getCardStatus() == CardStatus.ACTIVE) {
                saveEvent(updated, CardEventType.ACTIVATED, username, "Card activated from update");
            } else if (updated.getCardStatus() == CardStatus.BLOCKED) {
                saveEvent(updated, CardEventType.BLOCKED, username, resolveReason(updated.getBlockReason(), "Card blocked from update"));
            }
            sendCardStatusMail(updated, updated.getCardStatus().name(), updated.getBlockReason());
        }

        return mapResponse(updated);
    }

    @Override
    public CardResponse archive(Long id, String username) {
        Card card = getEntity(id);
        card.setStatus(RecordStatus.ARCHIVED);
        card.setBlockReason(resolveReason(card.getBlockReason(), "Archived by " + username));
        return mapResponse(cardRepository.update(card));
    }

    @Override
    public CardResponse restore(Long id, String username) {
        Card card = getEntity(id);
        if (card.getStatus() != RecordStatus.ARCHIVED) {
            throw new BadRequestException("Only archived card can be restored");
        }
        card.setStatus(RecordStatus.ACTIVE);
        return mapResponse(cardRepository.update(card));
    }

    @Override
    public CardResponse activate(Long id, CardWorkflowActionRequest request, String username) {
        Card card = getEntity(id);
        ensureActiveRecord(card);
        if (card.getCardStatus() == CardStatus.EXPIRED) {
            throw new BadRequestException("Expired card cannot be activated");
        }
        card.setCardStatus(CardStatus.ACTIVE);
        card.setBlockReason(null);
        Card updated = cardRepository.update(card);
        saveEvent(updated, CardEventType.ACTIVATED, resolveUser(request, username), resolveRemarks(request, "Card activated"));
        sendCardStatusMail(updated, "Activated", resolveRemarks(request, "Card activated"));
        return mapResponse(updated);
    }

    @Override
    public CardResponse block(Long id, CardWorkflowActionRequest request, String username) {
        Card card = getEntity(id);
        ensureActiveRecord(card);
        if (card.getCardStatus() == CardStatus.EXPIRED) {
            throw new BadRequestException("Expired card cannot be blocked");
        }
        String blockReason = trim(request == null ? null : request.getBlockReason());
        if (blockReason == null) {
            throw new BadRequestException("Block reason is required");
        }
        card.setCardStatus(CardStatus.BLOCKED);
        card.setBlockReason(blockReason);
        Card updated = cardRepository.update(card);
        saveEvent(updated, CardEventType.BLOCKED, resolveUser(request, username), resolveRemarks(request, blockReason));
        sendCardStatusMail(updated, "Blocked", resolveRemarks(request, blockReason));
        return mapResponse(updated);
    }

    @Override
    public CardResponse unblock(Long id, CardWorkflowActionRequest request, String username) {
        Card card = getEntity(id);
        ensureActiveRecord(card);
        if (card.getCardStatus() != CardStatus.BLOCKED) {
            throw new BadRequestException("Only blocked card can be unblocked");
        }
        card.setCardStatus(CardStatus.ACTIVE);
        card.setBlockReason(null);
        Card updated = cardRepository.update(card);
        saveEvent(updated, CardEventType.UNBLOCKED, resolveUser(request, username), resolveRemarks(request, "Card unblocked"));
        sendCardStatusMail(updated, "Unblocked", resolveRemarks(request, "Card unblocked"));
        return mapResponse(updated);
    }

    @Override
    public CardResponse replace(Long id, CardWorkflowActionRequest request, String username) {
        Card current = getEntity(id);
        ensureActiveRecord(current);
        if (current.getCardStatus() == CardStatus.EXPIRED) {
            throw new BadRequestException("Expired card cannot be replaced");
        }
        current.setCardStatus(CardStatus.REPLACED);
        Card oldCard = cardRepository.update(current);
        saveEvent(oldCard, CardEventType.REPLACED, resolveUser(request, username), resolveRemarks(request, "Card replaced"));
        sendCardStatusMail(oldCard, "Replaced", resolveRemarks(request, "Card replaced"));

        Card replacement = cloneCard(current, request, CardStatus.PENDING_ACTIVATION, id);
        Card saved = cardRepository.save(replacement);
        saveEvent(saved, CardEventType.ISSUED, resolveUser(request, username), "Replacement card issued for " + current.getCardRefNo());
        sendCardStatusMail(saved, "Replacement issued", "Replacement card issued");
        return mapResponse(saved);
    }

    @Override
    public CardResponse renew(Long id, CardWorkflowActionRequest request, String username) {
        Card current = getEntity(id);
        ensureActiveRecord(current);
        current.setCardStatus(CardStatus.RENEWED);
        Card oldCard = cardRepository.update(current);
        saveEvent(oldCard, CardEventType.RENEWED, resolveUser(request, username), resolveRemarks(request, "Card renewed"));
        sendCardStatusMail(oldCard, "Renewed", resolveRemarks(request, "Card renewed"));

        Card renewed = cloneCard(current, request, CardStatus.PENDING_ACTIVATION, id);
        renewed.setIssueDate(LocalDate.now());
        renewed.setExpiryDate(resolveRenewedExpiryDate(request, current));
        Card saved = cardRepository.save(renewed);
        saveEvent(saved, CardEventType.ISSUED, resolveUser(request, username), "Renewed card issued for " + current.getCardRefNo());
        sendCardStatusMail(saved, "Renewal issued", "Renewed card issued");
        return mapResponse(saved);
    }

    @Override
    public List<CardEventLogResponse> getEvents(Long id) {
        getEntity(id);
        return cardEventLogRepository.findByCardId(id).stream()
                .map(event -> new CardEventLogResponse(
                        event.getId(),
                        event.getCard().getId(),
                        event.getCard().getCardRefNo(),
                        event.getEventType(),
                        event.getEventDate(),
                        event.getPerformedBy(),
                        event.getRemarks(),
                        event.getStatus(),
                        event.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public List<CardPinEventResponse> getPinEvents(Long id) {
        getEntity(id);
        return cardPinEventRepository.findByCardId(id).stream()
                .map(this::mapPinEvent)
                .toList();
    }

    @Override
    public CardPinEventResponse addPinEvent(Long id, CardPinEventRequest request, String username) {
        Card card = getEntity(id);
        ensureActiveRecord(card);
        if (request == null || request.getEventType() == null) {
            throw new BadRequestException("PIN event type is required");
        }

        CardPinEvent entity = new CardPinEvent();
        entity.setCard(card);
        entity.setEventType(request.getEventType());
        entity.setEventDate(request.getEventDate());
        entity.setPerformedBy(resolveUser(request.getPerformedBy(), username));
        CardPinEvent saved = cardPinEventRepository.save(entity);

        if (request.getEventType() == CardPinEventType.WRONG_PIN || request.getEventType() == CardPinEventType.PIN_BLOCKED) {
            saveEvent(card, CardEventType.ATM_USAGE_ALERT, resolveUser(request.getPerformedBy(), username), "PIN security event recorded");
        }

        return mapPinEvent(saved);
    }

    @Override
    public List<CardTransactionResponse> getAtmCdmTransactions() {
        return cardEventLogRepository.findAtmCdmTransactions();
    }

    @Override
    public CardDashboardSummaryResponse dashboardSummary() {
        LocalDate today = LocalDate.now();
        LocalDate soon = today.plusDays(30);
        return new CardDashboardSummaryResponse(
                safeCount(cardRepository.countAllNonArchived()),
                safeCount(cardRepository.countByCardStatus(CardStatus.ACTIVE)),
                safeCount(cardRepository.countByCardStatus(CardStatus.BLOCKED)),
                safeCount(cardRepository.countExpiringBetween(today, soon)),
                safeCount(cardEventLogRepository.countTransactionEvents()),
                safeCount(cardRepository.countByCardStatus(CardStatus.PENDING_ACTIVATION)),
                safeCount(cardEventLogRepository.countUsageAlertsToday(today.atStartOfDay(), LocalDateTime.now())),
                cardRepository.findExpiringBetween(today, soon, 6).stream().map(this::mapResponse).toList(),
                cardRepository.findByCardStatus(CardStatus.PENDING_ACTIVATION, 6).stream().map(this::mapResponse).toList(),
                cardEventLogRepository.findRecentUsageAlerts(8)
        );
    }

    private ValidatedContext validateRequest(CardRequest request, Long currentId) {
        if (request == null) {
            throw new BadRequestException("Card request is required");
        }
        if (request.getCustomerId() == null) {
            throw new BadRequestException("Customer is required");
        }
        if (request.getAccountId() == null) {
            throw new BadRequestException("Account is required");
        }
        if (request.getCardType() == null) {
            throw new BadRequestException("Card type is required");
        }

        LocalDate issueDate = request.getIssueDate() == null ? LocalDate.now() : request.getIssueDate();
        LocalDate expiryDate = request.getExpiryDate();
        if (expiryDate == null) {
            throw new BadRequestException("Expiry date is required");
        }
        if (expiryDate.isBefore(issueDate)) {
            throw new BadRequestException("Expiry date cannot be earlier than issue date");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        if (customer.getStatus() == RecordStatus.ARCHIVED) {
            throw new BadRequestException("Archived customer cannot be linked with card");
        }
        if (customer.getCustomerStatus() != CustomerStatus.ACTIVE) {
            throw new BadRequestException("Only active customer is eligible for card issuance");
        }

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        if (account.getStatus() == RecordStatus.ARCHIVED) {
            throw new BadRequestException("Archived account cannot be linked with card");
        }
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Only active account is eligible for card issuance");
        }
        if (!account.getCustomer().getId().equals(customer.getId())) {
            throw new BadRequestException("Selected customer does not own the selected account");
        }
        if (!Boolean.TRUE.equals(account.getAccountType().getWithdrawalAllowed())) {
            throw new BadRequestException("Selected account type is not eligible for card withdrawal usage");
        }

        String maskedCardNo = trim(request.getMaskedCardNo());
        if (maskedCardNo != null && cardRepository.existsByMaskedCardNoExceptId(maskedCardNo, currentId)) {
            throw new BadRequestException("Masked card number already exists");
        }
        if (request.getCardStatus() == CardStatus.BLOCKED && trim(request.getBlockReason()) == null) {
            throw new BadRequestException("Block reason is required for blocked card status");
        }

        return new ValidatedContext(customer, account);
    }

    private void mapRequest(CardRequest request, Card card, Customer customer, Account account, Long currentId) {
        card.setCustomer(customer);
        card.setAccount(account);
        card.setCardType(request.getCardType());
        card.setIssueDate(request.getIssueDate() == null ? LocalDate.now() : request.getIssueDate());
        card.setExpiryDate(request.getExpiryDate());
        card.setCardStatus(request.getCardStatus() == null ? CardStatus.PENDING_ACTIVATION : request.getCardStatus());
        card.setBlockReason(trim(request.getBlockReason()));
        card.setMaskedCardNo(resolveMaskedCardNo(request.getMaskedCardNo(), account.getAccountNumber(), currentId));
        if (card.getCardStatus() != CardStatus.BLOCKED) {
            card.setBlockReason(null);
        }
        if (card.getStatus() == null) {
            card.setStatus(RecordStatus.ACTIVE);
        }
    }

    private String resolveMaskedCardNo(String incoming, String accountNumber, Long currentId) {
        String value = trim(incoming);
        if (value != null) {
            return value;
        }
        String lastRef = cardRepository.findLastCardRefNo();
        int nextNumber = 1;
        if (lastRef != null && lastRef.startsWith("CRD-")) {
            nextNumber = Integer.parseInt(lastRef.substring(4)) + 1;
        }
        String accountSuffix = accountNumber == null || accountNumber.length() < 4
                ? String.format("%04d", nextNumber % 10000)
                : accountNumber.substring(accountNumber.length() - 4);
        String generated = String.format("4000-****-%04d-%s", nextNumber % 10000, accountSuffix);
        if (cardRepository.existsByMaskedCardNoExceptId(generated, currentId)) {
            generated = generated + "-" + nextNumber;
        }
        return generated;
    }

    private String generateCardRefNo() {
        String lastCode = cardRepository.findLastCardRefNo();
        int nextNumber = 1;
        if (lastCode != null && lastCode.startsWith("CRD-")) {
            nextNumber = Integer.parseInt(lastCode.substring(4)) + 1;
        }
        return String.format("CRD-%05d", nextNumber);
    }

    private Card cloneCard(Card source, CardWorkflowActionRequest request, CardStatus status, Long currentId) {
        Card cloned = new Card();
        cloned.setCardRefNo(generateCardRefNo());
        cloned.setCustomer(source.getCustomer());
        cloned.setAccount(source.getAccount());
        cloned.setCardType(source.getCardType());
        cloned.setIssueDate(LocalDate.now());
        cloned.setExpiryDate(resolveRenewedExpiryDate(request, source));
        cloned.setCardStatus(status);
        cloned.setStatus(RecordStatus.ACTIVE);
        cloned.setBlockReason(null);
        cloned.setMaskedCardNo(resolveMaskedCardNo(request == null ? null : request.getMaskedCardNo(), source.getAccount().getAccountNumber(), currentId));
        return cloned;
    }

    private LocalDate resolveRenewedExpiryDate(CardWorkflowActionRequest request, Card source) {
        if (request != null && request.getExpiryDate() != null) {
            return request.getExpiryDate();
        }
        LocalDate baseDate = source.getExpiryDate() == null || source.getExpiryDate().isBefore(LocalDate.now())
                ? LocalDate.now()
                : source.getExpiryDate();
        return baseDate.plusYears(5);
    }

    private Card getEntity(Long id) {
        if (id == null) {
            throw new BadRequestException("Card id is required");
        }
        return cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
    }

    private void ensureActiveRecord(Card card) {
        if (card.getStatus() == RecordStatus.ARCHIVED) {
            throw new BadRequestException("Archived card cannot be processed");
        }
    }

    private void saveEvent(Card card, CardEventType eventType, String username, String remarks) {
        CardEventLog entity = new CardEventLog();
        entity.setCard(card);
        entity.setEventType(eventType);
        entity.setPerformedBy(resolveUser(username, "SYSTEM"));
        entity.setRemarks(remarks);
        cardEventLogRepository.save(entity);
    }

    private CardResponse mapResponse(Card entity) {
        LocalDate today = LocalDate.now();
        return new CardResponse(
                entity.getId(),
                entity.getCardRefNo(),
                entity.getCustomer().getId(),
                entity.getCustomer().getCustomerCode(),
                entity.getCustomer().getFullName(),
                entity.getAccount().getId(),
                entity.getAccount().getAccountNumber(),
                entity.getAccount().getAccountType().getTypeCode(),
                entity.getAccount().getAccountType().getTypeName(),
                entity.getAccount().getBranchId(),
                entity.getAccount().getCurrentBalance(),
                entity.getCardType(),
                entity.getMaskedCardNo(),
                entity.getIssueDate(),
                entity.getExpiryDate(),
                entity.getExpiryDate() != null && entity.getExpiryDate().isBefore(today) ? CardStatus.EXPIRED : entity.getCardStatus(),
                entity.getBlockReason(),
                entity.getStatus(),
                entity.getExpiryDate() != null && !entity.getExpiryDate().isBefore(today) && !entity.getExpiryDate().isAfter(today.plusDays(30)),
                safeCount(cardEventLogRepository.countByCardId(entity.getId())),
                safeCount(cardPinEventRepository.countByCardId(entity.getId())),
                safeCount(cardEventLogRepository.countUsageAlertsByCardId(entity.getId())),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private CardPinEventResponse mapPinEvent(CardPinEvent entity) {
        return new CardPinEventResponse(
                entity.getId(),
                entity.getCard().getId(),
                entity.getCard().getCardRefNo(),
                entity.getEventType(),
                entity.getEventDate(),
                entity.getPerformedBy(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    private String resolveRemarks(CardWorkflowActionRequest request, String fallback) {
        return resolveReason(request == null ? null : request.getRemarks(), fallback);
    }

    private String resolveReason(String value, String fallback) {
        String trimmed = trim(value);
        return trimmed == null ? fallback : trimmed;
    }

    private String resolveUser(CardWorkflowActionRequest request, String fallback) {
        return resolveUser(request == null ? null : request.getPerformedBy(), fallback);
    }

    private String resolveUser(String value, String fallback) {
        String trimmed = trim(value);
        return trimmed == null ? fallback : trimmed;
    }

    private String trim(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private long safeCount(Long count) {
        return count == null ? 0L : count;
    }

    private void sendCardStatusMail(Card card, String decision, String remarks) {
        if (card == null || card.getCustomer() == null || trim(card.getCustomer().getEmail()) == null) {
            return;
        }
        automatedMailService.sendApprovalDecisionEmail(
                card.getCustomer().getEmail(),
                "Card",
                card.getCardRefNo(),
                decision,
                remarks,
                "/cards/" + card.getId(),
                "Open Card"
        );
    }

    private record ValidatedContext(Customer customer, Account account) {}
}
