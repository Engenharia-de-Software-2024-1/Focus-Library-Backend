package com.focuslibrary.focus_library.dto;

import lombok.Data;

@Data
public class SessaoPostPutRequestDTO {

    private Integer minutos;

    private String data;

    private Long idSessao;

}
