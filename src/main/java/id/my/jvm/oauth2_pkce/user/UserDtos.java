package id.my.jvm.oauth2_pkce.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * Project : oauth2-pkce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 27/09/26
 * Time: 06.02
 */
public final class UserDtos {

    private UserDtos() {
    }

    public record RegisterUserRequest(
            @NotBlank @Size(min = 3, max = 50)
            @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "may only contain letters, digits, '.', '_' and '-'")
            String username,
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank @Size(min = 8, max = 100) String password,
            @Size(max = 150) String fullName) {
    }

    public record UserResponse(
            Long id,
            String username,
            String email,
            String fullName,
            boolean enabled,
            Set<String> roles,
            Instant createdAt) {

        public static UserResponse from(User user) {
            return new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName(),
                    user.isEnabled(),
                    user.getRoles().stream().map(Role::name).collect(Collectors.toSet()),
                    user.getCreatedAt());
        }
    }
}
