package com.focuslibrary.focus_library.service.sessao;

import com.focuslibrary.focus_library.dto.SessaoPostPutRequestDTO;
import com.focuslibrary.focus_library.dto.SessaoResponseDTO;

import java.time.LocalTime;
import java.util.List;

public interface SessaoService {

    SessaoResponseDTO addSessao(SessaoPostPutRequestDTO sessaoDTO);

    List<SessaoResponseDTO> addSessao(List<SessaoPostPutRequestDTO> sessaoDTO);

    List <SessaoResponseDTO> getUserSessao();
}
