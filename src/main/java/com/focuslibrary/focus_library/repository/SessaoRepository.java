package com.focuslibrary.focus_library.repository;

import com.focuslibrary.focus_library.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SessaoRepository extends JpaRepository<Sessao, Long> {
}
