package com.focuslibrary.focus_library.dto;

import java.time.LocalDate;

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

    @NotBlank(message = "Data de Nascimento Invalida")
    private LocalDate dataNascimento;
}