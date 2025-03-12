package com.focuslibrary.focus_library.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class SessaoId {

    @Column(name = "sessao_id")
    private Long sessaoId;

    @Column(name = "usuario_id")
    private Long usuarioId;
}
