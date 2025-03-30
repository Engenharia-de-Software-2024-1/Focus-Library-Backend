package com.focuslibrary.focus_library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRegisterDTO {

    @NotBlank(message = "Username Invalido")
    private String username;

    @NotBlank(message = "Senha Invalida")
    private String senha;

    @NotBlank(message = "Email Invalido")
    private String email;

    //UI no front nao envia dataNascimento e NotBlanck Usado apenas com string
    //@NotBlank(message = "Data de Nascimento Invalida")
    //private LocalDate dataNascimento;
}
