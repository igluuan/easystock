package br.devluan.easystock.controllers;

import br.devluan.easystock.dto.request.ProductCreationDTO;
import br.devluan.easystock.dto.request.ProductUpdateDTO;
import br.devluan.easystock.dto.response.ProductResponseDTO;
import br.devluan.easystock.dto.response.PageResponseDTO;
import br.devluan.easystock.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping("create")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ProductResponseDTO>create(@RequestBody ProductCreationDTO productCreationDTO){
        ProductResponseDTO newProduct = productService.createProduct(productCreationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @PutMapping("/update/{productId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ProductResponseDTO> updateQuantity(@PathVariable Long productId,
                                                             @RequestBody ProductUpdateDTO dto){
        ProductResponseDTO updatedProduct = productService.updateQuantity(productId, dto);
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable Long productId){
        ProductResponseDTO productResponseDTO = productService.getById(productId);
        return ResponseEntity.ok(productResponseDTO);
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<ProductResponseDTO>> getProducts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                           @RequestParam(value = "size", defaultValue = "10") int size){
        return ResponseEntity.ok(productService.getAllProducts(page, size));
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<PageResponseDTO<ProductResponseDTO>> getProductsByCategory(@PathVariable Long categoryId,
                                                                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                     @RequestParam(value = "size", defaultValue = "10") int size){
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, page, size));
    }
    @PatchMapping("/disable/{productId}")
    public ResponseEntity<Void> deactivateProduct(@PathVariable Long productId){
        productService.deactivateProduct(productId);
        return ResponseEntity.ok().build();
    }
}
