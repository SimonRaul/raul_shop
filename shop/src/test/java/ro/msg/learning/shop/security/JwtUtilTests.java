package ro.msg.learning.shop.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilTests {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(
                new ClassPathResource("certs/private.pem"),
                new ClassPathResource("certs/public.pem")
        );
    }

    @Test
    void createTokenIncludesAuthenticatedUserRolesAndTwentyFourHourLifetime() {
        Instant beforeCreation = Instant.now();
        String token = jwtUtil.createToken(new UsernamePasswordAuthenticationToken(
                "alice",
                "password",
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_CUSTOMER")
                )
        ));
        Instant afterCreation = Instant.now();

        Jwt decodedToken = jwtUtil.jwtDecoder().decode(token);

        assertEquals("alice", decodedToken.getSubject());
        assertEquals("ROLE_ADMIN ROLE_CUSTOMER", decodedToken.getClaimAsString("scope"));
        assertNotNull(decodedToken.getIssuedAt());
        assertNotNull(decodedToken.getExpiresAt());
        assertEquals(Duration.ofHours(24), Duration.between(decodedToken.getIssuedAt(), decodedToken.getExpiresAt()));
        assertFalse(decodedToken.getIssuedAt().isBefore(beforeCreation.minusSeconds(1)));
        assertFalse(decodedToken.getIssuedAt().isAfter(afterCreation));
    }

    @Test
    void constructorRejectsMissingKeyResources() {
        ClassPathResource missingPrivateKey = new ClassPathResource("certs/missing-private.pem");
        ClassPathResource publicKey = new ClassPathResource("certs/public.pem");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> new JwtUtil(missingPrivateKey, publicKey)
        );

        assertEquals("Invalid RSA JWT classpath key configuration", exception.getMessage());
    }
}
