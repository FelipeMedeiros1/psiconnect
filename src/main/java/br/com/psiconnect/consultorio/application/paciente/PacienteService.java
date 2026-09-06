package br.com.psiconnect.consultorio.application.paciente;

import org.springframework.transaction.annotation.Transactional;
import static br.com.psiconnect.consultorio.application.mapper.DadosPessoaisMapper.*;

import br.com.psiconnect.consultorio.application.port.PacienteRepository;

import br.com.psiconnect.consultorio.domain.paciente.Paciente;

import br.com.psiconnect.consultorio.domain.consulta.Sessao;
import br.com.psiconnect.consultorio.application.port.SessaoRepository;
import br.com.psiconnect.consultorio.application.paciente.dto.*;
import br.com.psiconnect.consultorio.domain.exception.ConsultorioException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PacienteService {
    private final PacienteRepository pacienteRepository;
    private final SessaoRepository sessaoRepository;

    public PacienteService(PacienteRepository pacienteRepository, SessaoRepository sessaoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.sessaoRepository = sessaoRepository;
    }

    public DadosDetalhePaciente cadastrar(DadosCadastroPaciente dados) {
        var jaCadastrado = pacienteRepository.existsByCpf(dados.cpf());
        if (jaCadastrado) {
            throw new ConsultorioException("Este CPF: " + dados.cpf() + ", já está cadastrado");
        }
        Paciente paciente = new Paciente(dados.responsavel(), dados.nome(), dados.dataNascimento(), dados.profissao(), dados.cpf(), endereco(dados.endereco()), contato(dados.contato()));
        pacienteRepository.save(paciente);
        return new DadosDetalhePaciente(paciente);
    }

    @Transactional(readOnly = true)
    public Page<DadosListagemPaciente> listar(Pageable paginacao) {
        return pacienteRepository.findAll(paginacao).map(DadosListagemPaciente::new);
    }

    @Transactional(readOnly = true)
    public DadosDetalhePaciente buscarPorId(Long id) {
        return new DadosDetalhePaciente(pacienteRepository.findById(id)
                .orElseThrow(() -> new ConsultorioException("Paciente não encontrado")));
    }

    @Transactional(readOnly = true)
    public List<DadosDetalhePaciente> buscarPorNome(String nome) {
        return pacienteRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(DadosDetalhePaciente::new).toList();
    }

    public DadosDetalhePaciente atualizar(DadosAtualizacaoPaciente dados) {
        var paciente = pacienteRepository.getReferenceById(dados.id());
        paciente.atualizarInformacoes(dados.nome(), dados.valorConsulta(), contato(dados.contato()), endereco(dados.endereco()));
        return new DadosDetalhePaciente(paciente);
    }

    @Transactional(readOnly = true)
    public List<DadosRelatorioPacienteMensal> gerarRelatorioMensal(LocalDateTime inicioMes, LocalDateTime fimMes) {
        return pacienteRepository.findAll().stream()
                .map(paciente -> {
                    List<Sessao> sessoesDoPaciente = sessaoRepository.findAllByPaciente_IdAndDataBetween(paciente.getId(), inicioMes, fimMes);
                    return new DadosRelatorioPacienteMensal(
                            paciente.getNome(),
                            sessoesDoPaciente.size(),
                            inicioMes.toLocalDate(),
                            paciente.getValorSessao(),
                            sessoesDoPaciente.stream()
                                    .map(Sessao::getValorSessao)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    );
                })
                .collect(Collectors.toList());
    }

    public void deletar(Long id) {
        pacienteRepository.deleteById(id);
    }

    public void altaPaciente(DadosAtualizacaoPaciente dados) {
        Paciente paciente = pacienteRepository.getReferenceById(dados.id());
        paciente.altaPaciente(dados.motivoAlta());
        pacienteRepository.save(paciente);
    }
}