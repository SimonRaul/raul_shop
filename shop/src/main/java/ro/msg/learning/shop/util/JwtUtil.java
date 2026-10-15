package ro.msg.learning.shop.util;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;

import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public JwtUtil() {
        try {
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");

            keyGenerator.initialize(2048);

            KeyPair keyPair = keyGenerator.generateKeyPair();

            this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
            this.publicKey = (RSAPublicKey) keyPair.getPublic();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error on generating RSA key pair", e);
        }
    }

    public String createToken(Authentication authentication) {
        String username = authentication.getName();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600000);

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(privateKey)
                .compact();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
    }
}
