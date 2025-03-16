package com.focuslibrary.focus_library.controller;

import com.focuslibrary.focus_library.dto.SessaoPostPutRequestDTO;
import com.focuslibrary.focus_library.service.sessao.SessaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("sessao")
public class SessaoController {

    @Autowired
    private SessaoService sessaoService;

    @PostMapping("")
    public ResponseEntity<?> addSessao(
            @RequestBody @Valid SessaoPostPutRequestDTO sessaoDTO
            ) {
        System.out.println("addSessao");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sessaoService.addSessao(sessaoDTO));
    }

    @PostMapping("/addlist")
    public ResponseEntity<?> addSessaoList(
            @RequestBody List<@Valid SessaoPostPutRequestDTO> sessaoDTO
    ){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sessaoService.addSessao(sessaoDTO));
    }

    @GetMapping("")
    public ResponseEntity<?> getAllSessao() {
        return ResponseEntity.status(HttpStatus.OK).body(sessaoService.getUserSessao());
    }
}
