package br.com.psiconnect.consultorio.application.consulta;

import org.springframework.transaction.annotation.Transactional;

import br.com.psiconnect.consultorio.application.port.SessaoRepository;

import br.com.psiconnect.consultorio.domain.consulta.Sessao;

import br.com.psiconnect.consultorio.domain.consulta.AgendamentoSessao;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosAgendamentoSessao;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosDetalhamentoSessao;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosListagemSessao;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosRelatorioConsultaMensal;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosAtualizacaoSessao;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosHistoricoAtendimento;
import br.com.psiconnect.consultorio.application.port.PacienteRepository;
import br.com.psiconnect.consultorio.domain.psicologo.Psicologo;
import br.com.psiconnect.consultorio.application.port.PsicologoRepository;
import br.com.psiconnect.consultorio.domain.exception.ConsultorioException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class SessaoService {
    private final SessaoRepository sessaoRepository;
    private final PsicologoRepository psicologoRepository;
    private final PacienteRepository pacienteRepository;
    private final AgendamentoSessao agendamento;

    public SessaoService(SessaoRepository sessaoRepository, PsicologoRepository psicologoRepository, PacienteRepository pacienteRepository, AgendamentoSessao agendamento) {
        this.sessaoRepository = sessaoRepository;
        this.psicologoRepository = psicologoRepository;
        this.pacienteRepository = pacienteRepository;
        this.agendamento = agendamento;
    }

    public DadosDetalhamentoSessao agendar(DadosAgendamentoSessao dados) {
        var paciente = pacienteRepository.findById(dados.idPaciente())
                .orElseThrow(() -> new ConsultorioException("Id do paciente informado não existe!"));
        Psicologo psicologo = dados.idPsicologo() == null ? null : psicologoRepository.findById(dados.idPsicologo())
                .orElseThrow(() -> new ConsultorioException("Id do psicólogo informado não existe!"));

        var sessao = agendamento.agendar(paciente, psicologo, dados.especialidade(), dados.data(), dados.valorSessao());
        pacienteRepository.save(paciente);
        return new DadosDetalhamentoSessao(sessaoRepository.save(sessao));
    }

    @Transactional(readOnly = true)
    public Page<DadosListagemSessao> listarSessoes(Pageable paginacao) {
        return sessaoRepository.findAll(paginacao).map(DadosListagemSessao::new);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoSessao buscarPorId(Long id) {
        return new DadosDetalhamentoSessao(sessaoRepository.findById(id)
                .orElseThrow(() -> new ConsultorioException("Sessão não encontrada!")));
    }

    public DadosDetalhamentoSessao atualizar(DadosAtualizacaoSessao dados) {
        var sessao = sessaoRepository.findById(dados.id())
                .orElseThrow(() -> new ConsultorioException("Sessão não encontrada!"));
        var paciente = pacienteRepository.findById(dados.idPaciente())
                .orElseThrow(() -> new ConsultorioException("Paciente não encontrado!"));
        var psicologo = psicologoRepository.findById(dados.idPsicologo())
                .orElseThrow(() -> new ConsultorioException("Psicólogo não encontrado!"));
        paciente.validarAgendamento();
        psicologo.validarAgendamento();

        if (dados.data().toLocalDate().isBefore(LocalDateTime.now().toLocalDate()))
            throw new ConsultorioException("A data da consulta não pode ser anterior à data atual!");
        if (!dados.data().isAfter(LocalDateTime.now()))
            throw new ConsultorioException("O horário da consulta não pode ser anterior à hora atual!");
        if (dados.data().toLocalTime().isBefore(java.time.LocalTime.of(7, 0))
                || dados.data().toLocalTime().isAfter(java.time.LocalTime.of(22, 0)))
            throw new ConsultorioException("O horário da consulta deve estar entre 07:00 e 22:00!");

        if (sessaoRepository.existsByPacienteIdAndDataAfterAndDataBeforeAndIdNot(
                dados.idPaciente(), dados.data().minusMinutes(50), dados.data().plusMinutes(50), dados.id()))
                throw new ConsultorioException("Paciente já possui uma consulta em um intervalo menor que 50 minutos!");
        if (sessaoRepository.existsByPsicologoIdAndDataAndIdNot(dados.idPsicologo(), dados.data(), dados.id()))
                throw new ConsultorioException("Psicólogo já possui consulta nesse horário!");
        if (dados.valorSessao().signum() <= 0) throw new ConsultorioException("Valor deve ser maior que zero!");

        sessao.reagendar(dados.data(), paciente, psicologo, dados.valorSessao());
        return new DadosDetalhamentoSessao(sessaoRepository.save(sessao));
    }

    public void deletar(Long id) {
        if (!sessaoRepository.existsById(id)) throw new ConsultorioException("Sessão não encontrada!");
        sessaoRepository.deleteById(id);
    }

    public void registrarAtendimento(Long id, String informacoes) {
        var sessao = sessaoRepository.findById(id)
                .orElseThrow(() -> new ConsultorioException("Sessão não encontrada!"));
        if (sessao.isCompareceu()) throw new ConsultorioException("Atendimento já foi confirmado!");
        sessao.marcarPresenca();
        sessao.registrarEvolucao(informacoes);
        pacienteRepository.save(sessao.getPaciente());
        sessaoRepository.save(sessao);
    }

    public void editarEvolucao(Long id, String informacoes) {
        var sessao = sessaoRepository.findById(id)
                .orElseThrow(() -> new ConsultorioException("Sessão não encontrada!"));
        if (!sessao.isCompareceu()) throw new ConsultorioException("O atendimento ainda não foi concluído!");
        if (LocalDateTime.now().isAfter(sessao.getData().plusDays(7)))
            throw new ConsultorioException("O prazo de sete dias para editar a evolução terminou!");
        sessao.editarEvolucao(informacoes);
        pacienteRepository.save(sessao.getPaciente());
        sessaoRepository.save(sessao);
    }

    @Transactional(readOnly = true)
    public Page<DadosHistoricoAtendimento> listarHistorico(Pageable paginacao) {
        return sessaoRepository.findAllByCompareceuTrue(paginacao).map(DadosHistoricoAtendimento::new);
    }
    @Transactional(readOnly = true)
    public List<DadosRelatorioConsultaMensal> gerarRelatorioMensal(LocalDateTime inicioMes, LocalDateTime fimMes) {

        List<Sessao> sessoes = sessaoRepository.gerarRelatorioConsultaMensal(inicioMes, fimMes);

        Map<String, Map<String, List<Sessao>>> relatorioAgrupado = sessoes.stream()
                .collect(Collectors.groupingBy(sessao -> sessao.getPsicologo().getNome(),
                        Collectors.groupingBy(sessao -> sessao.getPaciente().getNome())));

        List<DadosRelatorioConsultaMensal> relatorioMensal = new ArrayList<>();

        for (Map.Entry<String, Map<String, List<Sessao>>> entryPsicologo : relatorioAgrupado.entrySet()) {
            String nomePsicologo = entryPsicologo.getKey();

            for (Map.Entry<String, List<Sessao>> entryPaciente : entryPsicologo.getValue().entrySet()) {
                String nomePaciente = entryPaciente.getKey();
                List<Sessao> sessoesDoPaciente = entryPaciente.getValue();
                Long quantidadeConsultasNoMes = (long) sessoesDoPaciente.size();
                BigDecimal valorTotalConsultas = sessoesDoPaciente.stream()
                        .map(Sessao::getValorSessao)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                String crp = sessoesDoPaciente.get(0).getPsicologo().getCrp();

                relatorioMensal.add(new DadosRelatorioConsultaMensal(
                        nomePsicologo,
                        crp,
                        quantidadeConsultasNoMes,
                        nomePaciente,
                        valorTotalConsultas
                ));
            }
        }

        return relatorioMensal;
    }

    @Transactional(readOnly = true)
    public List<DadosRelatorioConsultaMensal> gerarRelatorioDetalhesMensal(LocalDateTime inicioMes, LocalDateTime fimMes) {
        return sessaoRepository.gerarRelatorioDetalhesMensal(inicioMes, fimMes);
    }

}
