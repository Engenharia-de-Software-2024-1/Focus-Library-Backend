package com.focuslibrary.focus_library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAuthRequestDTO {
    @NotBlank(message = "Token do Google é obrigatório")
    private String token;
}
