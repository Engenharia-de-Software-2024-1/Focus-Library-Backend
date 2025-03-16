package com.focuslibrary.focus_library.service.usuario;
import com.focuslibrary.focus_library.dto.UsuarioPostPutRequestDTO;
import com.focuslibrary.focus_library.dto.UsuarioResponseDTO;
import com.focuslibrary.focus_library.model.Usuario;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponseDTO> listarUsers();

    UsuarioResponseDTO addUsuario(UsuarioPostPutRequestDTO usuarioDTO);

    void deleteUsuario(String idUser);

    UsuarioResponseDTO editarUsuario(String idUser, UsuarioPostPutRequestDTO usuarioDTO);

    UsuarioResponseDTO getUsuario(String idUser);

    List<UsuarioResponseDTO> getRanking();
}