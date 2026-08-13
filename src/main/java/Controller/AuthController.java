package Controller;

import DTO.UserRegistrationRequest;
import DTO.UserResponse;
import Service.UserService;
import entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Регистрация и аутентификация")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRegistrationRequest request) {
        User user = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(user.getId(), user.getEmail(), user.getName()));
    }
}
