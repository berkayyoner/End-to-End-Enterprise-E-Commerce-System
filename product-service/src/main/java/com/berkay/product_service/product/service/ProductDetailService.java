package com.berkay.product_service.product.service;

import com.berkay.common.i18n.TranslationResolver;
import com.berkay.product_service.product.dto.ProductDetailDTO;
import com.berkay.product_service.product.dto.ProductNameDTO;
import com.berkay.product_service.product.dto.ProductPhotoDTO;
import com.berkay.product_service.product.dto.SellerBrandDTO;
import com.berkay.product_service.product.dto.SimpleProductDTO;
import com.berkay.product_service.product.entity.Product;
import com.berkay.product_service.product.exception.ProductNotFoundException;
import com.berkay.product_service.product.repository.ProductRepository;
import com.berkay.product_service.qna.dto.QnaQuestionResponse;
import com.berkay.product_service.qna.service.QnaService;
import com.berkay.product_service.review.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for aggregating comprehensive product detail information.
 * Combines core product data with related recommendations, marketplace info, and placeholders
 * for future phases (ratings/Q&A/campaigns).
 */
@Service
public class ProductDetailService {

	private static final int MAX_RELATED_PRODUCTS = 8;
	private static final int MAX_SIMILAR_PRODUCTS = 8;
	private static final int MAX_BRAND_STORES = 6;
	private static final int MAX_POPULAR_PAGES = 6;
	private static final int MAX_MIGHT_INTEREST = 6;
	private static final String DELIVERY_ESTIMATE = "3-7 days";

	private final ProductRepository productRepository;
	private final ReviewService reviewService;
	private final QnaService qnaService;

	public ProductDetailService(
			ProductRepository productRepository,
			ReviewService reviewService,
			QnaService qnaService) {
		this.productRepository = productRepository;
		this.reviewService = reviewService;
		this.qnaService = qnaService;
	}

	@Transactional(readOnly = true)
	public ProductDetailDTO getProductDetail(Long productId, String locale) {
		Product product = findActiveOrThrow(productId);

		// Core product data
		var resolved = TranslationResolver.resolve(product.getTranslations(), locale)
				.orElse(null);

		String name = resolved != null ? resolved.getName() : "Untranslated";
		String shortDescription = resolved != null ? resolved.getShortDescription() : null;
		String longDescription = resolved != null ? resolved.getLongDescription() : null;

		List<String> features = product.getKeyFeatures().stream()
				.map(kf -> kf.getFeatureText())
				.toList();

		List<ProductPhotoDTO> photoDTOs = product.getPhotos().stream()
				.sorted((p1, p2) -> Integer.compare(p1.getDisplayOrder(), p2.getDisplayOrder()))
				.map(ProductPhotoDTO::from)
				.toList();

		// Get all products in the same InnerType category for related recommendations
		List<Product> categoryProducts = productRepository.findActiveByInnerTypeId(
				product.getInnerType().getId());

		// Build related product lists (excluding current product)
		List<SimpleProductDTO> similarProducts = buildSimpleProductList(categoryProducts, productId, MAX_SIMILAR_PRODUCTS, locale);
		List<SimpleProductDTO> recommendedProducts = buildSimpleProductList(categoryProducts, productId, MAX_SIMILAR_PRODUCTS, locale);
		List<SimpleProductDTO> boughtTogetherProducts = buildSimpleProductList(categoryProducts, productId, MAX_RELATED_PRODUCTS, locale);

		// Build "might also interest you" (product names only, distinct from above if easy)
		List<ProductNameDTO> mightAlsoInterestYou = buildProductNameList(categoryProducts, productId, MAX_MIGHT_INTEREST);

		// Build "popular brands/stores" from distinct sellers in same category
		List<SellerBrandDTO> popularBrandsOrStores = buildSellerBrandList(categoryProducts, MAX_BRAND_STORES);

		// Build "popular pages" (product names + IDs from same category)
		List<ProductNameDTO> popularPages = buildProductNameList(categoryProducts, productId, MAX_POPULAR_PAGES);

		// Real Phase 6.1 data: ratings and Q&A
		double averageRating = reviewService.getAverageRating(productId);
		long ratingCountLong = reviewService.getRatingCount(productId);
		int ratingCount = (int) ratingCountLong;
		List<Object> qna = qnaService.getProductQuestions(productId).stream()
				.map(q -> (Object) q)
				.toList();

		// Placeholder for Phase 6.4 (campaigns)
		List<Object> campaigns = List.of();

		return new ProductDetailDTO(
				product.getId(),
				product.getSellerId(),
				product.getInnerType().getId(),
				product.getPrice(),
				product.getStock(),
				name,
				shortDescription,
				longDescription,
				features,
				photoDTOs,
				averageRating,
				ratingCount,
				qna,
				campaigns,
				similarProducts,
				recommendedProducts,
				boughtTogetherProducts,
				mightAlsoInterestYou,
				popularBrandsOrStores,
				popularPages,
				DELIVERY_ESTIMATE
		);
	}

