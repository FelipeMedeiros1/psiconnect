package br.com.psiconnect.consultorio.paciente;

import br.com.psiconnect.consultorio.consulta.Sessao;
import br.com.psiconnect.consultorio.paciente.dto.*;
import br.com.psiconnect.infra.exception.ConsultorioException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PacienteService {
    private final PacienteRepository pacienteRepository;
    private final PacienteRepository sessaRepository;

    public PacienteService(PacienteRepository pacienteRepository, PacienteRepository sessapRepository) {
        this.pacienteRepository = pacienteRepository;
        this.sessaRepository = sessapRepository;
    }

    public DadosDetalhamentoPaciente cadastrar(DadosCadastroPaciente dados) {
        var jaCadastrado = pacienteRepository.existsByCpf(dados.cpf());
        if (jaCadastrado) {
            throw new ConsultorioException("Este CPF: " + dados.cpf() + ", já está cadastrado");
        }
        Paciente paciente = new Paciente(dados);
        pacienteRepository.save(paciente);
        return new DadosDetalhamentoPaciente(paciente);
    }

    public Page<DadosListagemPaciente> listar(Pageable paginacao) {
        return pacienteRepository.findAll(paginacao).map(DadosListagemPaciente::new);
    }

    public DadosDetalhamentoPaciente buscarPorId(Long id) {
        return new DadosDetalhamentoPaciente(pacienteRepository.findById(id)
                .orElseThrow(() -> new ConsultorioException("Paciente não encontrado")));
    }

    public List<DadosDetalhamentoPaciente> buscarPorNome(String nome) {
        return pacienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    public DadosDetalhamentoPaciente atualizar(DadosAtualizacaoPaciente dados) {
        var paciente = pacienteRepository.getReferenceById(dados.id());
        paciente.atualizarInformacoes(dados);
        return new DadosDetalhamentoPaciente(paciente);
    }

    public List<DadosRelatorioPacienteMensal> gerarRelatorioMensal(LocalDateTime inicioMes, LocalDateTime fimMes) {
        return pacienteRepository.findAll().stream()
                .map(paciente -> {
                    List<Sessao> sessoesDoPaciente = sessaRepository.findAllByPacienteIdAndDataBetween(paciente.getId(), inicioMes, fimMes);
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