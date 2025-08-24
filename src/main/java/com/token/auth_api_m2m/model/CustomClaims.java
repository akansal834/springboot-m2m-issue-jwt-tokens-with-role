package com.token.auth_api_m2m.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class CustomClaims {
    String scopes;
    Map<String,String> claims;
}
