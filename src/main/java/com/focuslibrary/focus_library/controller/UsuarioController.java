package com.focuslibrary.focus_library.controller;

import com.focuslibrary.focus_library.dto.UsuarioPostPutRequestDTO;
import com.focuslibrary.focus_library.dto.UsuarioResponseDTO;
import com.focuslibrary.focus_library.service.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/usuario",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{idUser}")
    public ResponseEntity<?> getUsuario(
            @PathVariable Long idUser
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.getUsuario(idUser));
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.listarUsers());
    }

    @PostMapping("")
    public ResponseEntity<?> addUsuario(
            @RequestBody @Valid UsuarioPostPutRequestDTO usuarioDTO
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.addUsuario(usuarioDTO));
    }

    @DeleteMapping("/{idUser}")
    public ResponseEntity<?> deleteUsuario(
            @PathVariable Long idUser,
            @RequestParam String senha
    ) {
        usuarioService.deleteUsuario(idUser, senha);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("Usuario deletado com sucesso!");
    }

    @PutMapping("/{idUser}")
    public ResponseEntity<?> editUsuario(
            @RequestBody @Valid UsuarioPostPutRequestDTO usuarioDTO,
            @PathVariable Long idUser,
            @RequestParam String senha
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.editarUsuario(idUser, senha, usuarioDTO));
    }
}
