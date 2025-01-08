package br.devluan.easystock.mappers;

import br.devluan.easystock.dto.ProductDTO.ProductCreationDTO;
import br.devluan.easystock.dto.ProductDTO.ProductResponseDTO;
import br.devluan.easystock.entities.Category;
import br.devluan.easystock.entities.Product;
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
}
