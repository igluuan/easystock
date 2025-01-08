package br.devluan.easystock.services;

import br.devluan.easystock.dto.ProductDTO.ProductCreationDTO;
import br.devluan.easystock.dto.ProductDTO.ProductResponseDTO;
import br.devluan.easystock.dto.UserDTO.PageResponseDTO;
import br.devluan.easystock.entities.Category;
import br.devluan.easystock.entities.Product;
import br.devluan.easystock.exceptions.ResourceNotFoundException;
import br.devluan.easystock.mappers.ProductMapper;
import br.devluan.easystock.repositories.CategoryRepository;
import br.devluan.easystock.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductResponseDTO createProduct(ProductCreationDTO productDTO){
        logger.info("Creating new product {}", productDTO.name());
        Category category = categoryRepository.findById(productDTO.categoryId())
                .orElseThrow(()-> new ResourceNotFoundException("Category not found"));

        Product product = productMapper.toEntity(productDTO);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        logger.info("New product created {}", savedProduct.getName());

        return productMapper.toResponseDTO(savedProduct);
    }

    public ProductResponseDTO getById(Long ProductId){
        logger.info("Retrieving product by id {}", ProductId);
        Product existingProduct = productRepository.findById(ProductId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return productMapper.toResponseDTO(existingProduct);
    }

    public PageResponseDTO<ProductResponseDTO> getAllProducts(int page, int size){
        Page<Product> products = productRepository.findAll(PageRequest.of(page,size));
        Page<ProductResponseDTO> productResponseDTOS = products.map(productMapper::toResponseDTO);
        return PageResponseDTO.from(productResponseDTOS);
    }
}
