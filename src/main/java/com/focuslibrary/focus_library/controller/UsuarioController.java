package com.focuslibrary.focus_library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.focuslibrary.focus_library.dto.TrocaDadosUserDTO;
import com.focuslibrary.focus_library.dto.UsuarioPostPutRequestDTO;
import com.focuslibrary.focus_library.dto.UsuarioResponseDTO;
import com.focuslibrary.focus_library.service.usuario.UsuarioService;

import jakarta.validation.Valid;

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

    @GetMapping("")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioToken(){
        UsuarioResponseDTO usuarioDTO = usuarioService.getUsuarioByToken();
        return ResponseEntity.status(HttpStatus.OK).body(usuarioDTO);
    }

    @PutMapping("/{idUser}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @PathVariable String idUser,
            @RequestBody UsuarioPostPutRequestDTO usuarioDTO) {
        UsuarioResponseDTO usuario = usuarioService.editarUsuario(idUser, usuarioDTO);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/dadosgerais/{idUser}")
    public ResponseEntity<UsuarioResponseDTO> atualizarDadosGerais(
            @RequestBody @Valid TrocaDadosUserDTO userDTO,
            @PathVariable String idUser
            ){
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.editarDadosGeraisUsuario(idUser, userDTO));
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