	/**
	 * Build a list of SimpleProductDTO from category products, excluding the current product.
	 * Returns up to maxItems, using simple ID-based ordering.
	 */
	private List<SimpleProductDTO> buildSimpleProductList(List<Product> products, Long currentProductId, int maxItems, String locale) {
		return products.stream()
				.filter(p -> !p.getId().equals(currentProductId))
				.limit(maxItems)
				.map(p -> {
					var resolved = TranslationResolver.resolve(p.getTranslations(), locale)
							.orElse(null);
					String productName = resolved != null ? resolved.getName() : "Untranslated";

					// Get first photo URL if available
					String photoUrl = p.getPhotos().stream()
							.sorted((ph1, ph2) -> Integer.compare(ph1.getDisplayOrder(), ph2.getDisplayOrder()))
							.findFirst()
							.map(photo -> "/photo/" + photo.getId())  // Placeholder URL
							.orElse(null);

					return new SimpleProductDTO(
							p.getId(),
							productName,
							p.getPrice(),
							photoUrl,
							0.0  // Placeholder rating, will be populated by Phase 6.1
					);
				})
				.collect(Collectors.toList());
	}

	/**
	 * Build a list of ProductNameDTO from category products, excluding the current product.
	 * Returns up to maxItems, using simple ID-based ordering.
	 */
	private List<ProductNameDTO> buildProductNameList(List<Product> products, Long currentProductId, int maxItems) {
		return products.stream()
				.filter(p -> !p.getId().equals(currentProductId))
				.limit(maxItems)
				.map(p -> {
					// Get Turkish name as fallback, or any available translation
					var resolved = TranslationResolver.resolve(p.getTranslations(), "tr")
							.orElse(null);
					String productName = resolved != null ? resolved.getName() : "Untranslated";

					return new ProductNameDTO(p.getId(), productName);
				})
				.collect(Collectors.toList());
	}

	/**
	 * Build a list of distinct sellers/brands from category products.
	 * Returns up to maxItems unique sellers, using seller ID as placeholder name.
	 * TODO (Phase 4.1): Replace seller ID string with actual store name once available in auth-service.
	 */
	private List<SellerBrandDTO> buildSellerBrandList(List<Product> products, int maxItems) {
		Set<Long> seenSellers = new HashSet<>();
		return products.stream()
				.filter(p -> seenSellers.add(p.getSellerId()))  // Keep only first occurrence of each seller
				.limit(maxItems)
				.map(p -> new SellerBrandDTO(p.getSellerId(), "Seller " + p.getSellerId()))  // Placeholder: store name not yet available
				.collect(Collectors.toList());
	}

	private Product findActiveOrThrow(Long id) {
		return productRepository.findActiveById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
	}
}
