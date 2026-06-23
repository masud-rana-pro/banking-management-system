package com.sbms.statement.repository;

import com.sbms.statement.entity.FileReference;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class FileReferenceRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public FileReference save(FileReference entity) {
        entityManager.persist(entity);
        return entity;
    }

    public FileReference update(FileReference entity) {
        return entityManager.merge(entity);
    }

    public Optional<FileReference> findById(Long id) {
        try {
            return Optional.of(entityManager.createQuery(
                            "SELECT f FROM FileReference f WHERE f.id = :id",
                            FileReference.class
                    )
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<FileReference> findAll() {
        return entityManager.createQuery(
                        "SELECT f FROM FileReference f ORDER BY f.id DESC",
                        FileReference.class
                )
                .getResultList();
    }

    public Long countCreatedBetween(LocalDateTime fromTime, LocalDateTime toTime) {
        return entityManager.createQuery(
                        "SELECT COUNT(f.id) FROM FileReference f WHERE f.createdAt BETWEEN :fromTime AND :toTime",
                        Long.class
                )
                .setParameter("fromTime", fromTime)
                .setParameter("toTime", toTime)
                .getSingleResult();
    }
}
