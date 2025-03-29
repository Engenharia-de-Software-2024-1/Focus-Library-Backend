package com.focuslibrary.focus_library.service.auth;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.focuslibrary.focus_library.dto.AuthRegisterDTO;
import com.focuslibrary.focus_library.dto.UsuarioResponseDTO;
import com.focuslibrary.focus_library.exceptions.FocusLibraryException;
import com.focuslibrary.focus_library.model.Usuario;
import com.focuslibrary.focus_library.repository.UsuarioRepository;

@Service
public class AuthServiceImp implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return usuario;
    }

    public UsuarioResponseDTO registrar(AuthRegisterDTO authDTO) {
        if (usuarioRepository.findByUsername(authDTO.getUsername()) != null) {
            throw new FocusLibraryException("");
        }
        String criptografado = new BCryptPasswordEncoder().encode(authDTO.getSenha());
        Usuario usuario = modelMapper.map(authDTO, Usuario.class);
        usuario.setSenha(criptografado);
        usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }
}
