package com.sbms.lookup.repository;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.lookup.entity.LookupValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LookupValueRepository extends JpaRepository<LookupValue, Long> {

    @Query("""
            SELECT v FROM LookupValue v
            WHERE (:typeId IS NULL OR v.lookupType.id = :typeId)
              AND (:keyword IS NULL OR LOWER(v.valueCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(v.valueLabel) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(COALESCE(v.valueBnLabel, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY v.lookupType.typeName ASC, COALESCE(v.sortOrder, 999999) ASC, v.valueLabel ASC
            """)
    List<LookupValue> search(@Param("typeId") Long typeId, @Param("keyword") String keyword);

    @Query("""
            SELECT v FROM LookupValue v
            WHERE UPPER(v.lookupType.typeCode) = UPPER(:typeCode)
            ORDER BY COALESCE(v.sortOrder, 999999) ASC, v.valueLabel ASC
            """)
    List<LookupValue> findByTypeCode(@Param("typeCode") String typeCode);

    @Query("""
            SELECT v FROM LookupValue v
            WHERE UPPER(v.lookupType.typeCode) = UPPER(:typeCode)
              AND v.status = :status
            ORDER BY COALESCE(v.sortOrder, 999999) ASC, v.valueLabel ASC
            """)
    List<LookupValue> findByTypeCodeAndStatus(@Param("typeCode") String typeCode, @Param("status") RecordStatus status);

    boolean existsByLookupTypeIdAndValueCodeIgnoreCase(Long lookupTypeId, String valueCode);
    boolean existsByLookupTypeIdAndValueCodeIgnoreCaseAndIdNot(Long lookupTypeId, String valueCode, Long id);

    long countByStatus(RecordStatus status);
    long countByUpdatedAtAfter(LocalDateTime dateTime);
    long countByLookupTypeIdAndStatus(Long lookupTypeId, RecordStatus status);
    long countByLookupTypeId(Long lookupTypeId);
    List<LookupValue> findTop10ByOrderByUpdatedAtDesc();
}
