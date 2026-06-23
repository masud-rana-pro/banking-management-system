package com.sbms.user.repository;

import com.sbms.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private Session currentSession() {
        return entityManager.unwrap(Session.class);
    }

    public User save(User user) {
        if (user.getId() == null) {
            currentSession().persist(user);
            return user;
        }
        return currentSession().merge(user);
    }

    public List<User> findAll() {
        return findAll(null, null, null, null);
    }

    public List<User> findAll(String keyword, String status, Long roleId, Long branchId) {
        StringBuilder hql = new StringBuilder("from User u where 1=1");
        if (keyword != null && !keyword.isBlank()) {
            hql.append(" and (lower(u.username) like :keyword or lower(u.fullName) like :keyword or lower(coalesce(u.email, '')) like :keyword or lower(coalesce(u.employeeNo, '')) like :keyword)");
        }
        if (status != null && !status.isBlank()) {
            hql.append(" and upper(cast(u.status as string)) = :status");
        }
        if (roleId != null) {
            hql.append(" and u.role.id = :roleId");
        }
        if (branchId != null) {
            hql.append(" and u.branchId = :branchId");
        }
        hql.append(" order by u.id desc");
        TypedQuery<User> query = currentSession().createQuery(hql.toString(), User.class);
        if (keyword != null && !keyword.isBlank()) query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        if (status != null && !status.isBlank()) query.setParameter("status", status.trim().toUpperCase());
        if (roleId != null) query.setParameter("roleId", roleId);
        if (branchId != null) query.setParameter("branchId", branchId);
        return query.getResultList();
    }

    public List<User> search(String keyword) {
        return findAll(keyword, null, null, null);
    }

    public List<User> findActiveDropdown() {
        return currentSession()
                .createQuery("from User u where upper(cast(u.status as string)) = 'ACTIVE' and u.active = true and u.locked = false order by u.fullName asc", User.class)
                .getResultList();
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(currentSession().find(User.class, id));
    }

    public Optional<User> findByUsername(String username) {
        List<User> users = currentSession()
                .createQuery("from User u where lower(u.username) = :username", User.class)
                .setParameter("username", username.trim().toLowerCase())
                .setMaxResults(1)
                .getResultList();
        return users.stream().findFirst();
    }

    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return Optional.empty();
        List<User> users = currentSession()
                .createQuery("from User u where lower(u.email) = :email", User.class)
                .setParameter("email", email.trim().toLowerCase())
                .setMaxResults(1)
                .getResultList();
        return users.stream().findFirst();
    }

    public Optional<User> findByMobile(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) return Optional.empty();
        List<User> users = currentSession()
                .createQuery("from User u where u.mobile = :mobile", User.class)
                .setParameter("mobile", mobile.trim())
                .setMaxResults(1)
                .getResultList();
        return users.stream().findFirst();
    }

    public boolean existsById(Long id) {
        Long count = currentSession()
                .createQuery("select count(u.id) from User u where u.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return count != null && count > 0;
    }

    public boolean existsByUsername(String username, Long exceptId) {
        String hql = "select count(u.id) from User u where lower(u.username) = :username and (:exceptId is null or u.id <> :exceptId)";
        Long count = currentSession().createQuery(hql, Long.class)
                .setParameter("username", username.trim().toLowerCase())
                .setParameter("exceptId", exceptId)
                .getSingleResult();
        return count != null && count > 0;
    }

    public boolean existsByEmail(String email, Long exceptId) {
        if (email == null || email.trim().isEmpty()) return false;
        String hql = "select count(u.id) from User u where lower(u.email) = :email and (:exceptId is null or u.id <> :exceptId)";
        Long count = currentSession().createQuery(hql, Long.class)
                .setParameter("email", email.trim().toLowerCase())
                .setParameter("exceptId", exceptId)
                .getSingleResult();
        return count != null && count > 0;
    }

    public boolean existsByMobile(String mobile, Long exceptId) {
        if (mobile == null || mobile.trim().isEmpty()) return false;
        String hql = "select count(u.id) from User u where u.mobile = :mobile and (:exceptId is null or u.id <> :exceptId)";
        Long count = currentSession().createQuery(hql, Long.class)
                .setParameter("mobile", mobile.trim())
                .setParameter("exceptId", exceptId)
                .getSingleResult();
        return count != null && count > 0;
    }

    public long countAll() {
        Long count = currentSession().createQuery("select count(u.id) from User u", Long.class).getSingleResult();
        return count == null ? 0L : count;
    }

    public long countByStatus(String status) {
        Long count = currentSession()
                .createQuery("select count(u.id) from User u where upper(cast(u.status as string)) = :status", Long.class)
                .setParameter("status", status.trim().toUpperCase())
                .getSingleResult();
        return count == null ? 0L : count;
    }

    public long countLocked() {
        Long count = currentSession()
                .createQuery("select count(u.id) from User u where u.locked = true or upper(cast(u.status as string)) = 'LOCKED'", Long.class)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    public long countByRoleId(Long roleId) {
        Long count = currentSession()
                .createQuery("select count(u.id) from User u where u.role.id = :roleId", Long.class)
                .setParameter("roleId", roleId)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    public long countActiveByRoleId(Long roleId) {
        Long count = currentSession()
                .createQuery("select count(u.id) from User u where u.role.id = :roleId and upper(cast(u.status as string)) = 'ACTIVE'", Long.class)
                .setParameter("roleId", roleId)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    public List<String> findActiveOperationalAlertEmails() {
        return currentSession().createQuery("""
                        select distinct lower(trim(u.email))
                        from User u
                        join u.role r
                        where u.active = true
                          and u.locked = false
                          and upper(cast(u.status as string)) = 'ACTIVE'
                          and u.email is not null
                          and trim(u.email) <> ''
                          and upper(r.code) in (
                              'SYSTEM_ADMIN',
                              'BRANCH_MANAGER',
                              'OPERATIONS_OFFICER',
                              'COMPLIANCE_OFFICER',
                              'INTERNAL_AUDITOR',
                              'MIS_OFFICER',
                              'TREASURY_FINANCE_OFFICER'
                          )
                        order by lower(trim(u.email))
                        """, String.class)
                .getResultList();
    }
}
