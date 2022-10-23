package br.com.dbc.vemser.sistemaaluguelveiculos.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Locacao {
    private Integer idLocacao;
    private LocalDate dataLocacao;
    private LocalDate dataDevolucao;
    private Double valorLocacao;
    private Cliente cliente;
    private Veiculo veiculo;
    private CartaoCredito cartaoCredito;
    private Funcionario funcionario;
}
