package br.com.dbc.vemser.sistemaaluguelveiculos.service;

import br.com.dbc.vemser.sistemaaluguelveiculos.dto.LocacaoCreateDTO;
import br.com.dbc.vemser.sistemaaluguelveiculos.dto.LocacaoDTO;
import br.com.dbc.vemser.sistemaaluguelveiculos.entity.*;
import br.com.dbc.vemser.sistemaaluguelveiculos.exceptions.BancoDeDadosException;
import br.com.dbc.vemser.sistemaaluguelveiculos.exceptions.RegraDeNegocioException;
import br.com.dbc.vemser.sistemaaluguelveiculos.repository.LocacaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocacaoService {
    private final LocacaoRepository locacaoRepository;
    private final FuncionarioService funcionarioService;
    private final ClienteService clienteService;
    private final CartaoCreditoService cartaoCreditoService;
    private final VeiculoService veiculoService;

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    public LocacaoDTO create(LocacaoCreateDTO locacaoDTO) throws RegraDeNegocioException {
        try {
            Locacao locacaoAdicionada = locacaoRepository.create(converterEmLocacao(locacaoDTO));
            System.out.println("locação adicinado com sucesso! \n" + locacaoAdicionada);
            Funcionario funcionario = funcionarioService.findById(locacaoAdicionada.getFuncionario().getIdFuncionario());
            emailService.sendEmail(locacaoAdicionada, "locacao-template.ftl", funcionario.getEmail());
            return converterEmDTO(locacaoAdicionada);
        } catch (BancoDeDadosException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete(Integer id) throws RegraDeNegocioException {
        try {
            boolean conseguiuRemover = locacaoRepository.delete(id);
            System.out.println("removido? " + conseguiuRemover + "| com id=" + id);
        } catch (BancoDeDadosException e) {
            e.printStackTrace();
        }
        Locacao locacaoDeletada = findById(id);
        Funcionario funcionario = funcionarioService.findById(locacaoDeletada.getFuncionario().getIdFuncionario());
        emailService.sendEmail(locacaoDeletada, "locacao-template-delete.ftl", funcionario.getEmail());
    }

    public Locacao findById(Integer idLocacao) throws RegraDeNegocioException {
        try {
            Locacao locacaoRecuperada = locacaoRepository.list().stream()
                    .filter(locacao -> locacao.getFuncionario().getIdFuncionario().equals(idLocacao))
                    .findFirst()
                    .orElseThrow(() -> new RegraDeNegocioException("Locação não encontrada"));
            return locacaoRecuperada;
        } catch (BancoDeDadosException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LocacaoDTO update(Integer id, LocacaoCreateDTO locacao) throws RegraDeNegocioException {
        try {
            Funcionario funcionario = funcionarioService.findById(id);
            Locacao locacaoEntity = objectMapper.convertValue(locacao, Locacao.class);
            emailService.sendEmail(locacaoEntity, "locacao-template-update.ftl", funcionario.getEmail());
            return objectMapper.convertValue(locacaoRepository.update(id, converterEmLocacao(locacao)), LocacaoDTO.class);
        } catch (BancoDeDadosException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<LocacaoDTO> list() {
        try {
            List<Locacao> listar = locacaoRepository.list();
            return listar.stream()
                    .map(this::converterEmDTO)
                    .collect(Collectors.toList());
        } catch (BancoDeDadosException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Locacao converterEmLocacao(LocacaoCreateDTO locacaoCreateDTO) {
        Funcionario funcionario = null;
        //buscando na base para montar o objeto locacao, se existe funcionario, cliente, veiculo e cartao de credito monta o objeto locacao e retorna para ser salvo na base
        try {
            funcionario = funcionarioService.findById(locacaoCreateDTO.getIdFuncionario());
            Cliente cliente = clienteService.findById(locacaoCreateDTO.getIdCliente());
            Veiculo veiculo = veiculoService.findById(locacaoCreateDTO.getIdveiculo());
            CartaoCredito cartaoCredito = cartaoCreditoService.findById(locacaoCreateDTO.getIdCartaoCredito());
            return new Locacao(null, locacaoCreateDTO.getDataLocacao(), locacaoCreateDTO.getDataDevolucao(), locacaoCreateDTO.getValorLocacao(), cliente, veiculo, cartaoCredito, funcionario);
        } catch (RegraDeNegocioException | BancoDeDadosException e) {
            throw new RuntimeException(e);
        }
    }

    public LocacaoDTO converterEmDTO(Locacao locacao) {
        return objectMapper.convertValue(locacao, LocacaoDTO.class);
    }
}