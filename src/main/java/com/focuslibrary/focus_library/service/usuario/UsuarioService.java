package com.focuslibrary.focus_library.service.usuario;
import java.util.List;

import com.focuslibrary.focus_library.dto.UsuarioPostPutRequestDTO;
import com.focuslibrary.focus_library.dto.UsuarioResponseDTO;

public interface UsuarioService {

    List<UsuarioResponseDTO> listarUsers();

    void deleteUsuario(String idUser);

    UsuarioResponseDTO editarUsuario(String idUser, UsuarioPostPutRequestDTO usuarioDTO);

    UsuarioResponseDTO getUsuario(String idUser);

    UsuarioResponseDTO getUsuarioByToken();

    List<UsuarioResponseDTO> getRanking();
}