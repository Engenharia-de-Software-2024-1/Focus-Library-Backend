package com.focuslibrary.focus_library.service.sessao;

import com.focuslibrary.focus_library.config.security.TokenService;
import com.focuslibrary.focus_library.dto.SessaoPostPutRequestDTO;
import com.focuslibrary.focus_library.dto.SessaoResponseDTO;
import com.focuslibrary.focus_library.exeptions.FocusLibraryExeption;
import com.focuslibrary.focus_library.model.Sessao;
import com.focuslibrary.focus_library.model.SessaoId;
import com.focuslibrary.focus_library.model.Usuario;
import com.focuslibrary.focus_library.repository.SessaoRepository;
import com.focuslibrary.focus_library.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessaoServiceImp implements SessaoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SessaoRepository sessaoRepository;

    private Usuario validateToken(){
        String username = TokenService.getUsernameUsuarioLogado();
        if (username == null) {throw new FocusLibraryExeption("Token invalido");}
        return usuarioRepository.findByUsername(username);
    }

    private SessaoId validateSessao(Long sessaoDTOId, String userId){
        SessaoId sessaoId = new SessaoId(sessaoDTOId, userId);
        if (sessaoRepository.findById(sessaoId).isPresent())
            throw new FocusLibraryExeption("Sessao Ja Cadastrada");
        return sessaoId;
    }

    private LocalDate formatDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(date, formatter);
    }

    private Sessao buildSessao(Integer minutos, Usuario usuario, SessaoId sessaoId, LocalDate data){
        return Sessao.builder()
                .sessaoId(sessaoId)
                .data(data)
                .usuario(usuario)
                .minutos(minutos)
                .build();
    }

    @Override
    public SessaoResponseDTO addSessao(SessaoPostPutRequestDTO sessaoDTO) {

        Usuario usuario = validateToken();

        SessaoId sessaoId = validateSessao(sessaoDTO.getIdSessao(), usuario.getUserId());

        LocalDate data = formatDate(sessaoDTO.getData());

        Sessao sessao = buildSessao(sessaoDTO.getMinutos(), usuario, sessaoId, data);

        sessaoRepository.save(sessao);
        usuario.addSessao(sessao);
        usuarioRepository.save(usuario);

        return SessaoResponseDTO.builder()
                .sessaoId(sessaoId)
                .data(data)
                .minutos(sessaoDTO.getMinutos())
                .build();
    }

    @Override
    public List<SessaoResponseDTO> addSessao(List<SessaoPostPutRequestDTO> sessaoDTO) {
        List<Sessao> sessoes = new ArrayList<>();
        for (SessaoPostPutRequestDTO s : sessaoDTO) {
            try {
                Usuario usuario = validateToken();
                SessaoId sessaoId = validateSessao(s.getIdSessao(), usuario.getUserId());
                LocalDate data = formatDate(s.getData());
                sessoes.add(buildSessao(s.getMinutos(), usuario, sessaoId, data));
            } catch (Exception e) {
                continue;
            }
        }
        sessaoRepository.saveAll(sessoes);
        return sessoes.stream()
                .map(sessao -> new SessaoResponseDTO(sessao.getSessaoId(), sessao.getData(), sessao.getMinutos()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SessaoResponseDTO> getUserSessao() {
        Usuario usuario = validateToken();
        List<Sessao> sessoes = sessaoRepository.findByUsuario(usuario);
        return sessoes.stream()
                .map(sessao -> new SessaoResponseDTO(sessao.getSessaoId(), sessao.getData(), sessao.getMinutos()))
                .collect(Collectors.toList());
    }

}
