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
public class AtividadeId {

    @Column(name = "atividade_id")
    private String atividadeId;

    @Column(name = "usuario_id")
    private String usuarioId;
}
