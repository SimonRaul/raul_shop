package ro.msg.learning.shop.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import ro.msg.learning.shop.exception.NotFoundException;
import ro.msg.learning.shop.model.User;
import ro.msg.learning.shop.model.UserRole;
import ro.msg.learning.shop.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsernameBuildsDetailsWithRoleAuthority() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("encoded-password");
        user.setRole(UserRole.ADMIN);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("alice");

        assertEquals("alice", userDetails.getUsername());
        assertEquals("encoded-password", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
        verify(userRepository).findByUsername("alice");
    }

    @Test
    void loadUserByUsernameThrowsWhenUserDoesNotExist() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missing")
        );

        assertEquals("User with username: missing not found", exception.getMessage());
        verify(userRepository).findByUsername("missing");
    }
}
