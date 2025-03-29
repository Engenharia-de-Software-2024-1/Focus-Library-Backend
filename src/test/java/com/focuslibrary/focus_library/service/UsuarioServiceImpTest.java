package com.focuslibrary.focus_library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.focuslibrary.focus_library.config.security.TokenService;
import com.focuslibrary.focus_library.dto.UsuarioPostPutRequestDTO;
import com.focuslibrary.focus_library.dto.UsuarioResponseDTO;
import com.focuslibrary.focus_library.exceptions.FocusLibraryException;
import com.focuslibrary.focus_library.exceptions.UsuarioNaoExisteException;
import com.focuslibrary.focus_library.model.Sessao;
import com.focuslibrary.focus_library.model.Usuario;
import com.focuslibrary.focus_library.repository.SessaoRepository;
import com.focuslibrary.focus_library.repository.UsuarioRepository;
import com.focuslibrary.focus_library.service.usuario.UsuarioServiceImp;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImpTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UsuarioServiceImp usuarioService;

    private Usuario usuario;
    private UsuarioPostPutRequestDTO usuarioDTO;
    private UsuarioResponseDTO usuarioResponseDTO;
    private static final String USER_ID = "123";
    private static final String USERNAME = "testuser";
    private static final String EMAIL = "test@example.com";
    private static final String SENHA = "password123";
    private static final LocalDate DATA_NASCIMENTO = LocalDate.of(1990, 1, 1);

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .userId(USER_ID)
                .username(USERNAME)
                .email(EMAIL)
                .senha(SENHA)
                .dataNascimento(DATA_NASCIMENTO)
                .build();

        usuarioDTO = UsuarioPostPutRequestDTO.builder()
                .username(USERNAME)
                .email(EMAIL)
                .senha(SENHA)
                .dataNascimento(DATA_NASCIMENTO)
                .build();

        usuarioResponseDTO = UsuarioResponseDTO.builder()
                .userId(USER_ID)
                .username(USERNAME)
                .email(EMAIL)
                .dataNascimento(DATA_NASCIMENTO)
                .build();
    }

    @Test
    void listarUsers_ShouldReturnAllUsers() {
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioRepository.findAll()).thenReturn(usuarios);
        when(modelMapper.map(any(Usuario.class), eq(UsuarioResponseDTO.class))).thenReturn(usuarioResponseDTO);

        List<UsuarioResponseDTO> result = usuarioService.listarUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(USER_ID, result.get(0).getUserId());
        assertEquals(USERNAME, result.get(0).getUsername());
        assertEquals(EMAIL, result.get(0).getEmail());
        assertEquals(DATA_NASCIMENTO, result.get(0).getDataNascimento());
    }

    @Test
    void deleteUsuario_WithValidUser_ShouldDeleteUser() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(USERNAME);
            when(usuarioRepository.findById(USER_ID)).thenReturn(Optional.of(usuario));

            usuarioService.deleteUsuario(USER_ID);

            verify(usuarioRepository).delete(usuario);
        }
    }

    @Test
    void deleteUsuario_WithInvalidUser_ShouldThrowException() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(USERNAME);
            when(usuarioRepository.findById(USER_ID)).thenReturn(Optional.empty());

            assertThrows(UsuarioNaoExisteException.class, () -> usuarioService.deleteUsuario(USER_ID));
            verify(usuarioRepository, never()).delete(any());
        }
    }

    @Test
    void deleteUsuario_WithInvalidToken_ShouldThrowException() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(null);

            assertThrows(FocusLibraryException.class, () -> usuarioService.deleteUsuario(USER_ID));
            verify(usuarioRepository, never()).delete(any());
        }
    }

    @Test
    void deleteUsuario_WithUnauthorizedUser_ShouldThrowException() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn("differentUser");
            when(usuarioRepository.findById(USER_ID)).thenReturn(Optional.of(usuario));

            assertThrows(FocusLibraryException.class, () -> usuarioService.deleteUsuario(USER_ID));
            verify(usuarioRepository, never()).delete(any());
        }
    }

    @Test
    void editarUsuario_WithValidUser_ShouldUpdateUser() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(USERNAME);
            when(usuarioRepository.findById(USER_ID)).thenReturn(Optional.of(usuario));
            when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
            when(usuarioRepository.save(any())).thenReturn(usuario);
            
            // Setup mock for both map calls
            doAnswer(invocation -> {
                UsuarioPostPutRequestDTO source = invocation.getArgument(0);
                Usuario target = invocation.getArgument(1);
                target.setUsername(source.getUsername());
                target.setEmail(source.getEmail());
                target.setDataNascimento(source.getDataNascimento());
                return target;
            }).when(modelMapper).map(any(UsuarioPostPutRequestDTO.class), any(Usuario.class));
            
            when(modelMapper.map(any(Usuario.class), eq(UsuarioResponseDTO.class))).thenReturn(usuarioResponseDTO);

            UsuarioResponseDTO result = usuarioService.editarUsuario(USER_ID, usuarioDTO);

            assertNotNull(result);
            assertEquals(USER_ID, result.getUserId());
            assertEquals(USERNAME, result.getUsername());
            assertEquals(EMAIL, result.getEmail());
            assertEquals(DATA_NASCIMENTO, result.getDataNascimento());
            verify(usuarioRepository).save(any(Usuario.class));
            verify(modelMapper).map(any(UsuarioPostPutRequestDTO.class), any(Usuario.class));
            verify(modelMapper).map(any(Usuario.class), eq(UsuarioResponseDTO.class));
        }
    }

    @Test
    void editarUsuario_WithInvalidToken_ShouldThrowException() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(null);

            assertThrows(FocusLibraryException.class, () -> usuarioService.editarUsuario(USER_ID, usuarioDTO));
            verify(usuarioRepository, never()).save(any());
        }
    }

    @Test
    void editarUsuario_WithUnauthorizedUser_ShouldThrowException() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn("differentUser");
            when(usuarioRepository.findById(USER_ID)).thenReturn(Optional.of(usuario));

            assertThrows(FocusLibraryException.class, () -> usuarioService.editarUsuario(USER_ID, usuarioDTO));
            verify(usuarioRepository, never()).save(any());
        }
    }

    @Test
    void getUsuario_WithValidUser_ShouldReturnUser() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(USERNAME);
            when(usuarioRepository.findById(USER_ID)).thenReturn(Optional.of(usuario));
            when(sessaoRepository.findByUsuario(any())).thenReturn(Arrays.asList());
            when(modelMapper.map(any(Usuario.class), eq(UsuarioResponseDTO.class))).thenReturn(usuarioResponseDTO);

            UsuarioResponseDTO result = usuarioService.getUsuario(USER_ID);

            assertNotNull(result);
            assertEquals(USER_ID, result.getUserId());
            assertEquals(USERNAME, result.getUsername());
            assertEquals(EMAIL, result.getEmail());
            assertEquals(DATA_NASCIMENTO, result.getDataNascimento());
            assertEquals(0L, result.getStreak());
        }
    }

    @Test
    void getUsuario_WithInvalidToken_ShouldThrowException() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(null);

            assertThrows(FocusLibraryException.class, () -> usuarioService.getUsuario(USER_ID));
            verify(usuarioRepository, never()).findById(any());
        }
    }

    @Test
    void getUsuario_WithUnauthorizedUser_ShouldThrowException() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn("differentUser");
            when(usuarioRepository.findById(USER_ID)).thenReturn(Optional.of(usuario));

            assertThrows(FocusLibraryException.class, () -> usuarioService.getUsuario(USER_ID));
        }
    }

    @Test
    void getRanking_ShouldReturnSortedUsersByStreak() {
        Usuario usuario2 = Usuario.builder()
                .userId("456")
                .username("user2")
                .build();

        UsuarioResponseDTO usuarioResponseDTO2 = UsuarioResponseDTO.builder()
                .userId("456")
                .username("user2")
                .build();

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario, usuario2));
        when(sessaoRepository.findByUsuario(usuario)).thenReturn(Arrays.asList(
                Sessao.builder().data(LocalDate.now()).build(),
                Sessao.builder().data(LocalDate.now().minusDays(1)).build()
        ));
        when(sessaoRepository.findByUsuario(usuario2)).thenReturn(Arrays.asList(
                Sessao.builder().data(LocalDate.now()).build()
        ));
        when(modelMapper.map(usuario, UsuarioResponseDTO.class)).thenReturn(usuarioResponseDTO);
        when(modelMapper.map(usuario2, UsuarioResponseDTO.class)).thenReturn(usuarioResponseDTO2);

        List<UsuarioResponseDTO> result = usuarioService.getRanking();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getStreak());
        assertEquals(1L, result.get(1).getStreak());
    }
} 