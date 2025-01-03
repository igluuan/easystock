package br.devluan.easystock.services;

import br.devluan.easystock.dto.UserDTO;
import br.devluan.easystock.entities.User;
import br.devluan.easystock.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        userRepository.save(user);

        UserDTO createdUserDTO = new UserDTO();
        createdUserDTO.setName(user.getName());
        createdUserDTO.setEmail(user.getEmail());
        createdUserDTO.setPassword(user.getPassword());
        return createdUserDTO;
    }
}
