package br.devluan.easystock.services;

import br.devluan.easystock.dto.request.ProductCreationDTO;
import br.devluan.easystock.dto.request.ProductUpdateDTO;
import br.devluan.easystock.dto.response.ProductResponseDTO;
import br.devluan.easystock.dto.response.PageResponseDTO;
import br.devluan.easystock.domain.entities.Category;
import br.devluan.easystock.domain.entities.Product;
import br.devluan.easystock.domain.exceptions.ProductExceptions.BusinessException;
import br.devluan.easystock.domain.exceptions.ProductExceptions.ResourceNotFoundException;
import br.devluan.easystock.mappers.ProductMapper;
import br.devluan.easystock.repositories.CategoryRepository;
import br.devluan.easystock.repositories.ProductRepository;
import br.devluan.easystock.services.utils.ProductValidator;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ProductValidator validator;

    public ProductResponseDTO createProduct(ProductCreationDTO productDTO){
        validator.validateProductCreation(productDTO);
        logger.info("Creating new product {}", productDTO.name());
        Category category = categoryRepository.findById(productDTO.categoryId())
                .orElseThrow(()-> new ResourceNotFoundException("Category not found"));
        try {
            Product product = productMapper.toEntity(productDTO);
            product.setCategory(category);
            Product savedProduct = productRepository.save(product);
            logger.info("New product created {}", savedProduct.getName());
            return productMapper.toResponseDTO(savedProduct);
        } catch (DataIntegrityViolationException e) {
            logger.error("Error creating product: {}", e.getMessage());
            throw new BusinessException("Error creating product. Possible unique data breach.");
        }


    }

    public ProductResponseDTO updateQuantity(Long productId, ProductUpdateDTO productUpdateDTO) {
        Optional<Product> optionalProduct = Optional.ofNullable(findProductById(productId));

        Product product = optionalProduct.orElseThrow(() -> new BusinessException("Product not found"));

        validator.validateProductUpdate(productUpdateDTO, productId);

        try {
            Integer updatedQuantity = product.getStockQuantity() + productUpdateDTO.stockQuantity();
            System.out.println(updatedQuantity);
            product.setStockQuantity(updatedQuantity);
            productRepository.save(product);
            logger.info("Updated stock quantity for product ID {}. New quantity: {}", productId, updatedQuantity);
            return productMapper.toResponseDTO(product);
        } catch (DataIntegrityViolationException e) {
            logger.error("Error updating product ID {}: {}", productId, e.getMessage());
            throw new BusinessException("Error updating product. Possible unique data breach.");
        }
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getById(Long productId) {
        return productMapper.toResponseDTO(findProductById(productId));
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<ProductResponseDTO> getAllProducts(int page, int size){
        validatePaginationParams(page, size);
        try {
            Page<Product> products = productRepository.findAll(
                    PageRequest.of(page, size, Sort.by("createdAt").descending())
            );
            return PageResponseDTO.from(products.map(productMapper::toResponseDTO));
        } catch (Exception e) {
            logger.error("Error searching for products: {}", e.getMessage());
            throw new BusinessException("Error retrieving product list");
        }
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<ProductResponseDTO> getProductsByCategory(Long categoryId, int page, int size){
        validatePaginationParams(page, size);
        logger.info("Retrieving products by category id {}", categoryId);
        Category category = findCategoryById(categoryId);
        try {
            Page<Product> products = productRepository.findByCategory(
                    category,
                    PageRequest.of(page, size, Sort.by("createdAt").descending())
            );
            return PageResponseDTO.from(products.map(productMapper::toResponseDTO));
        }catch (Exception e) {
            logger.error("Error searching for products by category: {}", e.getMessage());
            throw new BusinessException("Error retrieving products from category");
        }
    }

    public void deactivateProduct(Long productId){
        logger.info("Deactivating product by id {}", productId);
        Product product = findProductById(productId);
        try {
            product.setActive(false);
            product.setUpdatedAt(LocalDateTime.now());
            productRepository.save(product);
            logger.info("Product successfully deactivated. ID: {}", productId);
        } catch (Exception e) {
            logger.error("Error deactivating product: {}", e.getMessage());
            throw new BusinessException("Error deactivating product");
        }
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.warn("Product not found. ID: {}", productId);
                    return new ResourceNotFoundException("Product not found");
                });
    }

    private Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    logger.warn("Category not found. ID: {}", categoryId);
                    return new ResourceNotFoundException("Category not found");
                });
    }

    private void validatePaginationParams(int page, int size) {
        if (page < 0 || size <= 0 || size > 100) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }
    }
}
