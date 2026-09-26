package id.my.jvm.oauth2_pkce.security;

import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
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
 * Stores RSA signing keys in MySQL so that issued tokens remain verifiable across restarts
 * and across multiple application instances.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JdbcJwkStore {

    private static final String SELECT_KEYS =
            "SELECT id, public_key, private_key FROM oauth2_jwk ORDER BY created_at DESC";
    private static final String INSERT_KEY =
            "INSERT INTO oauth2_jwk (id, public_key, private_key) VALUES (?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    /**
     * Returns all stored keys, newest first. A new key is generated when none exist.
     */
    @Transactional
    public List<RSAKey> loadOrCreateKeys() {
        List<RSAKey> keys = findAll();
        if (keys.isEmpty()) {
            RSAKey key = generate();
            save(key);
            log.info("Generated new RSA signing key '{}'", key.getKeyID());
            keys = List.of(key);
        }
        return keys;
    }

    private List<RSAKey> findAll() {
        return jdbcTemplate.query(SELECT_KEYS, (rs, rowNum) -> toRsaKey(
                rs.getString("id"), rs.getString("public_key"), rs.getString("private_key")));
    }

    private void save(RSAKey key) {
        try {
            Base64.Encoder encoder = Base64.getEncoder();
            jdbcTemplate.update(INSERT_KEY,
                    key.getKeyID(),
                    encoder.encodeToString(key.toRSAPublicKey().getEncoded()),
                    encoder.encodeToString(key.toRSAPrivateKey().getEncoded()));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to persist RSA key", ex);
        }
    }

    private static RSAKey generate() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();
            return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                    .privateKey((RSAPrivateKey) keyPair.getPrivate())
                    .keyID(UUID.randomUUID().toString())
                    .build();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("RSA algorithm not available", ex);
        }
    }

    private static RSAKey toRsaKey(String id, String publicKey, String privateKey) {
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKey pub = (RSAPublicKey) keyFactory.generatePublic(
                    new X509EncodedKeySpec(decoder.decode(publicKey)));
            RSAPrivateKey priv = (RSAPrivateKey) keyFactory.generatePrivate(
                    new PKCS8EncodedKeySpec(decoder.decode(privateKey)));
            return new RSAKey.Builder(pub).privateKey(priv).keyID(id).build();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to load RSA key " + id, ex);
        }
    }
}
