package br.com.psiconnect.consultorio.consulta;

import br.com.psiconnect.consultorio.consulta.agendamento.ValidadorAgendamentoConsulta;
import br.com.psiconnect.consultorio.consulta.dto.DadosAgendamentoSessao;
import br.com.psiconnect.consultorio.consulta.dto.DadosAtualizacaoSessao;
import br.com.psiconnect.consultorio.consulta.dto.DadosCancelamentoSessao;
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
        validarPaciente(dados);
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

    public DadosDetalhamentoSessao buscarPorId(Long id) {
        return sessaoRepository.findById(id)
                .map(DadosDetalhamentoSessao::new)
                .orElseThrow(() -> new ConsultorioException("Sessão com id: " + id + ", não encontrada"));
    }

    public void atualizar(Long id, DadosAtualizacaoSessao dados) {
        Sessao sessao = sessaoRepository.findById(id)
                .orElseThrow(() -> new ConsultorioException("Sessão com id: " + id + ", não encontrada"));
        atualizarData(dados, sessao);
        atualizarPsicologo(dados, sessao);
        atualizarPaciente(dados, sessao);
        sessaoRepository.save(sessao);
    }

    public void deletar(Long id) {
        sessaoRepository.deleteById(id);
    }

    public void inativar(Long id, DadosCancelamentoSessao dados) {
        Sessao sessao = sessaoRepository.findById(id)
                .orElseThrow(() -> new ConsultorioException("Sessão com id: " + id + ", não encontrada"));
        boolean cobravel = Boolean.TRUE.equals(dados.cobravel());
        boolean reagendada = Boolean.TRUE.equals(dados.reagendada());
        validarCancelamento(cobravel, reagendada);
        StatusSessao status = StatusSessao.CANCELADA;
        if (cobravel) {
            status = StatusSessao.CANCELADA_COBRAVEL;
        }
        if (reagendada) {
            status = StatusSessao.CANCELADA_REAGENDADA;
        }
        sessao.cancelar(status);
        sessaoRepository.save(sessao);
    }
    public List<DadosRelatorioConsultaMensal> gerarRelatorioMensal(LocalDateTime inicioMes, LocalDateTime fimMes) {
        List<Sessao> sessoes = sessaoRepository.gerarRelatorioConsultaMensal(inicioMes, fimMes);
        return relatorioConsultaMensalAssembler.criar(sessoes);
    }

    public List<DadosRelatorioConsultaMensal> gerarRelatorioDetalhesMensal(LocalDateTime inicioMes, LocalDateTime fimMes) {
        return sessaoRepository.gerarRelatorioDetalhesMensal(inicioMes, fimMes);
    }

    private void atualizarData(DadosAtualizacaoSessao dados, Sessao sessao) {
        if (dados.data() != null) {
            sessao.setData(dados.data());
        }
    }

    private void atualizarPsicologo(DadosAtualizacaoSessao dados, Sessao sessao) {
        if (dados.idPsicologo() != null) {
            Psicologo psicologo = psicologoRepository.findById(dados.idPsicologo())
                    .orElseThrow(() -> new ConsultorioException("Psicólogo com id: " + dados.idPsicologo() + ", não encontrado"));
            sessao.setPsicologo(psicologo);
        }
    }

    private void atualizarPaciente(DadosAtualizacaoSessao dados, Sessao sessao) {
        if (dados.idPaciente() != null) {
            var paciente = pacienteRepository.findById(dados.idPaciente())
                    .orElseThrow(() -> new ConsultorioException("Paciente com id: " + dados.idPaciente() + ", não encontrado"));
            sessao.setPaciente(paciente);
        }
    }

    private void validarCancelamento(boolean cobravel, boolean reagendada) {
        if (cobravel && reagendada) {
            throw new ConsultorioException("Sessão não pode ser cobrada e reagendada ao mesmo tempo");
        }
    }

    private Psicologo escolherPsicologo(DadosAgendamentoSessao dados) {
        if (dados.idPsicologo() != null) {
            return psicologoRepository.getReferenceById(dados.idPsicologo());
        }
        return psicologoRepository.escolherPsicologoLivreNaData(dados.especialidade(), dados.data());
    }

    private void validarPaciente(DadosAgendamentoSessao dados) {
        if (!pacienteRepository.existsById(dados.idPaciente())) {
            throw new ConsultorioException("Id do paciente informado não existe!");
        }
    }

    private void validarPsicologo(DadosAgendamentoSessao dados) {
        if (dados.idPsicologo() != null && !psicologoRepository.existsById(dados.idPsicologo())) {
            throw new ConsultorioException("Id do psicólogo informado não existe!");
        }
    }
}
