package br.com.psiconnect.consultorio.consulta;

import br.com.psiconnect.consultorio.consulta.dto.DadosRelatorioConsultaMensal;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RelatorioConsultaMensalAssembler {

    public List<DadosRelatorioConsultaMensal> criar(List<Sessao> sessoes) {
        Map<String, Map<String, List<Sessao>>> relatorioAgrupado = agruparPorPsicologoEPaciente(sessoes);
        return relatorioAgrupado.entrySet().stream()
                .flatMap(psicologoEntry -> criarRelatorioPorPsicologo(psicologoEntry.getKey(), psicologoEntry.getValue()).stream())
                .collect(Collectors.toList());
    }

    private Map<String, Map<String, List<Sessao>>> agruparPorPsicologoEPaciente(List<Sessao> sessoes) {
        return sessoes.stream()
                .collect(Collectors.groupingBy(sessao -> sessao.getPsicologo().getNome(),
                        Collectors.groupingBy(sessao -> sessao.getPaciente().getNome())));
    }

    private List<DadosRelatorioConsultaMensal> criarRelatorioPorPsicologo(String nomePsicologo, Map<String, List<Sessao>> sessoesPorPaciente) {
        return sessoesPorPaciente.entrySet().stream()
                .map(pacienteEntry -> criarRelatorioLinha(nomePsicologo, pacienteEntry.getKey(), pacienteEntry.getValue()))
                .collect(Collectors.toList());
    }

    private DadosRelatorioConsultaMensal criarRelatorioLinha(String nomePsicologo, String nomePaciente, List<Sessao> sessoesDoPaciente) {
        Sessao primeiraSessao = sessoesDoPaciente.get(0);
        BigDecimal valorTotal = calcularValorTotal(sessoesDoPaciente);
        return new DadosRelatorioConsultaMensal(
                nomePsicologo,
                primeiraSessao.getPsicologo().getCrp(),
                (long) sessoesDoPaciente.size(),
                nomePaciente,
                valorTotal
        );
    }

    private BigDecimal calcularValorTotal(List<Sessao> sessoesDoPaciente) {
        return sessoesDoPaciente.stream()
                .map(Sessao::getValorSessao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
