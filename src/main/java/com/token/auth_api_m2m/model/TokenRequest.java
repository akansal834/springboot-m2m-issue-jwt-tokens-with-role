package com.token.auth_api_m2m.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {

    private String grantType;
    private Map<String,String> claims;
}
