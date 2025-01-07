package br.devluan.easystock.mappers;

import br.devluan.easystock.dto.UserDTO.UserCreationDTO;
import br.devluan.easystock.dto.UserDTO.UserResponseDTO;
import Role;
import br.devluan.easystock.entities.User.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "creationAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    User toEntity(UserCreationDTO dto);

    UserCreationDTO toDTO(User user);

    @Mapping(target = "role", expression = "java(mapRole(user))")
    UserResponseDTO toResponseDTO(User user);

    default String mapRole(User user) {
        return user.getRoles()
                .stream()
                .findFirst()
                .map(Role::getName)
                .orElse("EMPLOYEE");
    }


}
