package com.berkay.product_service.category.repository;

import com.berkay.product_service.category.entity.InnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InnerTypeRepository extends JpaRepository<InnerType, Long> {

	@Query("SELECT it FROM InnerType it LEFT JOIN FETCH it.translations WHERE it.deleted = false ORDER BY it.id")
	List<InnerType> findAllActive();

	@Query("SELECT it FROM InnerType it LEFT JOIN FETCH it.translations WHERE it.id = :id AND it.deleted = false")
	Optional<InnerType> findActiveById(@Param("id") Long id);

	@Query("SELECT it FROM InnerType it LEFT JOIN FETCH it.translations WHERE it.subType.id = :subTypeId AND it.deleted = false ORDER BY it.id")
	List<InnerType> findActiveBySubTypeId(@Param("subTypeId") Long subTypeId);

	boolean existsByIdAndDeletedFalse(Long id);
}
