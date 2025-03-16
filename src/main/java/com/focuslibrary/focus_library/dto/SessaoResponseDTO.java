package com.focuslibrary.focus_library.dto;

import com.focuslibrary.focus_library.model.SessaoId;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class SessaoResponseDTO {

    private SessaoId sessaoId;

    private LocalDate data;

    private Integer minutos;
}
