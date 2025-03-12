package com.focuslibrary.focus_library.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sessao {

    @EmbeddedId
    @JsonProperty("sessaoId")
    private SessaoId sessaoId;

    @JsonProperty("data")
    @Column(nullable = false)
    private LocalDate data;

    @JsonProperty("minutos")
    @Column(nullable = false)
    private Integer minutos;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario usuario;
}
