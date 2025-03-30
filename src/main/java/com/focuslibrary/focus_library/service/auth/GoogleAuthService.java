package com.focuslibrary.focus_library.service.auth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.focuslibrary.focus_library.config.security.TokenService;
import com.focuslibrary.focus_library.dto.AuthResponseDTO;
import com.focuslibrary.focus_library.dto.GoogleAuthRequestDTO;
import com.focuslibrary.focus_library.model.GoogleAccount;
import com.focuslibrary.focus_library.model.Usuario;
import com.focuslibrary.focus_library.repository.GoogleAccountRepository;
import com.focuslibrary.focus_library.repository.UsuarioRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

@Service
public class GoogleAuthService {

    @Autowired
    private GoogleAccountRepository googleAccountRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    public AuthResponseDTO authenticateWithGoogle(
        final GoogleAuthRequestDTO request
    ) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new GsonFactory()
            )
                .setAudience(Collections.singletonList(clientId))
                .build();

            GoogleIdToken idToken = verifier.verify(request.getToken());
            if (idToken == null) {
                throw new RuntimeException("Token Google inválido");
            }

            Payload payload = idToken.getPayload();
            String googleId = payload.getSubject();
            String email = payload.getEmail();

            Optional<GoogleAccount> optionalGoogleAccount =
                googleAccountRepository.findByGoogleId(googleId);

            if (optionalGoogleAccount.isPresent()) {
                Usuario usuario = optionalGoogleAccount.get().getUsuario();
                return tokenService.generateToken(usuario);
            } else {
                Usuario novoUsuario = Usuario.builder()
                        .username(email)
                        .email(email)
                        .senha(passwordEncoder.encode(
                            UUID.randomUUID().toString()
                        ))
                        .build();

                usuarioRepository.save(novoUsuario);

                GoogleAccount googleAccount = GoogleAccount.builder()
                        .googleId(googleId)
                        .usuario(novoUsuario)
                        .build();

                googleAccountRepository.save(googleAccount);

                return tokenService.generateToken(novoUsuario);
            }

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(
                "Erro ao verificar token Google: " + e.getMessage()
            );
        }
    }
}
