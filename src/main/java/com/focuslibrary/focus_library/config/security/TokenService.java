package com.focuslibrary.focus_library.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.focuslibrary.focus_library.exeptions.FocusLibraryExeption;
import com.focuslibrary.focus_library.model.Usuario;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.chave}")
    private String chave;

    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(chave);
            String token = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(usuario.getUsername())
                    .withExpiresAt(getExpirationData())
                    .sign(algorithm);
            return token;
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

    private Instant getExpirationData() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
