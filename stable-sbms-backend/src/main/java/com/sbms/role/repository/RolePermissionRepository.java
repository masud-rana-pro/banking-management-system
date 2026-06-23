package com.sbms.role.repository;

import com.sbms.role.entity.RolePermission;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class RolePermissionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private Session currentSession() {
        return entityManager.unwrap(Session.class);
    }

    public RolePermission save(RolePermission entity) {
        if (entity.getId() == null) {
            currentSession().persist(entity);
            return entity;
        }
        return currentSession().merge(entity);
    }

    public List<RolePermission> findByRoleId(Long roleId) {
        return currentSession()
                .createQuery("from RolePermission rp where rp.role.id = :roleId order by rp.moduleName asc, rp.actionName asc", RolePermission.class)
                .setParameter("roleId", roleId)
                .getResultList();
    }

    public long countByRoleId(Long roleId) {
        Long count = currentSession()
                .createQuery("select count(rp.id) from RolePermission rp where rp.role.id = :roleId and rp.allowFlag = true", Long.class)
                .setParameter("roleId", roleId)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    public void deleteByRoleId(Long roleId) {
        currentSession()
                .createMutationQuery("delete from RolePermission rp where rp.role.id = :roleId")
                .setParameter("roleId", roleId)
                .executeUpdate();
    }
}
