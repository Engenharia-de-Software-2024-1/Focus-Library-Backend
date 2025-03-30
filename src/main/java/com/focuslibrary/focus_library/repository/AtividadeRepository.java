package com.focuslibrary.focus_library.repository;

import com.focuslibrary.focus_library.model.Atividade;
import com.focuslibrary.focus_library.model.AtividadeId;
import com.focuslibrary.focus_library.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtividadeRepository
extends JpaRepository<Atividade, AtividadeId> {
    List<Atividade> findByUsuario(Usuario usuario);
}
