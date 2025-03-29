package com.focuslibrary.focus_library.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrocaDadosUserDTO {

    @NotBlank(message = "username invalido")
    private String username;

    @NotBlank(message = "email invalido")
    private String email;

    @NotNull(message = "dataNascimento invalida")
    private LocalDate dataNascimento;
}
