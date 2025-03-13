package com.focuslibrary.focus_library.service.usuario;

import com.focuslibrary.focus_library.dto.UsuarioResponseDTO;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponseDTO> listarUsuarios();
}
