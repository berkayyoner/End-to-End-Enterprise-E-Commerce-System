package com.berkay.product_service.product.service;

import com.berkay.product_service.category.entity.InnerType;
import com.berkay.product_service.category.entity.MainCategory;
import com.berkay.product_service.category.entity.SubType;
import com.berkay.product_service.product.dto.ProductDetailDTO;
import com.berkay.product_service.product.entity.Product;
import com.berkay.product_service.product.entity.ProductKeyFeature;
import com.berkay.product_service.product.entity.ProductPhoto;
import com.berkay.product_service.product.entity.ProductTranslation;
import com.berkay.product_service.product.exception.ProductNotFoundException;
import com.berkay.product_service.campaign.service.CampaignService;
import com.berkay.product_service.product.repository.ProductRepository;
import com.berkay.product_service.qna.service.QnaService;
import com.berkay.product_service.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductDetailServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ReviewService reviewService;

	@Mock
	private QnaService qnaService;

	@Mock
	private CampaignService campaignService;

	private ProductDetailService service;
	private MainCategory mainCategory;
	private SubType subType;
	private InnerType innerType;

	@BeforeEach
	void setUp() {
		service = new ProductDetailService(productRepository, reviewService, qnaService, campaignService);
		mainCategory = new MainCategory("Electronics");
		subType = new SubType(mainCategory, "Computers");
		innerType = new InnerType(subType, "Laptops");
	}

	@Test
	void assemblesDetailResponseCorrectlyWithPhotosAndTranslations() {
		// Setup product with full data
		Product product = new Product(1L, innerType, new BigDecimal("999.99"), 100);
		setIdUsingReflection(product, 1L);

		// Add translations
		ProductTranslation trTranslation = new ProductTranslation(
				product, "tr", "Laptop Pro", "Güzel bir laptop", "Çok iyi kaliteli laptop..."
		);
		ProductTranslation enTranslation = new ProductTranslation(
				product, "en", "Laptop Pro", "A nice laptop", "Very good quality laptop..."
		);
		product.addTranslation(trTranslation);
		product.addTranslation(enTranslation);

		// Add photos
		ProductPhoto photo1 = new ProductPhoto(product, new byte[]{1, 2, 3}, 0);
		ProductPhoto photo2 = new ProductPhoto(product, new byte[]{4, 5, 6}, 1);
		product.addPhoto(photo1);
		product.addPhoto(photo2);

		// Add key features
		ProductKeyFeature feature1 = new ProductKeyFeature(product, "16GB RAM");
		ProductKeyFeature feature2 = new ProductKeyFeature(product, "512GB SSD");
		product.addKeyFeature(feature1);
		product.addKeyFeature(feature2);

		when(productRepository.findActiveById(1L)).thenReturn(Optional.of(product));
		when(productRepository.findActiveByInnerTypeId(innerType.getId())).thenReturn(List.of(product));
		when(campaignService.getCampaignsByProduct(1L)).thenReturn(List.of());

		ProductDetailDTO detail = service.getProductDetail(1L, "tr");

		assertThat(detail).isNotNull();
		assertThat(detail.id()).isEqualTo(1L);
		assertThat(detail.name()).isEqualTo("Laptop Pro");
		assertThat(detail.shortDescription()).isEqualTo("Güzel bir laptop");
		assertThat(detail.longDescription()).isEqualTo("Çok iyi kaliteli laptop...");
		assertThat(detail.price()).isEqualTo(new BigDecimal("999.99"));
		assertThat(detail.stock()).isEqualTo(100);
		assertThat(detail.keyFeatures()).hasSize(2).contains("16GB RAM", "512GB SSD");
		assertThat(detail.photos()).hasSize(2);
		assertThat(detail.estimatedDeliveryDays()).isEqualTo("3-7 days");
		assertThat(detail.averageRating()).isEqualTo(0.0);
		assertThat(detail.ratingCount()).isEqualTo(0);
		assertThat(detail.qna()).isEmpty();
		assertThat(detail.campaigns()).isEmpty();
	}

	@Test
	void excludesCurrentProductFromSimilarAndRecommendedLists() {
		// Setup: current product + 3 other products in same category
		Product currentProduct = new Product(1L, innerType, new BigDecimal("999.99"), 100);
		setIdUsingReflection(currentProduct, 1L);
		currentProduct.addTranslation(new ProductTranslation(currentProduct, "tr", "Laptop Pro", "Short", "Long"));

		Product product2 = new Product(2L, innerType, new BigDecimal("799.99"), 50);
		setIdUsingReflection(product2, 2L);
		product2.addTranslation(new ProductTranslation(product2, "tr", "Laptop Standard", "Short", "Long"));

		Product product3 = new Product(3L, innerType, new BigDecimal("599.99"), 75);
		setIdUsingReflection(product3, 3L);
		product3.addTranslation(new ProductTranslation(product3, "tr", "Laptop Budget", "Short", "Long"));

		List<Product> categoryProducts = List.of(currentProduct, product2, product3);

		when(productRepository.findActiveById(1L)).thenReturn(Optional.of(currentProduct));
		when(productRepository.findActiveByInnerTypeId(innerType.getId())).thenReturn(categoryProducts);
		when(campaignService.getCampaignsByProduct(1L)).thenReturn(List.of());

		ProductDetailDTO detail = service.getProductDetail(1L, "tr");

		// Verify similar products exclude current product
		assertThat(detail.similarProducts()).hasSize(2);
		assertThat(detail.similarProducts().stream().map(p -> p.id()).toList())
				.doesNotContain(1L)
				.contains(2L, 3L);

		// Verify recommended products exclude current product
		assertThat(detail.recommendedProducts()).hasSize(2);
		assertThat(detail.recommendedProducts().stream().map(p -> p.id()).toList())
				.doesNotContain(1L)
				.contains(2L, 3L);

		// Verify "might also interest you" excludes current product
		assertThat(detail.mightAlsoInterestYou()).hasSize(2);
		assertThat(detail.mightAlsoInterestYou().stream().map(p -> p.id()).toList())
				.doesNotContain(1L)
				.contains(2L, 3L);

		// Verify popular pages excludes current product
		assertThat(detail.popularPages()).hasSize(2);
		assertThat(detail.popularPages().stream().map(p -> p.id()).toList())
				.doesNotContain(1L);
	}

	@Test
	void respectsMaximumLimitsForRelatedProducts() {
		// Setup: current product + 10 other products
		Product currentProduct = new Product(1L, innerType, new BigDecimal("999.99"), 100);
		setIdUsingReflection(currentProduct, 1L);
		currentProduct.addTranslation(new ProductTranslation(currentProduct, "tr", "Main Laptop", "Short", "Long"));

		List<Product> categoryProducts = new ArrayList<>();
		categoryProducts.add(currentProduct);

		for (int i = 2; i <= 11; i++) {
			Product p = new Product(Long.valueOf(i), innerType, new BigDecimal("500"), 50);
			setIdUsingReflection(p, Long.valueOf(i));
			p.addTranslation(new ProductTranslation(p, "tr", "Laptop " + i, "Short", "Long"));
			categoryProducts.add(p);
		}

		when(productRepository.findActiveById(1L)).thenReturn(Optional.of(currentProduct));
		when(productRepository.findActiveByInnerTypeId(innerType.getId())).thenReturn(categoryProducts);
		when(campaignService.getCampaignsByProduct(1L)).thenReturn(List.of());

		ProductDetailDTO detail = service.getProductDetail(1L, "tr");

		// Similar/recommended products have max 8 items (per constant)
		assertThat(detail.similarProducts()).hasSize(8);
		assertThat(detail.recommendedProducts()).hasSize(8);
		// BoughtTogether also has max 8
		assertThat(detail.boughtTogetherProducts()).hasSize(8);

		// "Might also interest you" has different max (6 items)
		assertThat(detail.mightAlsoInterestYou()).hasSize(6);
		// Popular pages has max 6
		assertThat(detail.popularPages()).hasSize(6);
	}

	@Test
	void returnsDistinctSellersForPopularBrands() {
		// Setup: products from 3 different sellers
		Product product1 = new Product(10L, innerType, new BigDecimal("999.99"), 100);
		setIdUsingReflection(product1, 1L);
		product1.addTranslation(new ProductTranslation(product1, "tr", "Laptop 1", "Short", "Long"));

		Product product2 = new Product(10L, innerType, new BigDecimal("799.99"), 50);
		setIdUsingReflection(product2, 2L);
		product2.addTranslation(new ProductTranslation(product2, "tr", "Laptop 2", "Short", "Long"));

		Product product3 = new Product(20L, innerType, new BigDecimal("599.99"), 75);
		setIdUsingReflection(product3, 3L);
		product3.addTranslation(new ProductTranslation(product3, "tr", "Laptop 3", "Short", "Long"));

		Product product4 = new Product(30L, innerType, new BigDecimal("499.99"), 60);
		setIdUsingReflection(product4, 4L);
		product4.addTranslation(new ProductTranslation(product4, "tr", "Laptop 4", "Short", "Long"));

		List<Product> categoryProducts = List.of(product1, product2, product3, product4);

		when(productRepository.findActiveById(1L)).thenReturn(Optional.of(product1));
		when(productRepository.findActiveByInnerTypeId(innerType.getId())).thenReturn(categoryProducts);
		when(campaignService.getCampaignsByProduct(1L)).thenReturn(List.of());

		ProductDetailDTO detail = service.getProductDetail(1L, "tr");

		// Should have 3 distinct sellers (10L, 20L, 30L)
		assertThat(detail.popularBrandsOrStores()).hasSize(3);
		assertThat(detail.popularBrandsOrStores().stream().map(s -> s.sellerId()).toList())
				.containsExactly(10L, 20L, 30L);
	}

	@Test
	void throwsExceptionWhenProductNotFound() {
		when(productRepository.findActiveById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getProductDetail(99L, "tr"))
				.isInstanceOf(ProductNotFoundException.class)
				.hasMessageContaining("Product not found");
	}

	@Test
	void usesEnglishTranslationWhenRequested() {
		Product product = new Product(1L, innerType, new BigDecimal("999.99"), 100);
		setIdUsingReflection(product, 1L);

		ProductTranslation enTranslation = new ProductTranslation(
				product, "en", "Laptop Pro", "A nice laptop", "Very good quality..."
		);
		product.addTranslation(enTranslation);

		when(productRepository.findActiveById(1L)).thenReturn(Optional.of(product));
		when(productRepository.findActiveByInnerTypeId(innerType.getId())).thenReturn(List.of(product));
		when(campaignService.getCampaignsByProduct(1L)).thenReturn(List.of());

		ProductDetailDTO detail = service.getProductDetail(1L, "en");

		assertThat(detail.name()).isEqualTo("Laptop Pro");
		assertThat(detail.shortDescription()).isEqualTo("A nice laptop");
	}

	/**
	 * Helper method to set the ID on an entity via reflection, since the Product entity
	 * doesn't expose an ID setter (BaseEntity uses JPA to manage it).
	 */
	private void setIdUsingReflection(Product product, Long id) {
		try {
			java.lang.reflect.Field idField = product.getClass().getSuperclass()
					.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(product, id);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("Failed to set product ID for testing", e);
		}
	}
}
