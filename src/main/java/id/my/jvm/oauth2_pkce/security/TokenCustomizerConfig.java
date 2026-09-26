package id.my.jvm.oauth2_pkce.security;

import id.my.jvm.oauth2_pkce.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * Project : oauth2-pkce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 27/09/26
 * Time: 05.59
 */
@Configuration(proxyBeanMethods = false)
public class TokenCustomizerConfig {

    public static final String ROLES_CLAIM = "roles";

    /**
     * Adds the user's roles to the access token and profile/email claims to the ID token
     * (which are also returned by the OIDC UserInfo endpoint).
     */
    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer(UserRepository userRepository) {
        return context -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                Set<String> roles = context.getPrincipal().getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(authority -> authority.startsWith("ROLE_"))
                        .map(authority -> authority.substring("ROLE_".length()))
                        .collect(Collectors.toSet());
                context.getClaims().claim(ROLES_CLAIM, roles);
            }

            if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                Set<String> scopes = context.getAuthorizedScopes();
                userRepository.findByUsername(context.getPrincipal().getName()).ifPresent(user -> {
                    if (scopes.contains(OidcScopes.PROFILE)) {
                        context.getClaims().claim("preferred_username", user.getUsername());
                        if (user.getFullName() != null) {
                            context.getClaims().claim("name", user.getFullName());
                        }
                    }
                    if (scopes.contains(OidcScopes.EMAIL)) {
                        context.getClaims().claim("email", user.getEmail());
                    }
                });
            }
        };
    }
}
