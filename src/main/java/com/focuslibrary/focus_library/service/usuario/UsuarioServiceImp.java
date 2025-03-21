package com.focuslibrary.focus_library.service.usuario;

import com.focuslibrary.focus_library.config.security.TokenService;
import com.focuslibrary.focus_library.dto.UsuarioPostPutRequestDTO;
import com.focuslibrary.focus_library.dto.UsuarioResponseDTO;
import com.focuslibrary.focus_library.exceptions.FocusLibraryException;
import com.focuslibrary.focus_library.exceptions.UsuarioNaoExisteException;
import com.focuslibrary.focus_library.model.Usuario;
import com.focuslibrary.focus_library.model.Sessao;
import com.focuslibrary.focus_library.repository.UsuarioRepository;
import com.focuslibrary.focus_library.repository.SessaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImp implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private Usuario validateAuthenticatedUser(String idUser) {
        String username = TokenService.getUsernameUsuarioLogado();
        if (username == null) {
            throw new FocusLibraryException("Token inválido");
        }
        
        Usuario usuario = usuarioRepository.findById(idUser).orElseThrow(UsuarioNaoExisteException::new);
        if (!usuario.getUsername().equals(username)) {
            throw new FocusLibraryException("Não autorizado");
        }
        return usuario;
    }

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
                .senha(passwordEncoder.encode(usuarioDTO.getSenha()))
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

    public void deleteUsuario(String idUser) {
        Usuario usuario = validateAuthenticatedUser(idUser);
        usuarioRepository.delete(usuario);
    }

    public UsuarioResponseDTO editarUsuario(String idUser, UsuarioPostPutRequestDTO usuarioDTO) {
        Usuario usuario = validateAuthenticatedUser(idUser);
        
        usuario.setDataNascimento(usuarioDTO.getDataNascimento());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setUsername(usuarioDTO.getUsername());
        if (usuarioDTO.getSenha() != null) {
            usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        }
        usuario = usuarioRepository.save(usuario);
        
        return UsuarioResponseDTO.builder()
                .userId(usuario.getUserId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .dataNascimento(usuario.getDataNascimento())
                .build();
    }

    public UsuarioResponseDTO getUsuario(String idUser) {
        Usuario usuario = validateAuthenticatedUser(idUser);
        return UsuarioResponseDTO.builder()
                .userId(usuario.getUserId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .dataNascimento(usuario.getDataNascimento())
                .streak(getStreak(usuario))
                .build();
    }

    public List<UsuarioResponseDTO> getRanking() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> UsuarioResponseDTO.builder()
                        .userId(usuario.getUserId())
                        .username(usuario.getUsername())
                        .streak(getStreak(usuario))
                        .build())
                .sorted(Comparator.comparing(UsuarioResponseDTO::getStreak).reversed())
                .collect(Collectors.toList());
    }
    private Long getStreak(Usuario usuario) {
        List<Sessao> sessoes = sessaoRepository.findByUsuario(usuario);
        if (sessoes.isEmpty()) {
            return 0L;
        }

        // Sort sessions by date
        sessoes.sort(Comparator.comparing(Sessao::getData));
        
        List<LocalDate> uniqueDates = sessoes.stream()
                .map(Sessao::getData)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        LocalDate dataAtual = LocalDate.now();
        LocalDate dataUltimaSessao = uniqueDates.get(uniqueDates.size() - 1);

        // Check if streak is broken (more than 1 day since last session)
        if (ChronoUnit.DAYS.between(dataUltimaSessao, dataAtual) > 1) {
            return 0L;
        }

        // Count consecutive days backwards from the last session
        long streak = 1;
        for (int i = uniqueDates.size() - 2; i >= 0; i--) {
            LocalDate date = uniqueDates.get(i);
            LocalDate previousDate = uniqueDates.get(i + 1);
            
            if (ChronoUnit.DAYS.between(previousDate, date) == 1) {
                streak++;
            } else {
                break;
            }
        }
        
        return streak;
    }
}