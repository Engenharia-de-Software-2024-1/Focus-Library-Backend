package com.focuslibrary.focus_library.controller;

import com.focuslibrary.focus_library.dto.AuthRequestDTO;
import com.focuslibrary.focus_library.dto.AuthResponseDTO;
import com.focuslibrary.focus_library.exeptions.FocusLibraryExeption;
import com.focuslibrary.focus_library.model.Usuario;
import com.focuslibrary.focus_library.config.security.TokenService;
import com.focuslibrary.focus_library.service.auth.AuthServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthServiceImp authImp;

    @Autowired
    private TokenService tokenService;


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid AuthRequestDTO authDTO
            ) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getSenha());
        var auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        return ResponseEntity
                .status(HttpStatus.OK).body(token);
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(
            @RequestBody @Valid AuthRequestDTO authDTO) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(authImp.registrar(authDTO));
        } catch (FocusLibraryExeption e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestParam String refreshToken) {
        String token = tokenService.getAcessToken(refreshToken);
        System.out.println(refreshToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }
}
