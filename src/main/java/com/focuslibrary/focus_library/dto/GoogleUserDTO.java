package com.focuslibrary.focus_library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserDTO {
    private String id;
    private String email;
    private String name;
    private String picture;
    private String locale;
    private boolean verified_email;
} 