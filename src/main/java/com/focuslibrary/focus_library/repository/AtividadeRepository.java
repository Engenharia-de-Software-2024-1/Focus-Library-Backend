package com.focuslibrary.focus_library.repository;

import com.focuslibrary.focus_library.model.Atividade;
import com.focuslibrary.focus_library.model.AtividadeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtividadeRepository extends JpaRepository<Atividade, AtividadeId> {
}
