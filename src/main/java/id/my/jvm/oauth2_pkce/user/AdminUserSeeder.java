package id.my.jvm.oauth2_pkce.user;

import id.my.jvm.oauth2_pkce.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Project : oauth2-pkce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 27/09/26
 * Time: 05.58
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserSeeder implements ApplicationRunner {

    private final AppProperties appProperties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        AppProperties.Admin admin = appProperties.seed().admin();
        if (!admin.enabled() || userRepository.existsByUsername(admin.username())) {
            return;
        }
        User user = new User();
        user.setUsername(admin.username());
        user.setEmail(admin.email());
        user.setFullName("Administrator");
        user.setPassword(passwordEncoder.encode(admin.password()));
        user.setRoles(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN));
        userRepository.save(user);
        log.info("Seeded default admin user '{}'", admin.username());
    }
}
