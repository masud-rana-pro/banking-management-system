package com.sbms.user.repository;

import com.sbms.user.entity.UserSession;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserSessionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public UserSession save(UserSession session) {
        if (session.getId() == null) {
            entityManager.persist(session);
            return session;
        }
        return entityManager.merge(session);
    }

    public List<UserSession> findByUserId(Long userId) {
        return entityManager.createQuery("from UserSession us where us.user.id = :userId order by us.loginTime desc", UserSession.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<UserSession> findRecent(int limit) {
        return entityManager.createQuery("from UserSession us order by us.loginTime desc", UserSession.class)
                .setMaxResults(limit)
                .getResultList();
    }

    public Optional<UserSession> findActiveByToken(String token) {
        List<UserSession> sessions = entityManager
                .createQuery("select us from UserSession us join fetch us.user u left join fetch u.role where us.jwtId = :token and us.logoutTime is null order by us.loginTime desc", UserSession.class)
                .setParameter("token", token)
                .setMaxResults(1)
                .getResultList();
        return sessions.stream().findFirst();
    }

    public long countByUserId(Long userId) {
        Long count = entityManager.createQuery("select count(us.id) from UserSession us where us.user.id = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
        return count == null ? 0L : count;
    }
}
