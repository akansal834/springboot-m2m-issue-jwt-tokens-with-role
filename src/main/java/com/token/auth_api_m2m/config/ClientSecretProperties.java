package com.token.auth_api_m2m.config;

import com.token.auth_api_m2m.model.ClientSecrets;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Data
@ConfigurationProperties(prefix="")
    public class ClientSecretProperties {
    private Map<String, ClientSecrets> clients;

}
