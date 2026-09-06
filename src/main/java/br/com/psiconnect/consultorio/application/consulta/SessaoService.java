package br.com.psiconnect.consultorio.application.consulta;

import org.springframework.transaction.annotation.Transactional;

import br.com.psiconnect.consultorio.application.port.SessaoRepository;

import br.com.psiconnect.consultorio.domain.consulta.Sessao;

import br.com.psiconnect.consultorio.application.consulta.agendamento.ValidadorAgendamentoConsulta;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosAgendamentoSessao;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosDetalhamentoSessao;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosListagemSessao;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosRelatorioConsultaMensal;
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
    private final List<ValidadorAgendamentoConsulta> validadoresAgendamento;

    public SessaoService(SessaoRepository sessaoRepository, PsicologoRepository psicologoRepository, PacienteRepository pacienteRepository, List<ValidadorAgendamentoConsulta> validadoresAgendamento) {
        this.sessaoRepository = sessaoRepository;
        this.psicologoRepository = psicologoRepository;
        this.pacienteRepository = pacienteRepository;
        this.validadoresAgendamento = validadoresAgendamento;
    }

    public DadosDetalhamentoSessao agendar(DadosAgendamentoSessao dados) {
        if (!pacienteRepository.existsById(dados.idPaciente())) {
            throw new ConsultorioException("Id do paciente informado não existe!");
        }

        if (dados.idPsicologo() != null && !psicologoRepository.existsById(dados.idPsicologo())) {
            throw new ConsultorioException("Id do psicólogo informado não existe!");
        }

        validadoresAgendamento.forEach(v -> v.validar(dados));

        var paciente = pacienteRepository.getReferenceById(dados.idPaciente());
        Psicologo psicologo = escolherPsicologo(dados);
        if (psicologo == null) {
            throw new ConsultorioException("Não existe psicólogo disponível nessa data!");
        }

        var sessao = new Sessao(dados.data(), paciente, psicologo);
        sessao.definirValorSessao(paciente.getValorSessao());

        if (paciente.getValorSessao().compareTo(BigDecimal.ZERO) == 0) {
            paciente.definirValorSessao(dados.valorSessao());
            pacienteRepository.save(paciente);
        }

        sessaoRepository.save(sessao);
        return new DadosDetalhamentoSessao(sessao);
    }

    @Transactional(readOnly = true)
    public Page<DadosListagemSessao> listarSessoes(Pageable paginacao) {
        return sessaoRepository.findAll(paginacao).map(DadosListagemSessao::new);
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

    private Psicologo escolherPsicologo(DadosAgendamentoSessao dados) {
        if (dados.idPsicologo() != null) {
            return psicologoRepository.getReferenceById(dados.idPsicologo());
        }
        return psicologoRepository.escolherPsicologoLivreNaData(dados.especialidade(), dados.data());
    }
}