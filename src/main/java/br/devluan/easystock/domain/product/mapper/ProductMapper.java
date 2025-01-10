package br.devluan.easystock.domain.product.mapper;

import br.devluan.easystock.dto.request.ProductCreationDTO;
import br.devluan.easystock.dto.request.ProductUpdateDTO;
import br.devluan.easystock.dto.response.ProductResponseDTO;
import br.devluan.easystock.domain.product.entity.Category;
import br.devluan.easystock.domain.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductCreationDTO productDTO);

    ProductCreationDTO toCreationDTO(Product product);

    ProductResponseDTO toResponseDTO(Product product);

    ProductResponseDTO.CategoryResponseDTO toCategoryDTO(Category category);

    ProductUpdateDTO toUpdateDTO(Product product);
}
