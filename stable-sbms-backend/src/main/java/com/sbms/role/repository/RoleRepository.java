package com.sbms.role.repository;

import com.sbms.role.entity.Role;
import com.sbms.role.enums.RoleStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class RoleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private Session currentSession() {
        return entityManager.unwrap(Session.class);
    }

    public Role save(Role role) {
        if (role.getId() == null) {
            currentSession().persist(role);
            return role;
        }
        return currentSession().merge(role);
    }

    public List<Role> findAll() {
        return currentSession()
                .createQuery("from Role r order by r.updatedAt desc, r.id desc", Role.class)
                .getResultList();
    }

    public List<Role> findActive() {
        return currentSession()
                .createQuery("from Role r where r.status = 'ACTIVE' order by r.name asc", Role.class)
                .getResultList();
    }

    public Optional<Role> findById(Long id) {
        return Optional.ofNullable(currentSession().find(Role.class, id));
    }

    public boolean existsByCode(String code) {
        Long count = currentSession()
                .createQuery(
                        "select count(r.id) from Role r where lower(r.code) = :code",
                        Long.class
                )
                .setParameter("code", code == null ? "" : code.trim().toLowerCase())
                .getSingleResult();
        return count != null && count > 0;
    }

    public boolean existsByCodeAndIdNot(String code, Long id) {
        Long count = currentSession()
                .createQuery(
                        "select count(r.id) from Role r where lower(r.code) = :code and r.id <> :id",
                        Long.class
                )
                .setParameter("code", code == null ? "" : code.trim().toLowerCase())
                .setParameter("id", id == null ? -1L : id)
                .getSingleResult();
        return count != null && count > 0;
    }

    public boolean existsByName(String name) {
        Long count = currentSession()
                .createQuery(
                        "select count(r.id) from Role r where lower(r.name) = :name",
                        Long.class
                )
                .setParameter("name", name == null ? "" : name.trim().toLowerCase())
                .getSingleResult();
        return count != null && count > 0;
    }

    public boolean existsByNameAndIdNot(String name, Long id) {
        Long count = currentSession()
                .createQuery(
                        "select count(r.id) from Role r where lower(r.name) = :name and r.id <> :id",
                        Long.class
                )
                .setParameter("name", name == null ? "" : name.trim().toLowerCase())
                .setParameter("id", id == null ? -1L : id)
                .getSingleResult();
        return count != null && count > 0;
    }

    public Optional<Role> findByCode(String code) {
        List<Role> roles = currentSession()
                .createQuery("from Role r where lower(r.code) = :code", Role.class)
                .setParameter("code", code == null ? "" : code.trim().toLowerCase())
                .setMaxResults(1)
                .getResultList();
        return roles.stream().findFirst();
    }

    public long countByStatus(String status) {
        RoleStatus resolved = RoleStatus.valueOf(status == null ? "ACTIVE" : status.trim().toUpperCase());
        Long count = currentSession()
                .createQuery("select count(r.id) from Role r where r.status = :status", Long.class)
                .setParameter("status", resolved)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    public List<Role> findRecent(int limit) {
        return currentSession()
                .createQuery("from Role r order by r.updatedAt desc, r.id desc", Role.class)
                .setMaxResults(limit)
                .getResultList();
    }
}
