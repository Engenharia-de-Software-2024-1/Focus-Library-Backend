package com.focuslibrary.focus_library.service;

import com.focuslibrary.focus_library.config.security.TokenService;
import com.focuslibrary.focus_library.dto.SessaoDTO;
import com.focuslibrary.focus_library.exceptions.FocusLibraryException;
import com.focuslibrary.focus_library.model.Sessao;
import com.focuslibrary.focus_library.model.SessaoId;
import com.focuslibrary.focus_library.model.Usuario;
import com.focuslibrary.focus_library.repository.SessaoRepository;
import com.focuslibrary.focus_library.repository.UsuarioRepository;
import com.focuslibrary.focus_library.service.sessao.SessaoServiceImp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoServiceImpTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SessaoRepository sessaoRepository;

    @InjectMocks
    private SessaoServiceImp sessaoService;

    private Usuario usuario;
    private SessaoPostPutRequestDTO sessaoDTO;
    private static final String USER_ID = "123";
    private static final String USERNAME = "testuser";
    private static final Integer MINUTOS = 60;
    private static final String DATA = "16/03/2024";
    private static final Long SESSAO_ID = 1L;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .userId(USER_ID)
                .username(USERNAME)
                .build();

        sessaoDTO = new SessaoPostPutRequestDTO();
        sessaoDTO.setMinutos(MINUTOS);
        sessaoDTO.setData(DATA);
        sessaoDTO.setIdSessao(SESSAO_ID);
    }

    @Test
    void addSessao_WithValidData_ShouldCreateSessao() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            // Arrange
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(USERNAME);
            when(usuarioRepository.findByUsername(USERNAME)).thenReturn(usuario);
            when(sessaoRepository.findById(any(SessaoId.class))).thenReturn(Optional.empty());
            Sessao savedSessao = Sessao.builder()
                    .sessaoId(new SessaoId(SESSAO_ID, USER_ID))
                    .data(LocalDate.parse(DATA, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .minutos(MINUTOS)
                    .build();
            when(sessaoRepository.save(any(Sessao.class))).thenReturn(savedSessao);
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

            // Act
            SessaoDTO result = sessaoService.addSessao(sessaoDTO);

            // Assert
            assertNotNull(result);
            assertEquals(SESSAO_ID, result.getSessaoId().getSessaoId());
            assertEquals(USER_ID, result.getSessaoId().getUsuarioId());
            assertEquals(LocalDate.parse(DATA, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")), result.getData());
            assertEquals(MINUTOS, result.getMinutos());
            verify(sessaoRepository).save(any(Sessao.class));
            verify(usuarioRepository).save(any(Usuario.class));
        }
    }

    @Test
    void addSessao_WithInvalidToken_ShouldThrowException() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            // Arrange
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(null);

            // Act & Assert
            assertThrows(FocusLibraryException.class, () -> sessaoService.addSessao(sessaoDTO));
            verify(sessaoRepository, never()).save(any());
        }
    }

    @Test
    void addSessao_WithExistingSessao_ShouldThrowException() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            // Arrange
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(USERNAME);
            when(usuarioRepository.findByUsername(USERNAME)).thenReturn(usuario);
            when(sessaoRepository.findById(any(SessaoId.class))).thenReturn(Optional.of(new Sessao()));

            // Act & Assert
            assertThrows(FocusLibraryException.class, () -> sessaoService.addSessao(sessaoDTO));
            verify(sessaoRepository, never()).save(any());
        }
    }

    @Test
    void addSessao_WithList_ShouldCreateMultipleSessoes() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            // Arrange
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(USERNAME);
            when(usuarioRepository.findByUsername(USERNAME)).thenReturn(usuario);
            when(sessaoRepository.findById(any(SessaoId.class))).thenReturn(Optional.empty());
            when(sessaoRepository.saveAll(anyList())).thenReturn(Arrays.asList(new Sessao()));

            SessaoPostPutRequestDTO sessaoDTO2 = new SessaoPostPutRequestDTO();
            sessaoDTO2.setMinutos(30);
            sessaoDTO2.setData("17/03/2024");
            sessaoDTO2.setIdSessao(2L);

            List<SessaoPostPutRequestDTO> sessoesDTO = Arrays.asList(sessaoDTO, sessaoDTO2);

            // Act
            List<SessaoDTO> result = sessaoService.addSessao(sessoesDTO);

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());
            verify(sessaoRepository).saveAll(anyList());
        }
    }

    @Test
    void getUserSessao_WithValidUser_ShouldReturnSessoes() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            // Arrange
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(USERNAME);
            when(usuarioRepository.findByUsername(USERNAME)).thenReturn(usuario);
            
            Sessao sessao = Sessao.builder()
                    .sessaoId(new SessaoId(SESSAO_ID, USER_ID))
                    .data(LocalDate.parse(DATA, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .minutos(MINUTOS)
                    .build();
            
            when(sessaoRepository.findByUsuario(usuario)).thenReturn(Arrays.asList(sessao));

            // Act
            List<SessaoDTO> result = sessaoService.getUserSessao();

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals(SESSAO_ID, result.get(0).getSessaoId().getSessaoId());
            assertEquals(USER_ID, result.get(0).getSessaoId().getUsuarioId());
            assertEquals(LocalDate.parse(DATA, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")), result.get(0).getData());
            assertEquals(MINUTOS, result.get(0).getMinutos());
        }
    }

    @Test
    void getUserSessao_WithInvalidToken_ShouldThrowException() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            // Arrange
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(null);

            // Act & Assert
            assertThrows(FocusLibraryException.class, () -> sessaoService.getUserSessao());
            verify(sessaoRepository, never()).findByUsuario(any());
        }
    }
} 