package com.sbms.user.repository;

import com.sbms.user.entity.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class UserRoleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public UserRole save(UserRole userRole) {
        if (userRole.getId() == null) {
            entityManager.persist(userRole);
            return userRole;
        }
        return entityManager.merge(userRole);
    }

    public List<UserRole> findByUserId(Long userId) {
        return entityManager.createQuery("from UserRole ur where ur.user.id = :userId order by ur.createdAt desc", UserRole.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public long countByUserId(Long userId) {
        Long count = entityManager.createQuery("select count(ur.id) from UserRole ur where ur.user.id = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
        return count == null ? 0L : count;
    }
}
