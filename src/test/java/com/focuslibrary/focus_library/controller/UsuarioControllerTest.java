package com.focuslibrary.focus_library.controller;

import com.focuslibrary.focus_library.dto.UsuarioPostPutRequestDTO;
import com.focuslibrary.focus_library.dto.UsuarioResponseDTO;
import com.focuslibrary.focus_library.service.usuario.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioResponseDTO usuarioResponseDTO;
    private UsuarioPostPutRequestDTO usuarioRequestDTO;
    private String idUser;

    @BeforeEach
    void setUp() {
        idUser = "123";
        usuarioResponseDTO = UsuarioResponseDTO.builder()
                .userId(idUser)
                .username("Test User")
                .email("test@example.com")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .streak(0L)
                .build();

        usuarioRequestDTO = UsuarioPostPutRequestDTO.builder()
                .username("Test User")
                .email("test@example.com")
                .senha("password123")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    void listarUsuarios_ShouldReturnListOfUsers() {
        // Arrange
        List<UsuarioResponseDTO> expectedUsuarios = Arrays.asList(usuarioResponseDTO);
        when(usuarioService.listarUsers()).thenReturn(expectedUsuarios);

        // Act
        ResponseEntity<List<UsuarioResponseDTO>> response = usuarioController.listarUsuarios();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsuarios, response.getBody());
        verify(usuarioService).listarUsers();
    }

    @Test
    void buscarUsuario_ShouldReturnUser() {
        // Arrange
        when(usuarioService.getUsuario(idUser)).thenReturn(usuarioResponseDTO);

        // Act
        ResponseEntity<UsuarioResponseDTO> response = usuarioController.buscarUsuario(idUser);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarioResponseDTO, response.getBody());
        verify(usuarioService).getUsuario(idUser);
    }

    @Test
    void criarUsuario_ShouldCreateAndReturnUser() {
        // Arrange
        when(usuarioService.addUsuario(usuarioRequestDTO)).thenReturn(usuarioResponseDTO);

        // Act
        ResponseEntity<UsuarioResponseDTO> response = usuarioController.criarUsuario(usuarioRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarioResponseDTO, response.getBody());
        verify(usuarioService).addUsuario(usuarioRequestDTO);
    }

    @Test
    void atualizarUsuario_ShouldUpdateAndReturnUser() {
        // Arrange
        when(usuarioService.editarUsuario(idUser, usuarioRequestDTO)).thenReturn(usuarioResponseDTO);

        // Act
        ResponseEntity<UsuarioResponseDTO> response = usuarioController.atualizarUsuario(idUser, usuarioRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarioResponseDTO, response.getBody());
        verify(usuarioService).editarUsuario(idUser, usuarioRequestDTO);
    }

    @Test
    void deletarUsuario_ShouldDeleteUser() {
        // Act
        ResponseEntity<Void> response = usuarioController.deletarUsuario(idUser);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(usuarioService).deleteUsuario(idUser);
    }

    @Test
    void getRanking_ShouldReturnRankingList() {
        // Arrange
        List<UsuarioResponseDTO> expectedRanking = Arrays.asList(usuarioResponseDTO);
        when(usuarioService.getRanking()).thenReturn(expectedRanking);

        // Act
        ResponseEntity<List<UsuarioResponseDTO>> response = usuarioController.getRanking();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedRanking, response.getBody());
        verify(usuarioService).getRanking();
    }
} 