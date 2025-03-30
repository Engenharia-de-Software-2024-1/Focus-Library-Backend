package com.focuslibrary.focus_library.service.sessao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.focuslibrary.focus_library.config.security.TokenService;
import com.focuslibrary.focus_library.dto.AtividadeDTO;
import com.focuslibrary.focus_library.dto.SessaoDTO;
import com.focuslibrary.focus_library.exceptions.FocusLibraryException;
import com.focuslibrary.focus_library.model.Atividade;
import com.focuslibrary.focus_library.model.AtividadeId;
import com.focuslibrary.focus_library.model.Sessao;
import com.focuslibrary.focus_library.model.Usuario;
import com.focuslibrary.focus_library.repository.AtividadeRepository;
import com.focuslibrary.focus_library.repository.UsuarioRepository;

import jakarta.transaction.Transactional;


@Service
public class AtividadeServiceImp implements AtividadeService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AtividadeRepository atividadeRepository;

    private Usuario validateToken(){
        String username = TokenService.getUsernameUsuarioLogado();
        return usuarioRepository.findByUsername(username);
    }

    @Transactional
    @Override
    public AtividadeDTO addAtividade(AtividadeDTO atividadeDTO) {
        Usuario usuario = validateToken();

        AtividadeId atividadeId = new AtividadeId();
        atividadeId.setAtividadeId(atividadeDTO.getAtividadeId());
        atividadeId.setUsuarioId(usuario.getUserId());

        if (atividadeRepository.existsById(atividadeId))
            throw new FocusLibraryException("Id Atividade Incvalido");

        Atividade atividade = new Atividade();
        atividade.setUsuario(usuario);
        atividade.setData(atividadeDTO.getData());
        atividade.setAtividadeId(atividadeId);

        if (atividadeDTO.getSessoes() != null) {
            List<Sessao> sessoes = atividadeDTO.getSessoes().stream()
                    .map(sessaoDTO -> {
                        Sessao sessao = new Sessao();
                        sessao.setSegundosDescanso(sessaoDTO.getSegundosDescanso());
                        sessao.setSegundosFoco(sessaoDTO.getSegundosFoco());
                        sessao.setAtividade(atividade);
                        return sessao;
                    })
                    .collect(Collectors.toList());
            atividade.setSessoes(sessoes);
        }

        usuario.addAtividade(atividade);
        usuarioRepository.save(usuario);
        return atividadeDTO;
    }

    @Override
    public List<AtividadeDTO> getUserAtividades() {
        Usuario usuario = validateToken();
        List<Atividade> atividades = usuario.getAtividades();
        List<AtividadeDTO> response = new ArrayList<>();
        for (Atividade atividade: atividades){
            List<SessaoDTO> sessoesDTO = atividade.getSessoes().stream()
                    .map(sessao -> new SessaoDTO(sessao.getSegundosDescanso(), sessao.getSegundosFoco()))
                    .collect(Collectors.toList());

            AtividadeDTO atividadeDTO = AtividadeDTO.builder()
                    .atividadeId(atividade.getAtividadeId().getAtividadeId())
                    .data(atividade.getData())
                    .sessoes(sessoesDTO).build();

            response.add(atividadeDTO);
        }
        return response;
    }
}
