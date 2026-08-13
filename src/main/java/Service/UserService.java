package Service;

import DTO.UserRegistrationRequest;
import Repository.RoleRepository;
import Repository.UserRepository;
import entity.Role;
import entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import Exception.BusinessException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(UserRegistrationRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()){
            throw new BusinessException("User with this email already exists");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "USER")));

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setName(request.name());
        user.setRole(userRole);

        return userRepository.save(user);
    }
}
