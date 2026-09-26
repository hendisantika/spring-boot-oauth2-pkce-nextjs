-- Spring Authorization Server schema, adapted for MySQL (blob -> mediumblob, explicit NULL timestamps)

CREATE TABLE oauth2_registered_client
(
    id                            VARCHAR(100)                        NOT NULL,
    client_id                     VARCHAR(100)                        NOT NULL,
    client_id_issued_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret                 VARCHAR(200)                        NULL,
    client_secret_expires_at      TIMESTAMP                           NULL,
    client_name                   VARCHAR(200)                        NOT NULL,
    client_authentication_methods VARCHAR(1000)                       NOT NULL,
    authorization_grant_types     VARCHAR(1000)                       NOT NULL,
    redirect_uris                 VARCHAR(1000)                       NULL,
    post_logout_redirect_uris     VARCHAR(1000)                       NULL,
    scopes                        VARCHAR(1000)                       NOT NULL,
    client_settings               VARCHAR(2000)                       NOT NULL,
    token_settings                VARCHAR(2000)                       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_oauth2_registered_client_client_id UNIQUE (client_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE oauth2_authorization
(
    id                            VARCHAR(100)  NOT NULL,
    registered_client_id          VARCHAR(100)  NOT NULL,
    principal_name                VARCHAR(200)  NOT NULL,
    authorization_grant_type      VARCHAR(100)  NOT NULL,
    authorized_scopes             VARCHAR(1000) NULL,
    attributes                    MEDIUMBLOB    NULL,
    state                         VARCHAR(500)  NULL,
    authorization_code_value      MEDIUMBLOB    NULL,
    authorization_code_issued_at  TIMESTAMP     NULL,
    authorization_code_expires_at TIMESTAMP     NULL,
    authorization_code_metadata   MEDIUMBLOB    NULL,
    access_token_value            MEDIUMBLOB    NULL,
    access_token_issued_at        TIMESTAMP     NULL,
    access_token_expires_at       TIMESTAMP     NULL,
    access_token_metadata         MEDIUMBLOB    NULL,
    access_token_type             VARCHAR(100)  NULL,
    access_token_scopes           VARCHAR(1000) NULL,
    oidc_id_token_value           MEDIUMBLOB    NULL,
    oidc_id_token_issued_at       TIMESTAMP     NULL,
    oidc_id_token_expires_at      TIMESTAMP     NULL,
    oidc_id_token_metadata        MEDIUMBLOB    NULL,
    refresh_token_value           MEDIUMBLOB    NULL,
    refresh_token_issued_at       TIMESTAMP     NULL,
    refresh_token_expires_at      TIMESTAMP     NULL,
    refresh_token_metadata        MEDIUMBLOB    NULL,
    user_code_value               MEDIUMBLOB    NULL,
    user_code_issued_at           TIMESTAMP     NULL,
    user_code_expires_at          TIMESTAMP     NULL,
    user_code_metadata            MEDIUMBLOB    NULL,
    device_code_value             MEDIUMBLOB    NULL,
    device_code_issued_at         TIMESTAMP     NULL,
    device_code_expires_at        TIMESTAMP     NULL,
    device_code_metadata          MEDIUMBLOB    NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE INDEX idx_oauth2_authorization_principal ON oauth2_authorization (registered_client_id, principal_name);
CREATE INDEX idx_oauth2_authorization_state ON oauth2_authorization (state);

CREATE TABLE oauth2_authorization_consent
(
    registered_client_id VARCHAR(100)  NOT NULL,
    principal_name       VARCHAR(200)  NOT NULL,
    authorities          VARCHAR(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Persisted RSA signing keys so issued JWTs survive application restarts
CREATE TABLE oauth2_jwk
(
    id          VARCHAR(100) NOT NULL,
    public_key  TEXT         NOT NULL,
    private_key TEXT         NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
