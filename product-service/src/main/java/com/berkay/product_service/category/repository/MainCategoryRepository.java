package com.berkay.product_service.category.repository;

import com.berkay.product_service.category.entity.MainCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {

	@Query("SELECT mc FROM MainCategory mc LEFT JOIN FETCH mc.translations WHERE mc.deleted = false ORDER BY mc.id")
	List<MainCategory> findAllActive();

	@Query("SELECT mc FROM MainCategory mc LEFT JOIN FETCH mc.translations WHERE mc.id = :id AND mc.deleted = false")
	Optional<MainCategory> findActiveById(@Param("id") Long id);

	boolean existsByIdAndDeletedFalse(Long id);
}
