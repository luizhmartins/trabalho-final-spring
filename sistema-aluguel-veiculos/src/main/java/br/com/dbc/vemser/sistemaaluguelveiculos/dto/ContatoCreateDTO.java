package br.com.dbc.vemser.sistemaaluguelveiculos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContatoCreateDTO {

    private String telefone;
    private String email;
}