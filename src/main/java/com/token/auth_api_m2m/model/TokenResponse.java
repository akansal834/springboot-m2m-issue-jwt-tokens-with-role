package com.token.auth_api_m2m.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {
    @Schema(example = "yueyue78BHGSDjhjhYUYUDHS7BJBNbnbnbnbnN")
    private String token;
    @Schema(example = "Bearer")
    private String tokenType;
    @Schema(example = "3600")
    private Integer expiry;
}
