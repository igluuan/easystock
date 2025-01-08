package br.devluan.easystock.controllers;

import br.devluan.easystock.dto.ProductDTO.ProductCreationDTO;
import br.devluan.easystock.dto.ProductDTO.ProductResponseDTO;
import br.devluan.easystock.dto.UserDTO.PageResponseDTO;
import br.devluan.easystock.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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

}
