package com.sbms.zakat.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.zakat.entity.CharityBeneficiary;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class CharityBeneficiaryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public CharityBeneficiary save(CharityBeneficiary entity) {
        entityManager.persist(entity);
        return entity;
    }

    public CharityBeneficiary update(CharityBeneficiary entity) {
        return entityManager.merge(entity);
    }

    public Optional<CharityBeneficiary> findById(Long id) {
        return Optional.ofNullable(entityManager.find(CharityBeneficiary.class, id));
    }

    public Optional<CharityBeneficiary> findActiveById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                    "SELECT b FROM CharityBeneficiary b WHERE b.id = :id AND b.status <> :archived",
                    CharityBeneficiary.class
            ).setParameter("id", id)
                    .setParameter("archived", RecordStatus.ARCHIVED)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<CharityBeneficiary> findAll(String keyword) {
        StringBuilder hql = new StringBuilder("SELECT b FROM CharityBeneficiary b WHERE 1=1 ");
        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append("AND (LOWER(b.beneficiaryCode) LIKE :keyword OR LOWER(b.beneficiaryName) LIKE :keyword OR LOWER(COALESCE(b.mobile,'')) LIKE :keyword) ");
        }
        hql.append("ORDER BY b.id DESC");
        TypedQuery<CharityBeneficiary> query = entityManager.createQuery(hql.toString(), CharityBeneficiary.class);
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }
        return query.getResultList();
    }

    public List<CharityBeneficiary> findActive() {
        return entityManager.createQuery(
                "SELECT b FROM CharityBeneficiary b WHERE b.status = :status ORDER BY b.beneficiaryName ASC",
                CharityBeneficiary.class
        ).setParameter("status", RecordStatus.ACTIVE).getResultList();
    }

    public boolean existsByBeneficiaryCode(String code) {
        Long count = entityManager.createQuery(
                "SELECT COUNT(b.id) FROM CharityBeneficiary b WHERE b.beneficiaryCode = :code",
                Long.class
        ).setParameter("code", code).getSingleResult();
        return count > 0;
    }

    public boolean existsByBeneficiaryCodeExceptId(String code, Long id) {
        Long count = entityManager.createQuery(
                "SELECT COUNT(b.id) FROM CharityBeneficiary b WHERE b.beneficiaryCode = :code AND b.id <> :id",
                Long.class
        ).setParameter("code", code)
                .setParameter("id", id)
                .getSingleResult();
        return count > 0;
    }

    public String findLastBeneficiaryCode() {
        List<String> rows = entityManager.createQuery(
                "SELECT b.beneficiaryCode FROM CharityBeneficiary b WHERE b.beneficiaryCode LIKE :prefix ORDER BY b.beneficiaryCode DESC",
                String.class
        ).setParameter("prefix", "BEN-%")
                .setMaxResults(1)
                .getResultList();
        return rows.isEmpty() ? null : rows.get(0);
    }
}
