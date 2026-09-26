package id.my.jvm.oauth2_pkce.user;

import id.my.jvm.oauth2_pkce.common.ConflictException;
import id.my.jvm.oauth2_pkce.common.ResourceNotFoundException;
import id.my.jvm.oauth2_pkce.user.UserDtos.RegisterUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Project : oauth2-pkce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 27/09/26
 * Time: 06.02
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username '%s' is already taken".formatted(request.username()));
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email '%s' is already registered".formatted(request.email()));
        }
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of(Role.ROLE_USER));
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User '%s' not found".formatted(username)));
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
