package com.focuslibrary.focus_library.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @JsonProperty("username")
    @Column(nullable = false, unique = true)
    private String username;

    @JsonProperty("senha")
    @Column(nullable = false)
    private String senha;

    @JsonProperty("email")
    private String email;

    @JsonProperty("dataNascimento")
    private LocalDate dataNascimento;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Sessao> sessoes = new ArrayList<>();
}