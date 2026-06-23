package com.sbms.accounting.repository;

import com.sbms.accounting.entity.GlJournal;
import com.sbms.accounting.entity.GlJournalLine;
import com.sbms.accounting.dto.response.TrialBalanceRowResponse;
import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Repository
@Transactional
public class GlJournalRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public GlJournal saveJournal(GlJournal entity) {
        entityManager.persist(entity);
        return entity;
    }

    public GlJournalLine saveLine(GlJournalLine entity) {
        entityManager.persist(entity);
        return entity;
    }

    public Optional<GlJournal> findBySource(String sourceType, Long sourceReferenceId) {
        List<GlJournal> items = entityManager.createQuery("""
                from GlJournal j
                where upper(j.sourceType) = :sourceType
                  and j.sourceReferenceId = :sourceReferenceId
                order by j.id desc
                """, GlJournal.class)
            .setParameter("sourceType", sourceType == null ? "" : sourceType.trim().toUpperCase())
            .setParameter("sourceReferenceId", sourceReferenceId)
            .getResultList();
        return items.stream().findFirst();
    }

    public List<GlJournal> findAll(String sourceType, Long sourceReferenceId, String accountCode, LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        StringBuilder hql = new StringBuilder("from GlJournal j where 1=1");
        if (sourceType != null && !sourceType.isBlank()) {
            hql.append(" and upper(j.sourceType) = :sourceType");
        }
        if (sourceReferenceId != null) {
            hql.append(" and j.sourceReferenceId = :sourceReferenceId");
        }
        if (branchId != null) {
            hql.append(" and j.branchId = :branchId");
        }
        if (dateFrom != null) {
            hql.append(" and j.journalDate >= :dateFrom");
        }
        if (dateTo != null) {
            hql.append(" and j.journalDate <= :dateTo");
        }
        if (accountCode != null && !accountCode.isBlank()) {
            hql.append(" and exists (select 1 from GlJournalLine l where l.journalId = j.id and upper(l.accountCode) = :accountCode)");
        }
        hql.append(" order by j.journalDate desc, j.id desc");
        TypedQuery<GlJournal> query = entityManager.createQuery(hql.toString(), GlJournal.class);
        if (sourceType != null && !sourceType.isBlank()) {
            query.setParameter("sourceType", sourceType.trim().toUpperCase());
        }
        if (sourceReferenceId != null) {
            query.setParameter("sourceReferenceId", sourceReferenceId);
        }
        if (branchId != null) {
            query.setParameter("branchId", branchId);
        }
        if (dateFrom != null) {
            query.setParameter("dateFrom", dateFrom);
        }
        if (dateTo != null) {
            query.setParameter("dateTo", dateTo);
        }
        if (accountCode != null && !accountCode.isBlank()) {
            query.setParameter("accountCode", accountCode.trim().toUpperCase());
        }
        return query.getResultList();
    }

    public List<GlJournalLine> findLines(Long journalId) {
        return entityManager.createQuery("""
                from GlJournalLine l
                where l.journalId = :journalId
                order by l.lineNo asc, l.id asc
                """, GlJournalLine.class)
            .setParameter("journalId", journalId)
            .getResultList();
    }

    public long countAll() {
        Long value = entityManager.createQuery("select count(j.id) from GlJournal j", Long.class).getSingleResult();
        return value == null ? 0L : value;
    }

    public long countBySourceType(String sourceType) {
        Long value = entityManager.createQuery("""
                select count(j.id)
                from GlJournal j
                where upper(j.sourceType) = :sourceType
                """, Long.class)
            .setParameter("sourceType", sourceType.trim().toUpperCase())
            .getSingleResult();
        return value == null ? 0L : value;
    }

    public long countByStatus(RecordStatus status) {
        Long value = entityManager.createQuery("""
                select count(j.id)
                from GlJournal j
                where j.status = :status
                """, Long.class)
            .setParameter("status", status)
            .getSingleResult();
        return value == null ? 0L : value;
    }

    public List<TrialBalanceRowResponse> fetchTrialBalance(LocalDate dateFrom, LocalDate dateTo, Long branchId) {
        StringBuilder hql = new StringBuilder("""
                select new com.sbms.accounting.dto.response.TrialBalanceRowResponse(
                    a.accountCode,
                    a.accountName,
                    a.accountType,
                    coalesce(sum(case when upper(l.entrySide) = 'DEBIT' then l.amount else 0 end), 0),
                    coalesce(sum(case when upper(l.entrySide) = 'CREDIT' then l.amount else 0 end), 0),
                    coalesce(sum(case when upper(l.entrySide) = 'DEBIT' then l.amount else -l.amount end), 0)
                )
                from GlJournal j
                join GlJournalLine l on l.journalId = j.id
                join GlAccount a on upper(a.accountCode) = upper(l.accountCode)
                where j.status = :status
                """);
        if (dateFrom != null) {
            hql.append(" and j.journalDate >= :dateFrom");
        }
        if (dateTo != null) {
            hql.append(" and j.journalDate <= :dateTo");
        }
        if (branchId != null) {
            hql.append(" and j.branchId = :branchId");
        }
        hql.append("""
                 group by a.accountCode, a.accountName, a.accountType
                 order by a.accountCode asc
                """);
        TypedQuery<TrialBalanceRowResponse> query = entityManager.createQuery(hql.toString(), TrialBalanceRowResponse.class)
                .setParameter("status", RecordStatus.ACTIVE);
        if (dateFrom != null) {
            query.setParameter("dateFrom", dateFrom);
        }
        if (dateTo != null) {
            query.setParameter("dateTo", dateTo);
        }
        if (branchId != null) {
            query.setParameter("branchId", branchId);
        }
        return query.getResultList();
    }

    public List<Object[]> fetchProfitLossByBranch(LocalDate dateFrom, LocalDate dateTo) {
        StringBuilder hql = new StringBuilder("""
                select
                    j.branchId,
                    a.accountType,
                    coalesce(sum(case
                        when upper(a.accountType) = 'INCOME' and upper(l.entrySide) = 'CREDIT' then l.amount
                        when upper(a.accountType) = 'INCOME' and upper(l.entrySide) = 'DEBIT' then -l.amount
                        when upper(a.accountType) = 'EXPENSE' and upper(l.entrySide) = 'DEBIT' then l.amount
                        when upper(a.accountType) = 'EXPENSE' and upper(l.entrySide) = 'CREDIT' then -l.amount
                        else 0 end), 0)
                from GlJournal j
                join GlJournalLine l on l.journalId = j.id
                join GlAccount a on upper(a.accountCode) = upper(l.accountCode)
                where j.status = :status
                  and upper(a.accountType) in ('INCOME', 'EXPENSE')
                """);
        if (dateFrom != null) {
            hql.append(" and j.journalDate >= :dateFrom");
        }
        if (dateTo != null) {
            hql.append(" and j.journalDate <= :dateTo");
        }
        hql.append("""
                
                group by j.branchId, a.accountType
                order by j.branchId asc
                """);
        TypedQuery<Object[]> query = entityManager.createQuery(hql.toString(), Object[].class)
                .setParameter("status", RecordStatus.ACTIVE);
        if (dateFrom != null) {
            query.setParameter("dateFrom", dateFrom);
        }
        if (dateTo != null) {
            query.setParameter("dateTo", dateTo);
        }
        return query.getResultList();
    }
}
