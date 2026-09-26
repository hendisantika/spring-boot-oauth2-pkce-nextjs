package id.my.jvm.oauth2_pkce.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : oauth2-pkce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 27/09/26
 * Time: 05.58
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(Security security, Seed seed) {

    public record Security(
            String issuer,
            @DefaultValue List<String> corsAllowedOrigins,
            PublicClient publicClient) {
    }

    public record PublicClient(
            String clientId,
            String clientName,
            @DefaultValue List<String> redirectUris,
            @DefaultValue List<String> postLogoutRedirectUris,
            @DefaultValue List<String> scopes,
            @DefaultValue("15m") Duration accessTokenTtl,
            @DefaultValue("false") boolean requireConsent) {
    }

    public record Seed(Admin admin) {
    }

    public record Admin(
            @DefaultValue("false") boolean enabled,
            String username,
            String password,
            String email) {
    }
}
