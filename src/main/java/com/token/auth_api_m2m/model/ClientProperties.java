package com.token.auth_api_m2m.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.security.PrivateKey;
import java.security.PublicKey;

@Data
public class ClientProperties {

    @NotNull
    private transient String secret;
    @NotNull
    private transient PrivateKey privateKey;
    @NotNull
    private transient PublicKey publicKey;

    @NotNull
    private String kid;
    private String aud;
    private String description;
    @NotNull
    private String roles;
    @NotNull
    private Integer expiry;


}
