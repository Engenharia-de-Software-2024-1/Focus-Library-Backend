package com.focuslibrary.focus_library.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.focuslibrary.focus_library.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    @JsonProperty("id")
    private String  userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("dataNascimento")
    private LocalDate dataNascimento;

    @JsonProperty("streak")
    private Long streak;

    public UsuarioResponseDTO(final Usuario usuario) {
        this.userId = usuario.getUserId();
        this.username = usuario.getUsername();
        this.email = usuario.getEmail();
        this.dataNascimento = usuario.getDataNascimento();
    }
}
