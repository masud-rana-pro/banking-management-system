package com.sbms.notification.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.notification.entity.NotificationTemplate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class NotificationTemplateRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public NotificationTemplate save(NotificationTemplate entity) {
        entityManager.persist(entity);
        return entity;
    }

    public NotificationTemplate update(NotificationTemplate entity) {
        return entityManager.merge(entity);
    }

    public Optional<NotificationTemplate> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT t FROM NotificationTemplate t WHERE t.id = :id",
                    NotificationTemplate.class
            ).setParameter("id", id).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<NotificationTemplate> findByTemplateCode(String templateCode) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT t FROM NotificationTemplate t WHERE LOWER(t.templateCode) = :code",
                    NotificationTemplate.class
            ).setParameter("code", templateCode.toLowerCase()).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<NotificationTemplate> findAll() {
        return entityManager.createQuery(
                "SELECT t FROM NotificationTemplate t ORDER BY t.id DESC",
                NotificationTemplate.class
        ).getResultList();
    }

    public String findLastTemplateCode() {
        List<String> result = entityManager.createQuery(
                "SELECT t.templateCode FROM NotificationTemplate t WHERE t.templateCode LIKE :prefix ORDER BY t.templateCode DESC",
                String.class
        ).setParameter("prefix", "NTF-%").setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public long countByStatus(RecordStatus status) {
        return entityManager.createQuery(
                "SELECT COUNT(t.id) FROM NotificationTemplate t WHERE t.status = :status",
                Long.class
        ).setParameter("status", status).getSingleResult();
    }
}
