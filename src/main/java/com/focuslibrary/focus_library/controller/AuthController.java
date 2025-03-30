package com.focuslibrary.focus_library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.focuslibrary.focus_library.config.security.TokenService;
import com.focuslibrary.focus_library.dto.AuthRegisterDTO;
import com.focuslibrary.focus_library.dto.AuthRequestDTO;
import com.focuslibrary.focus_library.dto.AuthResponseDTO;
import com.focuslibrary.focus_library.dto.GoogleAuthRequestDTO;
import com.focuslibrary.focus_library.exceptions.FocusLibraryException;
import com.focuslibrary.focus_library.model.Usuario;
import com.focuslibrary.focus_library.service.auth.AuthServiceImp;
import com.focuslibrary.focus_library.service.auth.GoogleAuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthServiceImp authImp;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private GoogleAuthService googleAuthService;


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid final AuthRequestDTO authDTO
    ) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(
                                    authDTO.getUsername(),
                                    authDTO.getSenha()
                                );
        var auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        return ResponseEntity
                .status(HttpStatus.OK).body(token);
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(
            @RequestBody @Valid final AuthRegisterDTO authDTO
    ) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(authImp.registrar(authDTO));
        } catch (FocusLibraryException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(
            @RequestBody @Valid final GoogleAuthRequestDTO googleAuthRequest
    ) {
        try {
            AuthResponseDTO response =
            googleAuthService.authenticateWithGoogle(googleAuthRequest);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro na autenticação com Google: " + e.getMessage());
        }
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestParam final String refreshToken
    ) {
        String token = tokenService.getAcessToken(refreshToken);
        System.out.println(refreshToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }
}
