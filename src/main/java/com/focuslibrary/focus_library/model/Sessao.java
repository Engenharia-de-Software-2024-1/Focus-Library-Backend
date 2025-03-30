package com.focuslibrary.focus_library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long idSessao;

    @Column(nullable = false)
    private Integer segundos_descanso;

    @Column(nullable = false)
    private Integer segundos_foco;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "atividade_id", referencedColumnName = "atividade_id"),
            @JoinColumn(name = "usuario_id", referencedColumnName = "usuario_id")
    })
    @JsonIgnore
    private Atividade atividade;
}
