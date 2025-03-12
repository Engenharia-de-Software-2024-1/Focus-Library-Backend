package com.focuslibrary.focus_library.service.usuario;

import com.focuslibrary.focus_library.dto.UsuarioPostPutRequestDTO;
import com.focuslibrary.focus_library.dto.UsuarioResponseDTO;
import com.focuslibrary.focus_library.exeptions.UsuarioNaoExisteExeption;
import com.focuslibrary.focus_library.model.Usuario;
import com.focuslibrary.focus_library.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImp implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<UsuarioResponseDTO> listarUsers() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> UsuarioResponseDTO.builder()
                        .userId(usuario.getUserId())
                        .username(usuario.getUsername())
                        .email(usuario.getEmail())
                        .dataNascimento(usuario.getDataNascimento())
                        .build())
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO addUsuario(UsuarioPostPutRequestDTO usuarioDTO) {
        Usuario usuario = Usuario.builder()
                .username(usuarioDTO.getUsername())
                .senha(usuarioDTO.getSenha())
                .email(usuarioDTO.getEmail())
                .dataNascimento(usuarioDTO.getDataNascimento())
                .build();
        usuario = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.builder()
                .userId(usuario.getUserId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .dataNascimento(usuario.getDataNascimento())
                .build();
    }

    public void deleteUsuario(Long idUser, String senha) {
        Usuario usuario = usuarioRepository.findById(idUser).orElseThrow(UsuarioNaoExisteExeption::new);
        if (!usuario.getSenha().equals(senha)) {
            throw new UsuarioNaoExisteExeption();
        }
        usuarioRepository.delete(usuario);
    }

    public UsuarioResponseDTO editarUsuario(Long idUser, String senha, UsuarioPostPutRequestDTO usuarioDTO){
        Usuario usuario = usuarioRepository.findById(idUser).orElseThrow(UsuarioNaoExisteExeption::new);
        if (!usuario.getSenha().equals(senha)) {
            throw new UsuarioNaoExisteExeption();
        }
        usuario.setDataNascimento(usuarioDTO.getDataNascimento());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setUsername(usuarioDTO.getUsername());
        usuario.setSenha(usuarioDTO.getSenha());
        usuario = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.builder()
                .userId(usuario.getUserId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .dataNascimento(usuario.getDataNascimento())
                .build();
    }

    public UsuarioResponseDTO getUsuario(Long idUser){
        Usuario usuario = usuarioRepository.findById(idUser).orElseThrow(UsuarioNaoExisteExeption::new);
        return UsuarioResponseDTO.builder()
                .userId(usuario.getUserId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .dataNascimento(usuario.getDataNascimento())
                .build();
    }
}
