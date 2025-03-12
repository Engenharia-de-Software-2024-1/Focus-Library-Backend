package com.focuslibrary.focus_library.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPostPutRequestDTO {

    @JsonProperty("username")
    @NotBlank(message = "username invalido")
    private String username;

    @JsonProperty("senha")
    private String senha;

    @JsonProperty("email")
    private String email;

    @JsonProperty("dataNascimento")
    private LocalDate dataNascimento;
}
