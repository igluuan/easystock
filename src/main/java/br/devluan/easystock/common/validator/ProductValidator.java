package br.devluan.easystock.common.validator;


import br.devluan.easystock.dto.base.ProductBaseDTO;
import br.devluan.easystock.dto.request.ProductCreationDTO;
import br.devluan.easystock.dto.request.ProductUpdateDTO;
import br.devluan.easystock.application.exceptions.ProductExceptions.ProductValidationException;
import br.devluan.easystock.domain.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductValidator {
    private final CategoryRepository categoryRepository;

    public void validateProductCreation(ProductCreationDTO product) {
        List<String> errors = new ArrayList<>();

        // Name validation
        if (product.name() == null || product.name().trim().isEmpty()) {
            errors.add("Product name is required");
        } else {
            if (product.name().length() < 3) {
                errors.add("Product name must have at least 3 characters");
            }
            if (product.name().length() > 100) {
                errors.add("Product name must have at most 100 characters");
            }
            if (!product.name().matches("^[a-zA-Z0-9 ._-]+$")) {
                errors.add("Product name contains invalid characters");
            }
        }

        // Price validation
        if (product.price() == null) {
            errors.add("Product price is required");
        } else {
            if (product.price() <= 0) {
                errors.add("Product price must be greater than zero");
            }
            if (product.price() > 999999.99) {
                errors.add("Product price cannot be greater than 999,999.99");
            }
        }

        // Stock validation
        if (product.stockQuantity() == null) {
            errors.add("Stock quantity is required");
        } else {
            if (product.stockQuantity() < 0) {
                errors.add("Stock quantity cannot be negative");
            }
            if (product.stockQuantity() > 999999) {
                errors.add("Stock quantity cannot be greater than 999,999");
            }
        }

        // Minimum quantity validation
        if (product.minQuantity() == null) {
            errors.add("Minimum quantity is required");
        } else {
            if (product.minQuantity() < 0) {
                errors.add("Minimum quantity cannot be negative");
            }
            if (product.minQuantity() > product.stockQuantity()) {
                errors.add("Minimum quantity cannot be greater than stock quantity");
            }
        }

        // Category validation
        if (product.categoryId() == null) {
            errors.add("Product category is required");
        } else {
            if (!categoryRepository.existsById(product.categoryId())) {
                errors.add("Specified category does not exist");
            }
        }

        if (!errors.isEmpty()) {
            throw new ProductValidationException("Validation errors found", errors);
        }
    }

    public void validateProductUpdate(ProductUpdateDTO product, Long productId) {
        List<String> errors = new ArrayList<>();

        // ID validation
        if (productId == null || productId <= 0) {
            errors.add("Invalid product ID");
        }

        // Reuse validation for common fields
        validateCommonFields(product, errors);

        if (!errors.isEmpty()) {
            throw new ProductValidationException("Validation errors found in update", errors);
        }
    }

    public void validateStockOperation(Long productId, Integer quantity) {
        List<String> errors = new ArrayList<>();

        if (productId == null || productId <= 0) {
            errors.add("Invalid product ID");
        }

        if (quantity == null) {
            errors.add("Quantity is required");
        } else {
            if (quantity <= 0) {
                errors.add("Quantity must be greater than zero");
            }
            if (quantity > 999999) {
                errors.add("Quantity cannot be greater than 999,999");
            }
        }

        if (!errors.isEmpty()) {
            throw new ProductValidationException("Validation errors found in stock operation", errors);
        }
    }

    private void validateCommonFields(ProductBaseDTO product, List<String> errors) {
        // Common validations between creation and update
        if (product.name() != null) {
            if (product.name().trim().isEmpty()) {
                errors.add("Product name cannot be empty");
            } else {
                if (product.name().length() < 3) {
                    errors.add("Product name must have at least 3 characters");
                }
                if (product.name().length() > 100) {
                    errors.add("Product name must have at most 100 characters");
                }
                if (!product.name().matches("^[a-zA-Z0-9 ._-]+$")) {
                    errors.add("Product name contains invalid characters");
                }
            }
        }

        if (product.price() != null) {
            if (product.price() <= 0) {
                errors.add("Product price must be greater than zero");
            }
            if (product.price() > 999999.99) {
                errors.add("Product price cannot be greater than 999,999.99");
            }
        }
    }
}
