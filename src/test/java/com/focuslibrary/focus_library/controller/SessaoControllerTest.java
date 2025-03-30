package com.focuslibrary.focus_library.controller;

import com.focuslibrary.focus_library.dto.SessaoDTO;
import com.focuslibrary.focus_library.service.sessao.SessaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SessaoControllerTest {

    @Mock
    private SessaoService sessaoService;

    @InjectMocks
    private SessaoController sessaoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addSessao_ShouldReturnCreatedStatusAndResponseDTO() {
        // Arrange
        SessaoPostPutRequestDTO requestDTO = new SessaoPostPutRequestDTO();
        requestDTO.setMinutos(60);
        requestDTO.setData("2024-03-16");
        requestDTO.setIdSessao(1L);

        SessaoDTO expectedResponse = SessaoDTO.builder()
                .sessaoId(new SessaoId())
                .data(LocalDate.parse("2024-03-16"))
                .minutos(60)
                .build();

        when(sessaoService.addSessao(any(SessaoPostPutRequestDTO.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = sessaoController.addSessao(requestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        verify(sessaoService, times(1)).addSessao(any(SessaoPostPutRequestDTO.class));
    }

    @Test
    void addSessaoList_ShouldReturnCreatedStatusAndListOfResponseDTO() {
        // Arrange
        SessaoPostPutRequestDTO requestDTO1 = new SessaoPostPutRequestDTO();
        requestDTO1.setMinutos(60);
        requestDTO1.setData("2024-03-16");
        requestDTO1.setIdSessao(1L);

        SessaoPostPutRequestDTO requestDTO2 = new SessaoPostPutRequestDTO();
        requestDTO2.setMinutos(30);
        requestDTO2.setData("2024-03-17");
        requestDTO2.setIdSessao(2L);

        List<SessaoPostPutRequestDTO> requestDTOs = Arrays.asList(requestDTO1, requestDTO2);

        SessaoDTO responseDTO1 = SessaoDTO.builder()
                .sessaoId(new SessaoId())
                .data(LocalDate.parse("2024-03-16"))
                .minutos(60)
                .build();

        SessaoDTO responseDTO2 = SessaoDTO.builder()
                .sessaoId(new SessaoId())
                .data(LocalDate.parse("2024-03-17"))
                .minutos(30)
                .build();

        List<SessaoDTO> expectedResponses = Arrays.asList(responseDTO1, responseDTO2);

        when(sessaoService.addSessao(anyList())).thenReturn(expectedResponses);

        // Act
        ResponseEntity<?> response = sessaoController.addSessaoList(requestDTOs);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponses, response.getBody());
        verify(sessaoService, times(1)).addSessao(anyList());
    }

    @Test
    void getAllSessao_ShouldReturnOkStatusAndListOfResponseDTO() {
        // Arrange
        SessaoDTO responseDTO1 = SessaoDTO.builder()
                .sessaoId(new SessaoId())
                .data(LocalDate.parse("2024-03-16"))
                .minutos(60)
                .build();

        SessaoDTO responseDTO2 = SessaoDTO.builder()
                .sessaoId(new SessaoId())
                .data(LocalDate.parse("2024-03-17"))
                .minutos(30)
                .build();

        List<SessaoDTO> expectedResponses = Arrays.asList(responseDTO1, responseDTO2);

        when(sessaoService.getUserSessao()).thenReturn(expectedResponses);

        // Act
        ResponseEntity<?> response = sessaoController.getAllSessao();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponses, response.getBody());
        verify(sessaoService, times(1)).getUserSessao();
    }
} 