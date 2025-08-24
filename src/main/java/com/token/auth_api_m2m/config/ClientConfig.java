package com.token.auth_api_m2m.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.token.auth_api_m2m.exception.KeyReadingException;
import com.token.auth_api_m2m.model.ClientProperties;
import com.token.auth_api_m2m.model.Jwk;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    private final ClientSecretProperties clientSecretProperties;
    private final ObjectMapper objectMapper;

    @Bean
    public Map<String, ClientProperties> clients() throws IOException {
        Map<String, ClientProperties> clients;
        try {
            Resource resource = new ClassPathResource("clientProperties.json");
            clients = objectMapper.readValue(resource.getInputStream(), Map.class);

            if (clients.size() > 0) {
                for (Map.Entry<String, ClientProperties> clientEntry : clients.entrySet()) {
                    clientEntry.getValue().setSecret(clientSecretProperties.getSecrets().get(clientEntry.getKey()).getSecret());
                    clientEntry.getValue().setKid(clientSecretProperties.getSecrets().get(clientEntry.getKey()).getKid());
                    clientEntry.getValue().setPrivateKey(loadPrivateKey(clientEntry.getKey()));

                    PublicKey publicKey = loadPublicKey(clientEntry.getKey());
                    clientEntry.getValue().setPublicKey(publicKey);

                }
            }
        }
        catch (IOException | IllegalArgumentException| NoSuchAlgorithmException | InvalidKeySpecException e){
            throw new KeyReadingException("Failed to read keys");


        }
        return clients;
    }
    @Bean
    public Map<String, Jwk> jwksMap(Map<String, ClientProperties> clients) {
        Map<String, Jwk> jwkMap = new HashMap<>();

        clients.forEach((clientId, client) -> {
            if (client.getPublicKey() instanceof RSAPublicKey) {
                RSAPublicKey rsaKey = (RSAPublicKey) client.getPublicKey();

                String n = base64UrlEncode(unsignedToBytes(rsaKey.getModulus()));
                String e = base64UrlEncode(unsignedToBytes(rsaKey.getPublicExponent()));

                Jwk jwk = new Jwk();
                jwk.setKty("RSA");
                jwk.setKid(client.getKid());
                jwk.setUse("sig");
                jwk.setAlg("RS256");
                jwk.setN(n);
                jwk.setE(e);

                jwkMap.put(client.getKid(), jwk);
            }
            // Handle other key types if needed
        });

        return jwkMap;
    }

    private String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private byte[] unsignedToBytes(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes.length > 0 && bytes[0] == 0) {
            byte[] tmp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, tmp, 0, tmp.length);
            return tmp;
        }
        return bytes;
    }
    public static PrivateKey loadPrivateKey(String clientId)
            throws IOException,IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException{
        String pemFilePath = "/keys/" + clientId + "_private.pem";
        String privateKeyPem = new String(Files.readAllBytes(Paths.get(pemFilePath)));
        privateKeyPem = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(privateKeyPem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static PublicKey loadPublicKey(String clientId)
            throws IOException,IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
        String pemFilePath = "/keys/" + clientId + "_public.pem";
        String publicKeyPem = new String(Files.readAllBytes(Paths.get(pemFilePath)));
        publicKeyPem = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(publicKeyPem);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

}
