package com.focuslibrary.focus_library.service.sessao;

import java.util.List;

import com.focuslibrary.focus_library.dto.AtividadeDTO;

public interface AtividadeService {

    AtividadeDTO addAtividade(AtividadeDTO atividadeDTO);

    List <AtividadeDTO> getUserAtividades();
}
