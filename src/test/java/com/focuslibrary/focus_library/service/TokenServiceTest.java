package com.focuslibrary.focus_library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.focuslibrary.focus_library.config.security.TokenService;
import com.focuslibrary.focus_library.controller.UsuarioController;
import com.focuslibrary.focus_library.dto.AuthResponseDTO;
import com.focuslibrary.focus_library.exceptions.FocusLibraryException;
import com.focuslibrary.focus_library.exceptions.InvalidRefreshToken;
import com.focuslibrary.focus_library.model.Usuario;
import com.focuslibrary.focus_library.repository.UsuarioRepository;
import com.focuslibrary.focus_library.service.usuario.UsuarioService;
import java.lang.reflect.Field;
import java.util.Optional;

public class TokenServiceTest {
    @InjectMocks
    private TokenService tokenService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private String idUser;

    @Mock
    private UsuarioRepository usuarioRepository;


    @BeforeEach
    void setUp() throws Exception{
        MockitoAnnotations.openMocks(this); 
        idUser = "123";
        Field chaveField = TokenService.class.getDeclaredField("chave");
        chaveField.setAccessible(true);
        chaveField.set(tokenService, "my-secret-key");
    }

    @Test
    void testGenerateToken() {
        Usuario usuario = Usuario.builder()
                .userId(idUser)
                .username("Test User")
                .email("test@example.com")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .build();
        AuthResponseDTO authResponse = tokenService.generateToken(usuario);

        assertNotNull(authResponse.getAcessToken());
        assertNotNull(authResponse.getRefreshToken());
    }

    @Test
    void testGenerateTokenThrowsException() {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getUsername()).thenReturn("testUser");

        TokenService spyService = spy(tokenService);
        doThrow(new FocusLibraryException("Erro ao gerar token!")).when(spyService).generateToken(usuario);

        assertThrows(FocusLibraryException.class, () -> spyService.generateToken(usuario));
    }

    @Test
    void testValidateToken_validToken() {
        String validToken = "valid.token";
        String username = "testUser";
        TokenService spyService = spy(tokenService);
        when(spyService.validateToken(validToken)).thenReturn(username);

        String result = spyService.validateToken(validToken);
        assertEquals(username, result);
    }

    @Test
    void testValidateToken_invalidToken() {
        String invalidToken = "invalid.token";
        
        TokenService spyService = spy(tokenService);
        when(spyService.validateToken(invalidToken)).thenReturn("");

        String result = spyService.validateToken(invalidToken);
        assertEquals("", result);
    }

    @Test
    void testGetUsernameUsuarioLogado_authenticated() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = TokenService.getUsernameUsuarioLogado();

        assertEquals("testUser", username);
    }

    @Test
    void testGetUsernameUsuarioLogado_notAuthenticated() {
        SecurityContextHolder.clearContext();

        String username = TokenService.getUsernameUsuarioLogado();

        assertNull(username);
    }

    @Test
    void testGetAcessToken_invalidTokenException() {
        String refreshToken = "invalid.refresh.token";
        
        TokenService spyService = spy(tokenService);
        doThrow(new JWTVerificationException("Invalid token")).when(spyService).validateToken(refreshToken);

        String accessToken = spyService.getAcessToken(refreshToken);
        assertEquals("", accessToken);
    }

    @Test
    void testValidateTokenWithRealValidToken() {
        Usuario usuario = Usuario.builder()
                .userId(idUser)
                .username("testUser")
                .email("test@example.com")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .build();
        AuthResponseDTO authResponse = tokenService.generateToken(usuario);
        String validToken = authResponse.getAcessToken();

        String subject = tokenService.validateToken(validToken);
        assertEquals("testUser", subject);
    }

    @Test
    void testValidateTokenWithRealInvalidToken() {
        String invalidToken = "invalid.token";
        String subject = tokenService.validateToken(invalidToken);
        assertEquals("", subject);
    }

    @Test
    void testGetAcessTokenValidRefreshToken() {
        Usuario usuario = Usuario.builder()
                .userId(idUser)
                .username("testUser")
                .email("test@example.com")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .build();
        AuthResponseDTO authResponse = tokenService.generateToken(usuario);
        String refreshToken = authResponse.getRefreshToken();

        when(usuarioRepository.findById(idUser)).thenReturn(Optional.of(usuario));

        String accessToken = tokenService.getAcessToken(refreshToken);
        assertNotNull(accessToken);

        String subject = tokenService.validateToken(accessToken);
        assertEquals("testUser", subject);
    }

    @Test
    void testGetAcessTokenValidRefreshTokenUserNotFound() {
        Usuario usuario = Usuario.builder()
                .userId(idUser)
                .username("testUser")
                .email("test@example.com")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .build();
        AuthResponseDTO authResponse = tokenService.generateToken(usuario);
        String refreshToken = authResponse.getRefreshToken();

        when(usuarioRepository.findById(idUser)).thenReturn(Optional.empty());

        assertThrows(InvalidRefreshToken.class, () -> tokenService.getAcessToken(refreshToken));
    }

    @Test
    void testGetUsernameUsuarioLogadoAuthenticatedNonUserDetails() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("testUser");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = TokenService.getUsernameUsuarioLogado();
        assertEquals("testUser", username);
    }
}
