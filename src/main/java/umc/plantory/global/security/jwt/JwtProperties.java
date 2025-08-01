package umc.plantory.global.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Component
@ConfigurationProperties(prefix = "jwt")
@Validated
@Getter @Setter
public class JwtProperties {
    @NotBlank
    private String secret;

    @Positive
    private Long accessTokenExpiration;

    @Positive
    private Long refreshTokenExpiration;
}
