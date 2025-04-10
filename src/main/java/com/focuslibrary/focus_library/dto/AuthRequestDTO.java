package com.focuslibrary.focus_library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDTO {

    @NotBlank(message = "Username Invalido")
    private String username;

    @NotBlank(message = "Senha Invalida")
    private String senha;
}
