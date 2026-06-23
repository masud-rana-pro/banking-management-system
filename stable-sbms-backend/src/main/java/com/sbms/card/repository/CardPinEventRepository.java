package com.sbms.card.repository;

import com.sbms.card.entity.CardPinEvent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class CardPinEventRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public CardPinEvent save(CardPinEvent entity) {
        entityManager.persist(entity);
        return entity;
    }

    public List<CardPinEvent> findByCardId(Long cardId) {
        return entityManager.createQuery(
                        "SELECT p FROM CardPinEvent p " +
                                "JOIN FETCH p.card c " +
                                "WHERE c.id = :cardId " +
                                "ORDER BY p.eventDate DESC, p.id DESC",
                        CardPinEvent.class
                )
                .setParameter("cardId", cardId)
                .getResultList();
    }

    public Long countByCardId(Long cardId) {
        return entityManager.createQuery(
                        "SELECT COUNT(p.id) FROM CardPinEvent p WHERE p.card.id = :cardId",
                        Long.class
                )
                .setParameter("cardId", cardId)
                .getSingleResult();
    }
}
