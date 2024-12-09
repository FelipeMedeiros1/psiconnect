package br.com.psiconnect.consultorio.paciente;

import br.com.psiconnect.consultorio.consulta.Sessao;
import br.com.psiconnect.consultorio.consulta.SessaoRepository;
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
    private final SessaoRepository sessaRepository;

    public PacienteService(PacienteRepository pacienteRepository, SessaoRepository sessaoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.sessaRepository = sessaoRepository;
    }

    public DadosDetalhePaciente cadastrar(DadosCadastroPaciente dados) {
        var jaCadastrado = pacienteRepository.existsByCpf(dados.cpf());
        if (jaCadastrado) {
            throw new ConsultorioException("Este CPF: " + dados.cpf() + ", já está cadastrado");
        }
        Paciente paciente = new Paciente(dados);
        pacienteRepository.save(paciente);
        return new DadosDetalhePaciente(paciente);
    }

    public Page<DadosListagemPaciente> listar(Pageable paginacao) {
        return pacienteRepository.findAll(paginacao).map(DadosListagemPaciente::new);
    }

    public DadosDetalhePaciente buscarPorId(Long id) {
        return new DadosDetalhePaciente(pacienteRepository.findById(id)
                .orElseThrow(() -> new ConsultorioException("Paciente não encontrado")));
    }

    public List<DadosDetalhePaciente> buscarPorNome(String nome) {
        return pacienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    public DadosDetalhePaciente atualizar(DadosAtualizacaoPaciente dados) {
        var paciente = pacienteRepository.getReferenceById(dados.id());
        paciente.atualizarInformacoes(dados);
        return new DadosDetalhePaciente(paciente);
    }

    public List<DadosRelatorioPacienteMensal> gerarRelatorioMensal(LocalDateTime inicioMes, LocalDateTime fimMes) {
        return pacienteRepository.findAll().stream()
                .map(paciente -> {
                    // Aqui usamos o método ajustado do SessaoRepository
                    List<Sessao> sessoesDoPaciente = sessaRepository.findAllByPaciente_IdAndDataBetween(paciente.getId(), inicioMes, fimMes);
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