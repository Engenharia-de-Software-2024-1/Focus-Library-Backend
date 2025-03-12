package com.focuslibrary.focus_library.service.usuario;
import com.focuslibrary.focus_library.dto.UsuarioPostPutRequestDTO;
import com.focuslibrary.focus_library.dto.UsuarioResponseDTO;
import com.focuslibrary.focus_library.model.Usuario;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponseDTO> listarUsers();

    UsuarioResponseDTO addUsuario(UsuarioPostPutRequestDTO usuarioDTO);

    void deleteUsuario(Long idUser, String senha);

    UsuarioResponseDTO editarUsuario(Long idUser, String senha, UsuarioPostPutRequestDTO usuarioDTO);

    UsuarioResponseDTO getUsuario(Long idUser);
}
