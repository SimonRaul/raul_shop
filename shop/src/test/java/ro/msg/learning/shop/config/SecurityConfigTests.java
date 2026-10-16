package ro.msg.learning.shop.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SecurityConfigTests.TestController.class)
@Import({SecurityConfig.class, SecurityConfigTests.TestController.class})
class SecurityConfigTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void securityFilterChainAllowsAuthenticationEndpointsAndRequiresAuthenticationElsewhere() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void jwtAuthenticationConverterUsesScopeClaimWithoutAddingAPrefix() {
        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(60),
                Map.of("alg", "none"),
                Map.of("scope", "ROLE_ADMIN ROLE_CUSTOMER")
        );

        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();
        JwtAuthenticationToken authentication = (JwtAuthenticationToken) converter.convert(jwt);

        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertTrue(authorities.containsAll(List.of("ROLE_ADMIN", "ROLE_CUSTOMER")));
    }

    @Test
    void corsConfigurationAllowsOnlyConfiguredClientMethodsAndHeaders() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        CorsConfiguration configuration = source.getCorsConfiguration(new MockHttpServletRequest("OPTIONS", "/api/products"));

        assertEquals(List.of("http://localhost:3000"), configuration.getAllowedOrigins());
        assertEquals(List.of("GET", "POST", "PUT", "DELETE"), configuration.getAllowedMethods());
        assertEquals(List.of("Authorization", "Content-Type", "X-Requested-With"), configuration.getAllowedHeaders());
        assertTrue(configuration.getAllowCredentials());
    }

    @Test
    void passwordEncoderHashesAndVerifiesPasswords() {
        String encodedPassword = securityConfig.passwordEncoder().encode("secret");

        assertTrue(securityConfig.passwordEncoder().matches("secret", encodedPassword));
    }

    @Test
    void authenticationManagerDelegatesToAuthenticationConfiguration() throws Exception {
        AuthenticationManager expectedManager = authentication -> authentication;
        AuthenticationConfiguration authenticationConfiguration = mock(AuthenticationConfiguration.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(expectedManager);

        AuthenticationManager actualManager = securityConfig.authenticationManager(authenticationConfiguration);

        assertSame(expectedManager, actualManager);
    }

    @RestController
    static class TestController {
        @GetMapping("/api/auth/login")
        String login() {
            return "public";
        }

        @GetMapping("/protected")
        String protectedEndpoint() {
            return "protected";
        }
    }
}
