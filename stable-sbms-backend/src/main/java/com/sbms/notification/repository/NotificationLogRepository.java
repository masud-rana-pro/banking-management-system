package com.sbms.notification.repository;

import com.sbms.notification.entity.NotificationLog;
import com.sbms.notification.enums.NotificationChannelType;
import com.sbms.notification.enums.NotificationDeliveryStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class NotificationLogRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public NotificationLog save(NotificationLog entity) {
        entityManager.persist(entity);
        return entity;
    }

    public NotificationLog update(NotificationLog entity) {
        return entityManager.merge(entity);
    }

    public Optional<NotificationLog> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT l FROM NotificationLog l " +
                            "JOIN FETCH l.event e " +
                            "JOIN FETCH l.template t " +
                            "WHERE l.id = :id",
                    NotificationLog.class
            ).setParameter("id", id).getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<NotificationLog> findAll(String deliveryStatus, String channelType, String keyword) {
        StringBuilder hql = new StringBuilder(
                "SELECT l FROM NotificationLog l " +
                        "JOIN FETCH l.event e " +
                        "JOIN FETCH l.template t " +
                        "WHERE 1=1 "
        );
        if (deliveryStatus != null && !deliveryStatus.trim().isEmpty()) hql.append("AND l.deliveryStatus = :deliveryStatus ");
        if (channelType != null && !channelType.trim().isEmpty()) hql.append("AND l.channelType = :channelType ");
        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("AND (LOWER(e.eventCode) LIKE :keyword OR LOWER(e.eventName) LIKE :keyword OR LOWER(t.templateCode) LIKE :keyword OR LOWER(t.templateName) LIKE :keyword OR LOWER(l.recipientTo) LIKE :keyword) ");
        }
        hql.append("ORDER BY l.id DESC");

        TypedQuery<NotificationLog> query = entityManager.createQuery(hql.toString(), NotificationLog.class);
        if (deliveryStatus != null && !deliveryStatus.trim().isEmpty()) {
            query.setParameter("deliveryStatus", NotificationDeliveryStatus.valueOf(deliveryStatus.trim().toUpperCase()));
        }
        if (channelType != null && !channelType.trim().isEmpty()) {
            query.setParameter("channelType", NotificationChannelType.valueOf(channelType.trim().toUpperCase()));
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }
        return query.getResultList();
    }

    public List<NotificationLog> findRecent(int limit) {
        return entityManager.createQuery(
                "SELECT l FROM NotificationLog l " +
                        "JOIN FETCH l.event e " +
                        "JOIN FETCH l.template t " +
                        "ORDER BY l.id DESC",
                NotificationLog.class
        ).setMaxResults(limit).getResultList();
    }

    public long countByStatusForToday(NotificationDeliveryStatus deliveryStatus) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        return entityManager.createQuery(
                "SELECT COUNT(l.id) FROM NotificationLog l WHERE l.deliveryStatus = :deliveryStatus AND l.createdAt >= :start",
                Long.class
        ).setParameter("deliveryStatus", deliveryStatus)
                .setParameter("start", start)
                .getSingleResult();
    }

    public long countByStatus(NotificationDeliveryStatus deliveryStatus) {
        return entityManager.createQuery(
                "SELECT COUNT(l.id) FROM NotificationLog l WHERE l.deliveryStatus = :deliveryStatus",
                Long.class
        ).setParameter("deliveryStatus", deliveryStatus).getSingleResult();
    }

    public long countByChannelAndStatus(NotificationChannelType channelType, NotificationDeliveryStatus deliveryStatus) {
        return entityManager.createQuery(
                "SELECT COUNT(l.id) FROM NotificationLog l WHERE l.channelType = :channelType AND l.deliveryStatus = :deliveryStatus",
                Long.class
        ).setParameter("channelType", channelType)
                .setParameter("deliveryStatus", deliveryStatus)
                .getSingleResult();
    }
}
