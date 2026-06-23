package com.sbms.lookup.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.lookup.entity.LookupType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LookupTypeRepository extends JpaRepository<LookupType, Long> {
    Optional<LookupType> findByTypeCodeIgnoreCase(String typeCode);
    boolean existsByTypeCodeIgnoreCase(String typeCode);
    boolean existsByTypeCodeIgnoreCaseAndIdNot(String typeCode, Long id);
    List<LookupType> findByStatusOrderByTypeNameAsc(RecordStatus status);
    List<LookupType> findTop10ByOrderByUpdatedAtDesc();
}
