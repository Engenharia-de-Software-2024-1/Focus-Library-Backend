package com.focuslibrary.focus_library.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAccount {

    @Id
    @JsonProperty("googleId")
    private String googleId;

    @JsonProperty("usuario")
    @OneToOne(cascade = CascadeType.ALL)
    private Usuario usuario;
}
