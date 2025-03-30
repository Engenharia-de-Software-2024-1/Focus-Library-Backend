package com.focuslibrary.focus_library.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.focuslibrary.focus_library.config.security.TokenService;
import com.focuslibrary.focus_library.dto.AuthRegisterDTO;
import com.focuslibrary.focus_library.dto.AuthRequestDTO;
import com.focuslibrary.focus_library.dto.AuthResponseDTO;
import com.focuslibrary.focus_library.dto.GoogleAuthRequestDTO;
import com.focuslibrary.focus_library.dto.UsuarioResponseDTO;
import com.focuslibrary.focus_library.model.Usuario;
import com.focuslibrary.focus_library.service.auth.AuthServiceImp;
import com.focuslibrary.focus_library.service.auth.GoogleAuthService;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthServiceImp authServiceImp;

    @Mock
    private TokenService tokenService;

    @Mock
    private GoogleAuthService googleAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        // Arrange
        AuthRequestDTO authDTO = new AuthRequestDTO();
        authDTO.setUsername("testUser");
        authDTO.setSenha("testPassword");

        Authentication authentication = mock(Authentication.class);
        Usuario usuario = new Usuario();
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenService.generateToken(any(Usuario.class))).thenReturn(new AuthResponseDTO("testToken", "refreshToken"));

        // Act
        ResponseEntity<?> response = authController.login(authDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthResponseDTO responseBody = (AuthResponseDTO) response.getBody();
        assertNotNull(responseBody);
        assertEquals("testToken", responseBody.getAcessToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).generateToken(any(Usuario.class));
    }

    @Test
    void registrar_WithValidData_ShouldReturnCreatedResponse() {
        // Arrange
        AuthRegisterDTO authDTO = new AuthRegisterDTO();
        UsuarioResponseDTO expectedResponse = UsuarioResponseDTO.builder()
                .userId("1")
                .username("testUser")
                .email("test@example.com")
                .dataNascimento(LocalDate.now())
                .streak(0L)
                .build();
        when(authServiceImp.registrar(any(AuthRegisterDTO.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = authController.registrar(authDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(authServiceImp).registrar(any(AuthRegisterDTO.class));
    }

    @Test
    void loginWithGoogle_WithValidToken_ShouldReturnOkResponse() {
        // Arrange
        GoogleAuthRequestDTO googleAuthRequest = new GoogleAuthRequestDTO();
        AuthResponseDTO expectedResponse = new AuthResponseDTO("accessToken", "refreshToken");
        when(googleAuthService.authenticateWithGoogle(any(GoogleAuthRequestDTO.class)))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = authController.loginWithGoogle(googleAuthRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(googleAuthService).authenticateWithGoogle(any(GoogleAuthRequestDTO.class));
    }

    @Test
    void refresh_WithValidToken_ShouldReturnNewToken() {
        // Arrange
        String refreshToken = "testRefreshToken";
        String expectedToken = "newAccessToken";
        when(tokenService.getAcessToken(anyString())).thenReturn(expectedToken);

        // Act
        ResponseEntity<?> response = authController.refresh(refreshToken);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedToken, response.getBody());
        verify(tokenService).getAcessToken(refreshToken);
    }
} 