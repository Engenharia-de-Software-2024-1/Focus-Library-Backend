package com.focuslibrary.focus_library.controller;

import com.focuslibrary.focus_library.dto.AuthDTO;
import com.focuslibrary.focus_library.dto.UsuarioResponseDTO;
import com.focuslibrary.focus_library.exeptions.FocusLibraryExeption;
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


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid AuthDTO authDTO
            ) {
        System.out.println("opa");
        var usernamePassword = new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getSenha());
        var auth = authenticationManager.authenticate(usernamePassword);


        return ResponseEntity
                .status(HttpStatus.OK).build();
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(
            @RequestBody @Valid AuthDTO authDTO) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(authImp.registrar(authDTO));
        } catch (FocusLibraryExeption e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
