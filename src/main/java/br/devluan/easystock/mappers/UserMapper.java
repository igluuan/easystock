package br.devluan.easystock.mappers;

import br.devluan.easystock.dto.UserDTO.UserCreationDTO;
import br.devluan.easystock.dto.UserDTO.UserResponseDTO;
import br.devluan.easystock.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "creationAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    User toEntity(UserCreationDTO dto);

    UserResponseDTO toResponseDTO(User user);

    UserCreationDTO toDTO(User user);
}
