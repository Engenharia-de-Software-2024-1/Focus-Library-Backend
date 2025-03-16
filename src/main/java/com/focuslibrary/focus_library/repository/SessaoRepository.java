package com.focuslibrary.focus_library.repository;

import com.focuslibrary.focus_library.model.Sessao;
import com.focuslibrary.focus_library.model.SessaoId;
import com.focuslibrary.focus_library.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, SessaoId> {

    List<Sessao> findByUsuario(Usuario usuario);
}
