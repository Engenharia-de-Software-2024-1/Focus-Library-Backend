package com.focuslibrary.focus_library.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtividadeDTO {

    @NotBlank(message = "iD invalido")
    private String atividadeId;

    @NotNull(message = "data invalida")
    private LocalDate data;

    @NotNull(message = "sessoes invalida")
    private List<SessaoDTO> sessoes;
}
