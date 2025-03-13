package com.focuslibrary.focus_library.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.focuslibrary.focus_library.dto.AuthRequestDTO;
import com.focuslibrary.focus_library.dto.AuthResponseDTO;
import com.focuslibrary.focus_library.exeptions.FocusLibraryExeption;
import com.focuslibrary.focus_library.exeptions.InvalidRefreshToken;
import com.focuslibrary.focus_library.model.Usuario;
import com.auth0.jwt.algorithms.Algorithm;
import com.focuslibrary.focus_library.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.chave}")
    private String chave;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public AuthResponseDTO generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(chave);
            String acessToken = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(usuario.getUsername())
                    .withExpiresAt(getAcessExpirationData())
                    .sign(algorithm);

            String refreshToken = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(usuario.getUserId())
                    .withExpiresAt(getRefreshExpirationData())
                    .sign(algorithm);
            return new AuthResponseDTO(acessToken, refreshToken);
        } catch (JWTCreationException exception) {
            throw  new FocusLibraryExeption("Erro ao gerar token!");
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(chave);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    public String getAcessToken(String refreshToken) {
        try {
            String idUsuario = validateToken(refreshToken);
            System.out.println(idUsuario);
            Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(InvalidRefreshToken::new);

            Algorithm algorithm = Algorithm.HMAC256(chave);
            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(usuario.getUsername())
                    .withExpiresAt(getAcessExpirationData())
                    .sign(algorithm);
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    private Instant getRefreshExpirationData(){
        return LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.of("-03:00"));
    }

    private Instant getAcessExpirationData() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
