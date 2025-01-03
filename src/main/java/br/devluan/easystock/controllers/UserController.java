package br.devluan.easystock.controllers;

import br.devluan.easystock.dto.LoginRequest;
import br.devluan.easystock.dto.LoginResponse;
import br.devluan.easystock.dto.UserDTO;
import br.devluan.easystock.repositories.UserRepository;
import br.devluan.easystock.services.UserService;
import br.devluan.easystock.utils.UserValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserValidator userValidator;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtEncoder jwtEncoder;

    public UserController(UserService userService, UserValidator userValidator, BCryptPasswordEncoder passwordEncoder,
                          UserRepository userRepository, JwtEncoder jwtEncoder) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/create")
    public ResponseEntity<?>createNewUser(@RequestBody @Validated UserDTO userDTO, BindingResult bindingResult){
        userValidator.validate(userDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        UserDTO createdUser = userService.createUser(userDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse>login(@RequestBody LoginRequest loginRequest){
        var user = userRepository.findByEmail(loginRequest.email());

        if (user.isEmpty() || !user.get().isLoginCorrect(loginRequest, passwordEncoder)) {
            throw new BadCredentialsException("Invalid credentials.");
        }

        var now = Instant.now();
        var expiresIn = 300L;
        var claims = JwtClaimsSet.builder()
                .issuer("backend")
                .subject(user.get().getUserId().toString())
                .expiresAt(now.plusSeconds(expiresIn))
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn));
    }
}
