package br.devluan.easystock.controllers;

import br.devluan.easystock.dto.LoginDTO.LoginRequest;
import br.devluan.easystock.dto.LoginDTO.LoginResponse;
import br.devluan.easystock.dto.UserDTO.PageResponseDTO;
import br.devluan.easystock.dto.UserDTO.UpdateUserDTO;
import br.devluan.easystock.dto.UserDTO.UserCreationDTO;
import br.devluan.easystock.dto.UserDTO.UserResponseDTO;
import br.devluan.easystock.repositories.UserRepository.UserRepository;
import br.devluan.easystock.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtEncoder jwtEncoder;

    // method for create a new user
    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> create(@RequestBody @Valid UserCreationDTO userCreationDTO) {
        UserResponseDTO newUser = userService.createUser(userCreationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    // method for authenticate a user and generate a JWT token
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        var response = userService.authenticate(loginRequest);
        return ResponseEntity.ok(response);
    }

    // method to update a user
    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<UserResponseDTO> update(@PathVariable UUID userId, @RequestBody @Valid UpdateUserDTO updateUserDTO){
        UserResponseDTO updatedUser = userService.updateUser(userId, updateUserDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<UserResponseDTO> getUsers(@PathVariable UUID userId){
        UserResponseDTO userResponseDTO = userService.getUserById(userId);
        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<PageResponseDTO<UserResponseDTO>> getUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        return ResponseEntity.ok(userService.getUsers(page, size));
    }

    @PatchMapping("/toggle/{userId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Boolean> toggleStatus(@PathVariable UUID userId){
        boolean updatedStatus = userService.toggleStatus(userId);
        return ResponseEntity.ok(updatedStatus);
    }
}
