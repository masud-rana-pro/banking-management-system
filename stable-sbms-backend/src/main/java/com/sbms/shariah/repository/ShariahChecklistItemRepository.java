package com.sbms.shariah.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.shariah.entity.ShariahChecklistItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ShariahChecklistItemRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public ShariahChecklistItem save(ShariahChecklistItem entity) {
        entityManager.persist(entity);
        return entity;
    }

    public Optional<ShariahChecklistItem> findById(Long id) {
        return Optional.ofNullable(entityManager.find(ShariahChecklistItem.class, id));
    }

    public List<ShariahChecklistItem> findActive() {
        return entityManager.createQuery(
                "SELECT i FROM ShariahChecklistItem i WHERE i.status <> :archived ORDER BY i.itemCode ASC",
                ShariahChecklistItem.class
        ).setParameter("archived", RecordStatus.ARCHIVED).getResultList();
    }

    public List<ShariahChecklistItem> findAllByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return entityManager.createQuery(
                "SELECT i FROM ShariahChecklistItem i WHERE i.id IN :ids AND i.status <> :archived",
                ShariahChecklistItem.class
        ).setParameter("ids", ids)
                .setParameter("archived", RecordStatus.ARCHIVED)
                .getResultList();
    }
}
