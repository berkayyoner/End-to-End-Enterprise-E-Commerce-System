package com.berkay.product_service.product.service;

import com.berkay.product_service.category.entity.InnerType;
import com.berkay.product_service.category.entity.SubType;
import com.berkay.product_service.category.entity.MainCategory;
import com.berkay.product_service.category.repository.InnerTypeRepository;
import com.berkay.product_service.product.dto.ProductRequest;
import com.berkay.product_service.product.dto.ProductResponse;
import com.berkay.product_service.product.dto.ProductTranslationInput;
import com.berkay.product_service.product.entity.Product;
import com.berkay.product_service.product.exception.ProductNotFoundException;
import com.berkay.product_service.product.exception.ProductOwnershipException;
import com.berkay.product_service.product.repository.ProductRepository;
import com.berkay.product_service.product.search.service.ProductSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private InnerTypeRepository innerTypeRepository;

	@Mock
	private ProductSearchService searchService;

	private ProductService service;
	private MainCategory mainCategory;
	private SubType subType;
	private InnerType innerType;

	@BeforeEach
	void setUp() {
		service = new ProductService(productRepository, innerTypeRepository, searchService);
		mainCategory = new MainCategory("PLACEHOLDER");
		subType = new SubType(mainCategory, "PLACEHOLDER");
		innerType = new InnerType(subType, "PLACEHOLDER");
	}

	@Test
	void createsProductWithTranslationsSuccessfully() {
		Long sellerId = 1L;
		ProductRequest request = new ProductRequest(
				innerType.getId(),
				new BigDecimal("99.99"),
				100,
				List.of(
						new ProductTranslationInput("tr", "Laptop", "Güzel bir laptop", "Çok iyi ve kaliteli..."),
						new ProductTranslationInput("en", "Laptop", "A nice laptop", "Very good and quality...")
				),
				List.of("Feature 1", "Feature 2"),
				List.of()
		);

		when(innerTypeRepository.findActiveById(innerType.getId())).thenReturn(Optional.of(innerType));
		Product saved = new Product(sellerId, innerType, request.price(), request.stock());
		when(productRepository.save(any(Product.class))).thenReturn(saved);

		ProductResponse response = service.create(sellerId, request);

		assertThat(response).isNotNull();
		assertThat(response.sellerId()).isEqualTo(sellerId);
		assertThat(response.price()).isEqualTo(new BigDecimal("99.99"));
		assertThat(response.stock()).isEqualTo(100);
		verify(productRepository).save(any(Product.class));
	}

	@Test
	void throwsExceptionWhenProductNotFound() {
		when(productRepository.findActiveById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.get(99L, "tr"))
				.isInstanceOf(ProductNotFoundException.class)
				.hasMessageContaining("Product not found");
	}

	@Test
	void throwsExceptionWhenSellerTriesToEditOthersProduct() {
		Long sellerId = 1L;
		Long otherSellerId = 2L;

		Product product = new Product(otherSellerId, innerType, new BigDecimal("99.99"), 100);
		when(productRepository.findActiveById(product.getId())).thenReturn(Optional.of(product));

		ProductRequest request = new ProductRequest(
				innerType.getId(),
				new BigDecimal("49.99"),
				50,
				List.of(new ProductTranslationInput("tr", "Updated", "Updated", "Updated")),
				List.of(),
				List.of()
		);

		assertThatThrownBy(() -> service.update(product.getId(), sellerId, request))
				.isInstanceOf(ProductOwnershipException.class)
				.hasMessageContaining("only modify your own products");
	}

	@Test
	void throwsExceptionWhenSellerTriesToDeleteOthersProduct() {
		Long sellerId = 1L;
		Long otherSellerId = 2L;

		Product product = new Product(otherSellerId, innerType, new BigDecimal("99.99"), 100);
		when(productRepository.findActiveById(product.getId())).thenReturn(Optional.of(product));

		assertThatThrownBy(() -> service.delete(product.getId(), sellerId))
				.isInstanceOf(ProductOwnershipException.class)
				.hasMessageContaining("only modify your own products");
	}

	@Test
	void throwsExceptionWhenTryingToAddMoreThanTenPhotos() {
		Long sellerId = 1L;

		var photos = java.util.stream.IntStream.range(0, 11)
				.mapToObj(i -> new com.berkay.product_service.product.dto.ProductPhotoUpload(
						"iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
						i % 10
				))
				.toList();

		ProductRequest request = new ProductRequest(
				innerType.getId(),
				new BigDecimal("99.99"),
				100,
				List.of(new ProductTranslationInput("tr", "Product", "Short", "Long")),
				List.of(),
				photos
		);

		when(innerTypeRepository.findActiveById(innerType.getId())).thenReturn(Optional.of(innerType));

		assertThatThrownBy(() -> service.create(sellerId, request))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Cannot add more than 10 photos");
	}
}
