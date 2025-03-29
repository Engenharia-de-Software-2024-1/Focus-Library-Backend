package com.focuslibrary.focus_library.service.sessao;

import java.util.List;

import com.focuslibrary.focus_library.dto.SessaoPostPutRequestDTO;
import com.focuslibrary.focus_library.dto.SessaoResponseDTO;

public interface SessaoService {

    SessaoResponseDTO addSessao(SessaoPostPutRequestDTO sessaoDTO);

    List<SessaoResponseDTO> addSessao(List<SessaoPostPutRequestDTO> sessaoDTO);

    List <SessaoResponseDTO> getUserSessao();
}
