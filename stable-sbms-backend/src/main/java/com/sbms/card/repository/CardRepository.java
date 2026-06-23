package com.sbms.card.repository;

import com.sbms.card.entity.Card;
import com.sbms.card.enums.CardStatus;
import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class CardRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Card save(Card entity) {
        entityManager.persist(entity);
        return entity;
    }

    public Card update(Card entity) {
        return entityManager.merge(entity);
    }

    public Optional<Card> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT c FROM Card c " +
                                    "JOIN FETCH c.customer cu " +
                                    "JOIN FETCH c.account a " +
                                    "JOIN FETCH a.accountType at " +
                                    "WHERE c.id = :id",
                            Card.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<Card> findAll() {
        return entityManager.createQuery(
                        "SELECT c FROM Card c " +
                                "JOIN FETCH c.customer cu " +
                                "JOIN FETCH c.account a " +
                                "JOIN FETCH a.accountType at " +
                                "ORDER BY c.id DESC",
                        Card.class
                )
                .getResultList();
    }

    public Optional<Card> findByMaskedCardNo(String maskedCardNo) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT c FROM Card c " +
                                    "JOIN FETCH c.customer cu " +
                                    "JOIN FETCH c.account a " +
                                    "JOIN FETCH a.accountType at " +
                                    "WHERE LOWER(c.maskedCardNo) = :maskedCardNo",
                            Card.class
                    )
                    .setParameter("maskedCardNo", maskedCardNo.toLowerCase())
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public boolean existsByMaskedCardNoExceptId(String maskedCardNo, Long excludeId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(c.id) FROM Card c " +
                                "WHERE LOWER(c.maskedCardNo) = :maskedCardNo " +
                                "AND (:excludeId IS NULL OR c.id <> :excludeId)",
                        Long.class
                )
                .setParameter("maskedCardNo", maskedCardNo.toLowerCase())
                .setParameter("excludeId", excludeId)
                .getSingleResult();
        return count != null && count > 0;
    }

    public String findLastCardRefNo() {
        List<String> refs = entityManager.createQuery(
                        "SELECT c.cardRefNo FROM Card c " +
                                "WHERE c.cardRefNo LIKE :prefix " +
                                "ORDER BY c.cardRefNo DESC",
                        String.class
                )
                .setParameter("prefix", "CRD-%")
                .setMaxResults(1)
                .getResultList();
        return refs.isEmpty() ? null : refs.get(0);
    }

    public Long countAllNonArchived() {
        return entityManager.createQuery(
                        "SELECT COUNT(c.id) FROM Card c WHERE c.status <> :archived",
                        Long.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }

    public Long countByCardStatus(CardStatus cardStatus) {
        return entityManager.createQuery(
                        "SELECT COUNT(c.id) FROM Card c " +
                                "WHERE c.cardStatus = :cardStatus AND c.status <> :archived",
                        Long.class
                )
                .setParameter("cardStatus", cardStatus)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getSingleResult();
    }

    public Long countExpiringBetween(LocalDate fromDate, LocalDate toDate) {
        return entityManager.createQuery(
                        "SELECT COUNT(c.id) FROM Card c " +
                                "WHERE c.status <> :archived " +
                                "AND c.cardStatus <> :replaced " +
                                "AND c.cardStatus <> :renewed " +
                                "AND c.expiryDate BETWEEN :fromDate AND :toDate",
                        Long.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .setParameter("replaced", CardStatus.REPLACED)
                .setParameter("renewed", CardStatus.RENEWED)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getSingleResult();
    }

    public List<Card> findExpiringBetween(LocalDate fromDate, LocalDate toDate, int limit) {
        return entityManager.createQuery(
                        "SELECT c FROM Card c " +
                                "JOIN FETCH c.customer cu " +
                                "JOIN FETCH c.account a " +
                                "JOIN FETCH a.accountType at " +
                                "WHERE c.status <> :archived " +
                                "AND c.cardStatus <> :replaced " +
                                "AND c.cardStatus <> :renewed " +
                                "AND c.expiryDate BETWEEN :fromDate AND :toDate " +
                                "ORDER BY c.expiryDate ASC, c.id ASC",
                        Card.class
                )
                .setParameter("archived", RecordStatus.ARCHIVED)
                .setParameter("replaced", CardStatus.REPLACED)
                .setParameter("renewed", CardStatus.RENEWED)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Card> findByCardStatus(CardStatus cardStatus, int limit) {
        return entityManager.createQuery(
                        "SELECT c FROM Card c " +
                                "JOIN FETCH c.customer cu " +
                                "JOIN FETCH c.account a " +
                                "JOIN FETCH a.accountType at " +
                                "WHERE c.cardStatus = :cardStatus AND c.status <> :archived " +
                                "ORDER BY c.createdAt DESC, c.id DESC",
                        Card.class
                )
                .setParameter("cardStatus", cardStatus)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .setMaxResults(limit)
                .getResultList();
    }
}
