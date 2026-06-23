package com.sbms.card.repository;

import com.sbms.card.dto.response.CardTransactionResponse;
import com.sbms.card.entity.CardEventLog;
import com.sbms.card.enums.CardEventType;
import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class CardEventLogRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public CardEventLog save(CardEventLog entity) {
        entityManager.persist(entity);
        return entity;
    }

    public List<CardEventLog> findByCardId(Long cardId) {
        return entityManager.createQuery(
                        "SELECT e FROM CardEventLog e " +
                                "JOIN FETCH e.card c " +
                                "WHERE c.id = :cardId " +
                                "ORDER BY e.eventDate DESC, e.id DESC",
                        CardEventLog.class
                )
                .setParameter("cardId", cardId)
                .getResultList();
    }

    public Long countByCardId(Long cardId) {
        return entityManager.createQuery(
                        "SELECT COUNT(e.id) FROM CardEventLog e WHERE e.card.id = :cardId",
                        Long.class
                )
                .setParameter("cardId", cardId)
                .getSingleResult();
    }

    public Long countUsageAlertsByCardId(Long cardId) {
        return entityManager.createQuery(
                        "SELECT COUNT(e.id) FROM CardEventLog e " +
                                "WHERE e.card.id = :cardId AND e.eventType IN :types",
                        Long.class
                )
                .setParameter("cardId", cardId)
                .setParameter("types", List.of(CardEventType.ATM_USAGE_ALERT, CardEventType.CDM_USAGE_ALERT))
                .getSingleResult();
    }

    public Long countTransactionEvents() {
        return entityManager.createQuery(
                        "SELECT COUNT(e.id) FROM CardEventLog e " +
                                "JOIN e.card c " +
                                "WHERE e.eventType IN :types AND c.status <> :archived",
                        Long.class
                )
                .setParameter("types", List.of(CardEventType.ATM_TRANSACTION, CardEventType.CDM_TRANSACTION))
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }

    public Long countUsageAlertsToday(LocalDateTime startTime, LocalDateTime endTime) {
        return entityManager.createQuery(
                        "SELECT COUNT(e.id) FROM CardEventLog e " +
                                "JOIN e.card c " +
                                "WHERE e.eventType IN :types " +
                                "AND e.eventDate BETWEEN :startTime AND :endTime " +
                                "AND c.status <> :archived",
                        Long.class
                )
                .setParameter("types", List.of(CardEventType.ATM_USAGE_ALERT, CardEventType.CDM_USAGE_ALERT))
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }

    public List<CardTransactionResponse> findAtmCdmTransactions() {
        return entityManager.createQuery(
                        "SELECT new com.sbms.card.dto.response.CardTransactionResponse(" +
                                "e.id, c.id, c.cardRefNo, c.maskedCardNo, cu.id, cu.customerCode, cu.fullName, " +
                                "a.id, a.accountNumber, e.eventType, e.eventDate, e.performedBy, e.remarks" +
                                ") " +
                                "FROM CardEventLog e " +
                                "JOIN e.card c " +
                                "JOIN c.customer cu " +
                                "JOIN c.account a " +
                                "WHERE e.eventType IN :types " +
                                "ORDER BY e.eventDate DESC, e.id DESC",
                        CardTransactionResponse.class
                )
                .setParameter("types", List.of(
                        CardEventType.ATM_TRANSACTION,
                        CardEventType.CDM_TRANSACTION,
                        CardEventType.ATM_USAGE_ALERT,
                        CardEventType.CDM_USAGE_ALERT
                ))
                .getResultList();
    }

    public List<CardTransactionResponse> findRecentUsageAlerts(int limit) {
        return entityManager.createQuery(
                        "SELECT new com.sbms.card.dto.response.CardTransactionResponse(" +
                                "e.id, c.id, c.cardRefNo, c.maskedCardNo, cu.id, cu.customerCode, cu.fullName, " +
                                "a.id, a.accountNumber, e.eventType, e.eventDate, e.performedBy, e.remarks" +
                                ") " +
                                "FROM CardEventLog e " +
                                "JOIN e.card c " +
                                "JOIN c.customer cu " +
                                "JOIN c.account a " +
                                "WHERE e.eventType IN :types " +
                                "ORDER BY e.eventDate DESC, e.id DESC",
                        CardTransactionResponse.class
                )
                .setParameter("types", List.of(CardEventType.ATM_USAGE_ALERT, CardEventType.CDM_USAGE_ALERT))
                .setMaxResults(limit)
                .getResultList();
    }
}
