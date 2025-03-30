package com.focuslibrary.focus_library.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.focuslibrary.focus_library.config.security.TokenService;
import com.focuslibrary.focus_library.dto.AuthResponseDTO;
import com.focuslibrary.focus_library.dto.GoogleAuthRequestDTO;
import com.focuslibrary.focus_library.model.GoogleAccount;
import com.focuslibrary.focus_library.model.Usuario;
import com.focuslibrary.focus_library.repository.GoogleAccountRepository;
import com.focuslibrary.focus_library.repository.UsuarioRepository;
import com.focuslibrary.focus_library.service.auth.GoogleAuthService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class GoogleAuthServiceTest {

    @Mock
    private GoogleAccountRepository googleAccountRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private GoogleAuthService googleAuthService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(googleAuthService, "clientId", "test-client-id");
    }

    @Test
    void authenticateWithGoogle_InvalidToken_ThrowsException() throws GeneralSecurityException, IOException {
        GoogleAuthRequestDTO request = new GoogleAuthRequestDTO("invalid-token");

        GoogleIdTokenVerifier verifier = mock(GoogleIdTokenVerifier.class);
        when(verifier.verify(request.getToken())).thenReturn(null);

        try (MockedConstruction<GoogleIdTokenVerifier.Builder> ignored = mockConstruction(
                GoogleIdTokenVerifier.Builder.class,
                (mock, context) -> {
                    when(mock.setAudience(any(List.class))).thenReturn(mock);
                    when(mock.build()).thenReturn(verifier);
                })) {

            Exception exception = assertThrows(RuntimeException.class,
                    () -> googleAuthService.authenticateWithGoogle(request));

            assertTrue(exception.getMessage().contains("Token Google inválido"));
        }
    }

    @Test
    void authenticateWithGoogle_ValidToken_ExistingUser_ReturnsToken() throws Exception {
        String token = "valid-token";
        String googleId = "123";
        String email = "user@example.com";
        GoogleAuthRequestDTO request = new GoogleAuthRequestDTO(token);

        GoogleIdToken idToken = mock(GoogleIdToken.class);
        Payload payload = mock(Payload.class);
        when(idToken.getPayload()).thenReturn(payload);
        when(payload.getSubject()).thenReturn(googleId);
        when(payload.getEmail()).thenReturn(email);

        GoogleIdTokenVerifier verifier = mock(GoogleIdTokenVerifier.class);
        when(verifier.verify(token)).thenReturn(idToken);

        try (MockedConstruction<GoogleIdTokenVerifier.Builder> ignored = mockConstruction(
                GoogleIdTokenVerifier.Builder.class,
                (mock, context) -> {
                    when(mock.setAudience(any(List.class))).thenReturn(mock);
                    when(mock.build()).thenReturn(verifier);
                })) {

            Usuario existingUser = new Usuario();
            existingUser.setEmail(email);
            GoogleAccount googleAccount = new GoogleAccount();
            googleAccount.setUsuario(existingUser);
            when(googleAccountRepository.findByGoogleId(googleId)).thenReturn(Optional.of(googleAccount));

            AuthResponseDTO expectedResponse = new AuthResponseDTO("access-token", "refresh-token");
        
            when(tokenService.generateToken(existingUser)).thenReturn(expectedResponse);

            AuthResponseDTO response = googleAuthService.authenticateWithGoogle(request);

            assertNotNull(response);
            assertEquals(expectedResponse, response);
            verify(tokenService).generateToken(existingUser);
        }
    }

    @Test
    void authenticateWithGoogle_ValidToken_NewUser_CreatesUserAndAccount() throws Exception {
        String token = "valid-token";
        String googleId = "123";
        String email = "newuser@example.com";
        GoogleAuthRequestDTO request = new GoogleAuthRequestDTO(token);

        GoogleIdToken idToken = mock(GoogleIdToken.class);
        Payload payload = mock(Payload.class);
        when(idToken.getPayload()).thenReturn(payload);
        when(payload.getSubject()).thenReturn(googleId);
        when(payload.getEmail()).thenReturn(email);

        GoogleIdTokenVerifier verifier = mock(GoogleIdTokenVerifier.class);
        when(verifier.verify(token)).thenReturn(idToken);

        try (MockedConstruction<GoogleIdTokenVerifier.Builder> ignored = mockConstruction(
            GoogleIdTokenVerifier.Builder.class,
            (mock, context) -> {
                when(mock.setAudience(any(List.class))).thenReturn(mock);
                when(mock.build()).thenReturn(verifier);
            })) {

            when(googleAccountRepository.findByGoogleId(googleId)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Usuario> userCaptor = ArgumentCaptor.forClass(Usuario.class);
            when(usuarioRepository.save(userCaptor.capture())).thenAnswer(inv -> {
                Usuario user = inv.getArgument(0);
                user.setUserId("1");
                return user;
            });

            ArgumentCaptor<GoogleAccount> accountCaptor = ArgumentCaptor.forClass(GoogleAccount.class);
            when(googleAccountRepository.save(accountCaptor.capture())).thenAnswer(inv -> {
                GoogleAccount account = inv.getArgument(0);
                return account;
            });

            AuthResponseDTO expectedResponse = new AuthResponseDTO("access-token", "refresh-token");
            when(tokenService.generateToken(any(Usuario.class))).thenReturn(expectedResponse);

            AuthResponseDTO response = googleAuthService.authenticateWithGoogle(request);

            assertNotNull(response);
            assertEquals(expectedResponse, response);

            Usuario savedUser = userCaptor.getValue();
            assertEquals(email, savedUser.getUsername());
            assertEquals(email, savedUser.getEmail());
            assertDoesNotThrow(() -> UUID.fromString(savedUser.getSenha()));

            GoogleAccount savedAccount = accountCaptor.getValue();
            assertEquals(googleId, savedAccount.getGoogleId());
            assertEquals(savedUser, savedAccount.getUsuario());
        }
    }

    @Test
    void authenticateWithGoogle_TokenVerificationException_ThrowsException() throws Exception {
        GoogleAuthRequestDTO request = new GoogleAuthRequestDTO("error-token");

        GoogleIdTokenVerifier verifier = mock(GoogleIdTokenVerifier.class);
        when(verifier.verify(anyString())).thenThrow(new IOException("Verification error"));

        try (MockedConstruction<GoogleIdTokenVerifier.Builder> ignored = mockConstruction(
                GoogleIdTokenVerifier.Builder.class,
                (mock, context) -> {
                    when(mock.setAudience(any(List.class))).thenReturn(mock);
                    when(mock.build()).thenReturn(verifier);
                })) {

            Exception exception = assertThrows(RuntimeException.class,
                    () -> googleAuthService.authenticateWithGoogle(request));

            assertTrue(exception.getMessage().contains("Erro ao verificar token Google"));
        }
    }
}