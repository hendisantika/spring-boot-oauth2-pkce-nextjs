package id.my.jvm.oauth2_pkce.common;

/**
 * Created by IntelliJ IDEA.
 * Project : oauth2-pkce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 27/09/26
 * Time: 06.02
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
