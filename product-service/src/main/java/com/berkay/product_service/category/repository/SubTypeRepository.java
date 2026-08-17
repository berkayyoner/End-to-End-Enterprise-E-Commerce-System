package com.berkay.product_service.category.repository;

import com.berkay.product_service.category.entity.SubType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubTypeRepository extends JpaRepository<SubType, Long> {

	@Query("SELECT st FROM SubType st LEFT JOIN FETCH st.translations WHERE st.deleted = false ORDER BY st.id")
	List<SubType> findAllActive();

	@Query("SELECT st FROM SubType st LEFT JOIN FETCH st.translations WHERE st.id = :id AND st.deleted = false")
	Optional<SubType> findActiveById(@Param("id") Long id);

	@Query("SELECT st FROM SubType st LEFT JOIN FETCH st.translations WHERE st.mainCategory.id = :mainCategoryId AND st.deleted = false ORDER BY st.id")
	List<SubType> findActiveByMainCategoryId(@Param("mainCategoryId") Long mainCategoryId);

	boolean existsByIdAndDeletedFalse(Long id);
}
