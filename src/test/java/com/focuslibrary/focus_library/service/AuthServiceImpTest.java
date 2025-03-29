package com.focuslibrary.focus_library.service;

import com.focuslibrary.focus_library.dto.AuthRegisterDTO;
import com.focuslibrary.focus_library.dto.UsuarioResponseDTO;
import com.focuslibrary.focus_library.exceptions.FocusLibraryException;
import com.focuslibrary.focus_library.model.Usuario;
import com.focuslibrary.focus_library.repository.UsuarioRepository;
import com.focuslibrary.focus_library.service.auth.AuthServiceImp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImpTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AuthServiceImp authService;

    private AuthRegisterDTO authRegisterDTO;
    private Usuario usuario;
    private UsuarioResponseDTO usuarioResponseDTO;

    @BeforeEach
    void setUp() {
        authRegisterDTO = new AuthRegisterDTO();
        authRegisterDTO.setUsername("testuser");
        authRegisterDTO.setSenha("password123");
        authRegisterDTO.setEmail("test@example.com");

        usuario = new Usuario();
        usuario.setUsername("testuser");
        usuario.setSenha("encodedPassword");
        usuario.setEmail("test@example.com");

        usuarioResponseDTO = new UsuarioResponseDTO();
        usuarioResponseDTO.setUsername("testuser");
        usuarioResponseDTO.setEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        when(usuarioRepository.findByUsername("testuser")).thenReturn(usuario);

        UserDetails result = authService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(usuarioRepository).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowException() {
        when(usuarioRepository.findByUsername("nonexistent")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> 
            authService.loadUserByUsername("nonexistent")
        );
        verify(usuarioRepository).findByUsername("nonexistent");
    }

    @Test
    void registrar_WhenUsernameDoesNotExist_ShouldRegisterUser() {
        when(usuarioRepository.findByUsername(any())).thenReturn(null);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(modelMapper.map(any(AuthRegisterDTO.class), eq(Usuario.class))).thenReturn(usuario);
        when(modelMapper.map(any(Usuario.class), eq(UsuarioResponseDTO.class))).thenReturn(usuarioResponseDTO);

        UsuarioResponseDTO result = authService.registrar(authRegisterDTO);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(usuarioRepository).findByUsername(authRegisterDTO.getUsername());
        verify(usuarioRepository).save(any(Usuario.class));
        verify(modelMapper).map(any(AuthRegisterDTO.class), eq(Usuario.class));
        verify(modelMapper).map(any(Usuario.class), eq(UsuarioResponseDTO.class));
    }

    @Test
    void registrar_WhenUsernameExists_ShouldThrowException() {
        when(usuarioRepository.findByUsername(any())).thenReturn(usuario);

        assertThrows(FocusLibraryException.class, () -> 
            authService.registrar(authRegisterDTO)
        );
        verify(usuarioRepository).findByUsername(authRegisterDTO.getUsername());
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(modelMapper, never()).map(any(), any());
    }
} 