package com.focuslibrary.focus_library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthResponseDTO {
    private String acessToken;

    private String refreshToken;
}
