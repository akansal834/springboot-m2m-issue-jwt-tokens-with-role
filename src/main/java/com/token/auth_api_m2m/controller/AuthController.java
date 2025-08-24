package com.token.auth_api_m2m.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.token.auth_api_m2m.exception.InvalidCredentialsException;
import com.token.auth_api_m2m.exception.InvalidRequestException;
import com.token.auth_api_m2m.model.Constants;
import com.token.auth_api_m2m.model.TokenRequest;
import com.token.auth_api_m2m.model.TokenResponse;
import com.token.auth_api_m2m.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/oauth/token")
    public ResponseEntity<TokenResponse> generateToken(@RequestParam(name = "clientId",required = true) String clientId,
                                                       @RequestParam(name = "clientSecret",required = true) String clientSecret,
                                                       @RequestBody TokenRequest tokenRequest){
        if(!authService.isValidCredentials(clientId,clientSecret)) {
            throw new InvalidCredentialsException(Constants.INVALID_CREDENTIALS);
        }
        if(!authService.isValidRequest(tokenRequest)) {
            throw new InvalidRequestException(Constants.INVALID_REQUEST);
        }
        return new ResponseEntity(authService.generateToken(clientId,tokenRequest),HttpStatus.OK);
    }

    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Map<String, Object>> getJwks(){
        Map<String, Object> jwksKeys = Map.of("keys", authService.getPublicKeys());
        return new ResponseEntity<>(jwksKeys,HttpStatus.OK);
    }
}
