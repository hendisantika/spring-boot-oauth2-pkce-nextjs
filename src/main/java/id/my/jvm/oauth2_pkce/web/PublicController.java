package id.my.jvm.oauth2_pkce.web;

import id.my.jvm.oauth2_pkce.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Project : oauth2-pkce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 27/09/26
 * Time: 06.02
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final AppProperties appProperties;

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
                "application", "oauth2-pkce",
                "issuer", appProperties.security().issuer(),
                "clientId", appProperties.security().publicClient().clientId(),
                "scopes", appProperties.security().publicClient().scopes());
    }
}
