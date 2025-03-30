package com.focuslibrary.focus_library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SessaoDTO {

    @NotNull(message = "tempo de descanso invalido")
    @Min(value = 0, message = "tempo de descanso invalido")
    private Integer segundos_descanso;

    @NotNull(message = "tempo de foco invalido")
    @Min(value = 0, message = "tempo de foco invalido")
    private Integer segundos_foco;
}
