package com.berkay.product_service.category.changerequest.repository;

import com.berkay.product_service.category.changerequest.entity.CategoryChangeRequest;
import com.berkay.product_service.category.changerequest.entity.CategoryChangeRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryChangeRequestRepository extends JpaRepository<CategoryChangeRequest, Long> {

	Page<CategoryChangeRequest> findAllByStatus(CategoryChangeRequestStatus status, Pageable pageable);

	Optional<CategoryChangeRequest> findById(Long id);
}
