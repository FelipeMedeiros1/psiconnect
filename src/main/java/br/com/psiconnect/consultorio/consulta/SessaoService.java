package br.com.psiconnect.consultorio.consulta;

import br.com.psiconnect.consultorio.consulta.agendamento.ValidadorAgendamentoConsulta;
import br.com.psiconnect.consultorio.consulta.dto.DadosAtualizacaoValorSessao;
import br.com.psiconnect.consultorio.consulta.dto.DadosAgendamentoSessao;
import br.com.psiconnect.consultorio.consulta.dto.DadosDetalhamentoSessao;
import br.com.psiconnect.consultorio.consulta.dto.DadosListagemSessao;
import br.com.psiconnect.consultorio.consulta.dto.DadosRelatorioConsultaMensal;
import br.com.psiconnect.consultorio.paciente.PacienteRepository;
import br.com.psiconnect.consultorio.psicologo.Psicologo;
import br.com.psiconnect.consultorio.psicologo.PsicologoRepository;
import br.com.psiconnect.infra.exception.ConsultorioException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SessaoService {
    private final SessaoRepository sessaoRepository;
    private final PsicologoRepository psicologoRepository;
    private final PacienteRepository pacienteRepository;
    private final List<ValidadorAgendamentoConsulta> validadoresAgendamento;
    private final RelatorioConsultaMensalAssembler relatorioConsultaMensalAssembler;

    public SessaoService(SessaoRepository sessaoRepository,
                         PsicologoRepository psicologoRepository,
                         PacienteRepository pacienteRepository,
                         List<ValidadorAgendamentoConsulta> validadoresAgendamento,
                         RelatorioConsultaMensalAssembler relatorioConsultaMensalAssembler) {
        this.sessaoRepository = sessaoRepository;
        this.psicologoRepository = psicologoRepository;
        this.pacienteRepository = pacienteRepository;
        this.validadoresAgendamento = validadoresAgendamento;
        this.relatorioConsultaMensalAssembler = relatorioConsultaMensalAssembler;
    }

    public DadosDetalhamentoSessao agendar(DadosAgendamentoSessao dados) {
        validarPaciente(dados.idPaciente());
        validarPsicologo(dados);

        validadoresAgendamento.forEach(v -> v.validar(dados));

        var paciente = pacienteRepository.getReferenceById(dados.idPaciente());
        Psicologo psicologo = escolherPsicologo(dados);
        if (psicologo == null) {
            throw new ConsultorioException("Não existe psicólogo disponível nessa data!");
        }

        var sessao = new Sessao(dados.data(), paciente, psicologo);
        sessao.setValorSessao(paciente.getValorSessao());

        if (paciente.getValorSessao().compareTo(BigDecimal.ZERO) == 0) {
            paciente.setValorSessao(dados.valorSessao());
            pacienteRepository.save(paciente);
        }

        sessaoRepository.save(sessao);
        return new DadosDetalhamentoSessao(sessao);
    }

    public Page<DadosListagemSessao> listarSessoes(Pageable paginacao) {
        return sessaoRepository.findAll(paginacao).map(DadosListagemSessao::new);
    }
    public List<DadosRelatorioConsultaMensal> gerarRelatorioMensal(LocalDateTime inicioMes, LocalDateTime fimMes) {
        List<Sessao> sessoes = sessaoRepository.gerarRelatorioConsultaMensal(inicioMes, fimMes);
        return relatorioConsultaMensalAssembler.criar(sessoes);
    }

    public List<DadosRelatorioConsultaMensal> gerarRelatorioDetalhesMensal(LocalDateTime inicioMes, LocalDateTime fimMes) {
        return sessaoRepository.gerarRelatorioDetalhesMensal(inicioMes, fimMes);
    }

    public void atualizarValoresSessao(DadosAtualizacaoValorSessao dados) {
        validarPaciente(dados.idPaciente());
        validarValorSessao(dados.valorSessao());
        validarPeriodo(dados.inicioMes(), dados.fimMes());

        var paciente = pacienteRepository.getReferenceById(dados.idPaciente());
        paciente.atualizarValorSessao(dados.valorSessao());

        var inicioMes = dados.inicioMes().atStartOfDay();
        var fimMes = dados.fimMes().atTime(LocalTime.MAX);

        sessaoRepository.findAllByPaciente_IdAndDataBetween(dados.idPaciente(), inicioMes, fimMes)
                .forEach(sessao -> sessao.atualizarValorSessao(dados.valorSessao()));
    }

    private Psicologo escolherPsicologo(DadosAgendamentoSessao dados) {
        if (dados.idPsicologo() != null) {
            return psicologoRepository.getReferenceById(dados.idPsicologo());
        }
        return psicologoRepository.escolherPsicologoLivreNaData(dados.especialidade(), dados.data());
    }

    private void validarPaciente(Long idPaciente) {
        if (!pacienteRepository.existsById(idPaciente)) {
            throw new ConsultorioException("Id do paciente informado não existe!");
        }
    }

    private void validarPsicologo(DadosAgendamentoSessao dados) {
        if (dados.idPsicologo() != null && !psicologoRepository.existsById(dados.idPsicologo())) {
            throw new ConsultorioException("Id do psicólogo informado não existe!");
        }
    }

    private void validarValorSessao(BigDecimal valorSessao) {
        if (valorSessao == null) {
            throw new ConsultorioException("Valor da sessão é obrigatório!");
        }
        if (valorSessao.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ConsultorioException("Valor da sessão deve ser maior que zero!");
        }
    }

    private void validarPeriodo(LocalDate inicioMes, LocalDate fimMes) {
        if (inicioMes == null || fimMes == null) {
            throw new ConsultorioException("Período para atualização é obrigatório!");
        }
        if (inicioMes.isAfter(fimMes)) {
            throw new ConsultorioException("Data inicial não pode ser maior que a data final!");
        }
    }
}
