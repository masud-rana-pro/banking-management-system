package com.sbms.notification.repository;

import com.sbms.notification.entity.NotificationEvent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class NotificationEventRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public NotificationEvent save(NotificationEvent entity) {
        entityManager.persist(entity);
        return entity;
    }

    public NotificationEvent update(NotificationEvent entity) {
        return entityManager.merge(entity);
    }

    public Optional<NotificationEvent> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT e FROM NotificationEvent e WHERE e.id = :id",
                    NotificationEvent.class
            ).setParameter("id", id).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<NotificationEvent> findByEventCode(String eventCode) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT e FROM NotificationEvent e WHERE LOWER(e.eventCode) = :eventCode",
                    NotificationEvent.class
            ).setParameter("eventCode", eventCode.toLowerCase()).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<NotificationEvent> findAll() {
        return entityManager.createQuery(
                "SELECT e FROM NotificationEvent e ORDER BY e.id DESC",
                NotificationEvent.class
        ).getResultList();
    }

    public String findLastEventCode() {
        List<String> result = entityManager.createQuery(
                "SELECT e.eventCode FROM NotificationEvent e WHERE e.eventCode LIKE :prefix ORDER BY e.eventCode DESC",
                String.class
        ).setParameter("prefix", "EVT-%").setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }
}
