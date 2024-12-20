package br.com.psiconnect.consultorio.consulta;

import br.com.psiconnect.consultorio.consulta.agendamento.ValidadorAgendamentoConsulta;
import br.com.psiconnect.consultorio.consulta.dto.DadosAgendamentoSessao;
import br.com.psiconnect.consultorio.consulta.dto.DadosDetalhamentoSessao;
import br.com.psiconnect.consultorio.consulta.dto.DadosListagemSessao;
import br.com.psiconnect.consultorio.consulta.dto.DadosRelatorioConsultaMensal;
import br.com.psiconnect.consultorio.paciente.PacienteRepository;
import br.com.psiconnect.consultorio.psicologo.Psicologo;
import br.com.psiconnect.consultorio.psicologo.PsicologoRepository;
import br.com.psiconnect.infra.exception.ConsultorioException;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SessaoService {
    @Autowired
    private SessaoRepository sessaoRepository;
    @Autowired
    private PsicologoRepository psicologoRepository;
    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private List<ValidadorAgendamentoConsulta> validadoresAgendamento;

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
        // Obtemos a lista de sessões do repositório
        List<Sessao> sessoes = sessaoRepository.gerarRelatorioConsultaMensal(inicioMes, fimMes);

        // Agrupamos as sessões por psicólogo e paciente para calcular os dados necessários
        Map<String, Map<String, List<Sessao>>> relatorioAgrupado = sessoes.stream()
                .collect(Collectors.groupingBy(sessao -> sessao.getPsicologo().getNome(),
                        Collectors.groupingBy(sessao -> sessao.getPaciente().getNome())));

        List<DadosRelatorioConsultaMensal> relatorioMensal = new ArrayList<>();

        // Preenchemos a lista de DadosRelatorioConsultaMensal
        for (Map.Entry<String, Map<String, List<Sessao>>> entryPsicologo : relatorioAgrupado.entrySet()) {
            String nomePsicologo = entryPsicologo.getKey();

            for (Map.Entry<String, List<Sessao>> entryPaciente : entryPsicologo.getValue().entrySet()) {
                String nomePaciente = entryPaciente.getKey();
                List<Sessao> sessoesDoPaciente = entryPaciente.getValue(); // Correção aqui
                Long quantidadeConsultasNoMes = (long) sessoesDoPaciente.size();
                BigDecimal valorTotalConsultas = sessoesDoPaciente.stream()
                        .map(Sessao::getValorSessao)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Supondo que você tenha um método para obter o CRP do psicólogo
                String crp = sessoesDoPaciente.get(0).getPsicologo().getCrp(); // Exemplo de como obter o CRP

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
