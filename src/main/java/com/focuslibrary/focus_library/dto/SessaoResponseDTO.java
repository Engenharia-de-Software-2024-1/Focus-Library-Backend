package com.focuslibrary.focus_library.dto;

import java.time.LocalDate;

import com.focuslibrary.focus_library.model.SessaoId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SessaoResponseDTO {

    private SessaoId sessaoId;

    private LocalDate data;

    private Integer minutos;
}
