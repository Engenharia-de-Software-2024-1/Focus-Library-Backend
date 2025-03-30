package com.focuslibrary.focus_library.controller;

import com.focuslibrary.focus_library.dto.AtividadeDTO;
import com.focuslibrary.focus_library.service.sessao.AtividadeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("sessao")
public class AtividadeController {

    @Autowired
    private AtividadeService sessaoService;

    @PostMapping("")
    public ResponseEntity<AtividadeDTO> addAtividade(
            @RequestBody @Valid AtividadeDTO atividadeDTO
            ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessaoService.addAtividade(atividadeDTO));
    }

    @GetMapping("")
    public ResponseEntity<List<AtividadeDTO>> getAllAtividade() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(sessaoService.getUserAtividades());
    }
}
