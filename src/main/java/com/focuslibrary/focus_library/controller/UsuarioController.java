package com.focuslibrary.focus_library.controller;

import com.focuslibrary.focus_library.dto.UsuarioPostPutRequestDTO;
import com.focuslibrary.focus_library.dto.UsuarioResponseDTO;
import com.focuslibrary.focus_library.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarUsers();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{idUser}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuario(@PathVariable String idUser) {
        UsuarioResponseDTO usuario = usuarioService.getUsuario(idUser);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody UsuarioPostPutRequestDTO usuarioDTO) {
        UsuarioResponseDTO usuario = usuarioService.addUsuario(usuarioDTO);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{idUser}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @PathVariable String idUser,
            @RequestBody UsuarioPostPutRequestDTO usuarioDTO) {
        UsuarioResponseDTO usuario = usuarioService.editarUsuario(idUser, usuarioDTO);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{idUser}")
    public ResponseEntity<Void> deletarUsuario(
            @PathVariable String idUser) {
        usuarioService.deleteUsuario(idUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<UsuarioResponseDTO>> getRanking() {
        List<UsuarioResponseDTO> ranking = usuarioService.getRanking();
        return ResponseEntity.ok(ranking);
    }
}
