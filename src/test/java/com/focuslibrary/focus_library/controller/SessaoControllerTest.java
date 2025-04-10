package com.focuslibrary.focus_library.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.focuslibrary.focus_library.dto.AtividadeDTO;
import com.focuslibrary.focus_library.dto.SessaoDTO;
import com.focuslibrary.focus_library.service.sessao.AtividadeServiceImp;

class SessaoControllerTest {

    @Mock
    private AtividadeServiceImp atividadeService;

    @InjectMocks
    private AtividadeController atividadeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addAtividade_ShouldReturnCreatedStatusAndResponseDTO() {
        // Arrange
        AtividadeDTO requestDTO = new AtividadeDTO();
        requestDTO.setAtividadeId("1");
        requestDTO.setData(LocalDate.parse("2024-03-16"));
        requestDTO.setSessoes(List.of(new SessaoDTO(30, 60)));

        AtividadeDTO expectedResponse = AtividadeDTO.builder()
                .atividadeId("1")
                .data(LocalDate.parse("2024-03-16"))
                .sessoes(List.of(new SessaoDTO(30, 60)))
                .build();

        when(atividadeService.addAtividade(any(AtividadeDTO.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<AtividadeDTO> response = atividadeController.addAtividade(requestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        verify(atividadeService, times(1)).addAtividade(any(AtividadeDTO.class));
    }

    @Test
    void getAllAtividades_ShouldReturnOkStatusAndListOfResponseDTO() {
        // Arrange
        AtividadeDTO responseDTO1 = AtividadeDTO.builder()
                .atividadeId("1")
                .data(LocalDate.parse("2024-03-16"))
                .sessoes(List.of(new SessaoDTO(30, 60)))
                .build();

        AtividadeDTO responseDTO2 = AtividadeDTO.builder()
                .atividadeId("2")
                .data(LocalDate.parse("2024-03-17"))
                .sessoes(List.of(new SessaoDTO(45, 15)))
                .build();

        List<AtividadeDTO> expectedResponses = Arrays.asList(responseDTO1, responseDTO2);

        when(atividadeService.getUserAtividades()).thenReturn(expectedResponses);

        // Act
        ResponseEntity<List<AtividadeDTO>> response = atividadeController.getAllAtividade();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(atividadeService, times(1)).getUserAtividades();
    }
}