package com.focuslibrary.focus_library.service.usuario;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.focuslibrary.focus_library.dto.TrocaDadosUserDTO;
import com.focuslibrary.focus_library.model.Atividade;
import com.focuslibrary.focus_library.repository.AtividadeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.focuslibrary.focus_library.config.security.TokenService;
import com.focuslibrary.focus_library.dto.UsuarioPostPutRequestDTO;
import com.focuslibrary.focus_library.dto.UsuarioResponseDTO;
import com.focuslibrary.focus_library.exceptions.FocusLibraryException;
import com.focuslibrary.focus_library.exceptions.UsuarioNaoExisteException;
import com.focuslibrary.focus_library.model.Sessao;
import com.focuslibrary.focus_library.model.Usuario;
import com.focuslibrary.focus_library.repository.SessaoRepository;
import com.focuslibrary.focus_library.repository.UsuarioRepository;

@Service
public class UsuarioServiceImp implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

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
                .map(usuario -> modelMapper.map(usuario, UsuarioResponseDTO.class))
                .collect(Collectors.toList());
    }

    public void deleteUsuario(String idUser) {
        Usuario usuario = validateAuthenticatedUser(idUser);
        usuarioRepository.delete(usuario);
    }

    public UsuarioResponseDTO editarUsuario(String idUser, UsuarioPostPutRequestDTO usuarioDTO) {
        Usuario usuario = validateAuthenticatedUser(idUser);
        
        modelMapper.map(usuarioDTO, usuario);
        if (usuarioDTO.getSenha() != null) {
            usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        }
        usuario = usuarioRepository.save(usuario);
        
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    public UsuarioResponseDTO editarDadosGeraisUsuario(String idUser, TrocaDadosUserDTO userDTO){
        Usuario usuario = validateAuthenticatedUser(idUser);

        usuario.setDataNascimento(userDTO.getDataNascimento());
        usuario.setEmail(userDTO.getEmail());
        usuario.setUsername(userDTO.getUsername());

        usuarioRepository.save(usuario);

        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    public UsuarioResponseDTO getUsuario(String idUser) {
        Usuario usuario = validateAuthenticatedUser(idUser);
        UsuarioResponseDTO responseDTO = modelMapper.map(usuario, UsuarioResponseDTO.class);
        responseDTO.setStreak(getStreak(usuario));
        return responseDTO;
    }

    public UsuarioResponseDTO getUsuarioByToken(){
        String username = TokenService.getUsernameUsuarioLogado();
        if (username == null) {
            throw new FocusLibraryException("Token inválido");
        }
        Usuario usuario = usuarioRepository.findByUsername(username);
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    public List<UsuarioResponseDTO> getRanking() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> {
                    UsuarioResponseDTO responseDTO = modelMapper.map(usuario, UsuarioResponseDTO.class);
                    responseDTO.setStreak(getStreak(usuario));
                    return responseDTO;
                })
                .sorted(Comparator.comparing(UsuarioResponseDTO::getStreak).reversed())
                .collect(Collectors.toList());
    }

    private Long getStreak(Usuario usuario) {
        List<Atividade> atividades = atividadeRepository.findByUsuario(usuario);
        if (atividades.isEmpty()) {
            return 0L;
        }

        // Sort sessions by date
        atividades.sort(Comparator.comparing(Atividade::getData));

        List<LocalDate> uniqueDates = atividades.stream()
                .map(Atividade::getData)
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

            if (ChronoUnit.DAYS.between(date, previousDate) == 1) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }
}