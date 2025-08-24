package com.token.auth_api_m2m.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.token.auth_api_m2m.exception.TokenIssueException;
import com.token.auth_api_m2m.model.*;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${spring.application.name}")
    private String appName;
    private final Map<String, Jwk> jwksMap;
    private final Map<String, ClientProperties> clients;
    private final ObjectMapper objectMapper;

    @Override
    public boolean isValidCredentials(String clientId, String clientSecret) {
        if (clients.containsKey(clientId)) {
            return clients.get(clientId).getSecret().equals(clientSecret);
        }
        return false;
    }

    @Override
    public boolean isValidRequest(TokenRequest tokenRequest) {
        if(tokenRequest.getGrantType().equals(Constants.GRANT_TYPE)) {
            return true;
        }
        return false;
    }

    @Override
    public TokenResponse generateToken(String clientId,TokenRequest request) {
        return sign(clientId,request);
    }

    @Override
    public List<Jwk> getPublicKeys() {
        return new ArrayList<>(jwksMap.values());
    }

    private TokenResponse sign(String clientId, TokenRequest request){
        TokenResponse tokenResponse = null;
        try {
            ClientProperties clientProperties = clients.get(clientId);
            Instant now = Instant.now();
            Integer expiration = 3600;
            CustomClaims claims = new CustomClaims();
            claims.setClaims(request.getClaims());
            claims.getClaims().put("roles",clientProperties.getRoles());
            JwtBuilder tokenBuilder = Jwts.builder()
                    .setAudience(clientProperties.getAud())
                    .setId(Header.JWT_TYPE)
                    .setIssuer(appName)
                    .setHeaderParam("kid", clientProperties.getKid())
                    .setSubject(clientId)
                    .setClaims(objectMapper.convertValue(claims, Map.class))
                    .setExpiration(Date.from(now.plus(expiration, ChronoUnit.SECONDS)))
                    .signWith(clientProperties.getPrivateKey(), SignatureAlgorithm.RS256);

            tokenResponse = TokenResponse.builder()
                    .token(tokenBuilder.compact())
                    .tokenType(Constants.BEARER_TOKEN)
                    .expiry(expiration).build();
        }
        catch (Exception e){
            throw  new TokenIssueException(Constants.TOKEN_ISSUE_EXCEPTION);
        }
        return tokenResponse;
    }


}
