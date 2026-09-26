package id.my.jvm.oauth2_pkce.security;

import id.my.jvm.oauth2_pkce.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * Project : oauth2-pkce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 27/09/26
 * Time: 05.59
 */
/**
 * Registers (or updates) the public PKCE client defined in {@code app.security.public-client}.
 * <p>
 * The client has no secret ({@code client_authentication_method=none}) and must send a
 * {@code code_challenge} using S256 on every authorization request.
 */
@Slf4j
@Order(0)
@Component
@RequiredArgsConstructor
public class RegisteredClientInitializer implements ApplicationRunner {

    private final AppProperties appProperties;
    private final RegisteredClientRepository registeredClientRepository;

    @Override
    public void run(ApplicationArguments args) {
        AppProperties.PublicClient client = appProperties.security().publicClient();
        RegisteredClient existing = registeredClientRepository.findByClientId(client.clientId());
        String id = existing != null ? existing.getId() : UUID.randomUUID().toString();

        RegisteredClient registeredClient = RegisteredClient.withId(id)
                .clientId(client.clientId())
                .clientName(client.clientName())
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUris(uris -> uris.addAll(client.redirectUris()))
                .postLogoutRedirectUris(uris -> uris.addAll(client.postLogoutRedirectUris()))
                .scopes(scopes -> scopes.addAll(client.scopes()))
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true)
                        .requireAuthorizationConsent(client.requireConsent())
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                        .accessTokenTimeToLive(client.accessTokenTtl())
                        .build())
                .build();

        registeredClientRepository.save(registeredClient);
        log.info("{} public PKCE client '{}'", existing != null ? "Updated" : "Registered", client.clientId());
    }
}
