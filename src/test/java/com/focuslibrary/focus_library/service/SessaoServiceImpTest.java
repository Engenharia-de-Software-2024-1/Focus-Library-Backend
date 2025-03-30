package com.focuslibrary.focus_library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.focuslibrary.focus_library.config.security.TokenService;
import com.focuslibrary.focus_library.dto.AtividadeDTO;
import com.focuslibrary.focus_library.dto.SessaoDTO;
import com.focuslibrary.focus_library.exceptions.FocusLibraryException;
import com.focuslibrary.focus_library.model.Atividade;
import com.focuslibrary.focus_library.model.AtividadeId;
import com.focuslibrary.focus_library.model.Sessao;
import com.focuslibrary.focus_library.model.Usuario;
import com.focuslibrary.focus_library.repository.AtividadeRepository;
import com.focuslibrary.focus_library.repository.UsuarioRepository;
import com.focuslibrary.focus_library.service.sessao.AtividadeServiceImp;

@ExtendWith(MockitoExtension.class)
class SessaoServiceImpTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AtividadeRepository atividadeRepository;

    @InjectMocks
    private AtividadeServiceImp sessaoService;

    private Usuario usuario;
    private AtividadeDTO atividadeDTO;
    private List<Sessao> sessoes;
    private List<SessaoDTO> sessoesDTO;
    private static final String USER_ID = "123";
    private static final String USERNAME = "testuser";
    private static final String DATA = "2004-11-23";
    private static final String ATIVIDADE_ID = "1a";

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .userId(USER_ID)
                .username(USERNAME)
                .build();

        SessaoDTO sessao1 = new SessaoDTO(30, 30);
        Sessao s1 = Sessao.builder().idSessao(1L).segundos_foco(30).segundos_descanso(30).build();
        SessaoDTO sessao2 = new SessaoDTO(60, 40);
        Sessao s2 = Sessao.builder().idSessao(2L).segundos_foco(40).segundos_descanso(60).build();
        sessoes = new ArrayList<>();
        sessoes.add(s1);
        sessoes.add(s2);

        this.sessoesDTO = new ArrayList<>();
        sessoesDTO.add(sessao1);
        sessoesDTO.add(sessao2);

        atividadeDTO = new AtividadeDTO();
        atividadeDTO.setAtividadeId(ATIVIDADE_ID);
        atividadeDTO.setData(LocalDate.parse(DATA));
        atividadeDTO.setSessoes(sessoesDTO);
    }

    @Test
    void addAtividade_WithValidData_ShouldCreateAtividade() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            // Arrange
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(USERNAME);

            Usuario usuario = Usuario.builder()
                    .userId(USER_ID)
                    .username(USERNAME)
                    .build();
            when(usuarioRepository.findByUsername(USERNAME)).thenReturn(usuario);

            AtividadeId atividadeId = new AtividadeId(ATIVIDADE_ID, USER_ID);
            when(atividadeRepository.existsById(atividadeId)).thenReturn(false);

            AtividadeDTO atividadeDTO = new AtividadeDTO();
            atividadeDTO.setAtividadeId(ATIVIDADE_ID);
            atividadeDTO.setData(LocalDate.parse(DATA));

            when(usuarioRepository.save(usuario)).thenReturn(usuario);

            AtividadeDTO result = sessaoService.addAtividade(atividadeDTO);

            assertNotNull(result);
            assertEquals(ATIVIDADE_ID, result.getAtividadeId());
            assertEquals(LocalDate.parse(DATA), result.getData());

            verify(usuarioRepository, times(1)).save(usuario);

            verify(atividadeRepository, times(1)).existsById(atividadeId);
        }
    }

    @Test
    void addAtividade_WithInvalidToken_ShouldThrowException() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            // Arrange
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado)
                    .thenThrow(new FocusLibraryException("Token inválido"));

            // Act & Assert
            assertThrows(FocusLibraryException.class, () -> {
                sessaoService.addAtividade(atividadeDTO);
            });

            verify(usuarioRepository, never()).findByUsername(anyString());
            verify(atividadeRepository, never()).existsById(any());
        }
    }

    @Test
    void addAtividade_WithExistingSessao_ShouldThrowException() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            // Arrange
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(USERNAME);
            when(usuarioRepository.findByUsername(USERNAME)).thenReturn(usuario);

            AtividadeId atividadeId = new AtividadeId(ATIVIDADE_ID, USER_ID);
            when(atividadeRepository.existsById(atividadeId)).thenReturn(true);

            // Act & Assert
            assertThrows(FocusLibraryException.class, () -> {
                sessaoService.addAtividade(atividadeDTO);
            }, "Id Atividade Invalido");

            verify(atividadeRepository, times(1)).existsById(atividadeId);
            verify(usuarioRepository, never()).save(any());
        }
    }

    @Test
    void addAtividade_WithList_ShouldCreateMultipleAtividade() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            // Arrange
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(USERNAME);
            when(usuarioRepository.findByUsername(USERNAME)).thenReturn(usuario);

            AtividadeId atividadeId = new AtividadeId(ATIVIDADE_ID, USER_ID);
            when(atividadeRepository.existsById(atividadeId)).thenReturn(false);

            // Configura atividade com múltiplas sessões (já feito no setUp)
            when(usuarioRepository.save(usuario)).thenReturn(usuario);

            // Act
            AtividadeDTO result = sessaoService.addAtividade(atividadeDTO);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.getSessoes().size());
            verify(usuarioRepository, times(1)).save(usuario);
        }
    }

    @Test
    void getUserAtividade_WithValidUser_ShouldReturnAtividade() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado).thenReturn(USERNAME);

            Usuario usuario = Usuario.builder()
                    .userId(USER_ID)
                    .username(USERNAME)
                    .atividades(new ArrayList<>())
                    .build();

            when(usuarioRepository.findByUsername(USERNAME)).thenReturn(usuario);

            Atividade atividade = Atividade.builder()
                    .atividadeId(new AtividadeId(ATIVIDADE_ID, USER_ID))
                    .data(LocalDate.parse(DATA))
                    .sessoes(sessoes)
                    .usuario(usuario)
                    .build();

            usuario.getAtividades().add(atividade);

            List<AtividadeDTO> result = sessaoService.getUserAtividades();

            assertNotNull(result, "A lista retornada não deveria ser nula");
            assertEquals(1, result.size(), "Deveria retornar exatamente 1 atividade");

            AtividadeDTO dto = result.get(0);
            assertEquals(ATIVIDADE_ID, dto.getAtividadeId(), "ID da atividade incorreto");
            assertEquals(LocalDate.parse(DATA), dto.getData(), "Data da atividade incorreta");
            assertEquals(2, dto.getSessoes().size(), "Quantidade de sessões incorreta");
        }
    }

    @Test
    void getUserAtividade_WithInvalidToken_ShouldThrowException() {
        try (MockedStatic<TokenService> tokenServiceMock = mockStatic(TokenService.class)) {
            // Arrange
            tokenServiceMock.when(TokenService::getUsernameUsuarioLogado)
                    .thenThrow(new FocusLibraryException("Token inválido"));

            // Act & Assert
            assertThrows(FocusLibraryException.class, () -> {
                sessaoService.getUserAtividades();
            });

            verify(usuarioRepository, never()).findByUsername(anyString());
            verify(atividadeRepository, never()).findByUsuario(any());
        }
    }

} 