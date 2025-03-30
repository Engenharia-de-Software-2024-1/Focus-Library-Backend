package com.focuslibrary.focus_library.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
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
    private Integer segundosDescanso;

    @Column(nullable = false)
    private Integer segundosFoco;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "atividade_id", referencedColumnName = "atividade_id"),
            @JoinColumn(name = "usuario_id", referencedColumnName = "usuario_id")
    })
    @JsonIgnore
    private Atividade atividade;
}
