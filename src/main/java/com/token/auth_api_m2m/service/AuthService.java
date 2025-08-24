package com.token.auth_api_m2m.service;

import com.nimbusds.jose.jwk.RSAKey;
import com.token.auth_api_m2m.model.*;
import io.jsonwebtoken.JwtBuilder;

import java.util.List;

public interface AuthService {
    public boolean isValidCredentials(String clientId,String clientSecret);
    public boolean isValidRequest(TokenRequest tokenRequest);

    public TokenResponse generateToken(String clientId,TokenRequest request);
    public List<Jwk> getPublicKeys();
}
