package br.com.psiconnect.consultorio.financeiro;

import br.com.psiconnect.consultorio.consulta.Sessao;
import br.com.psiconnect.consultorio.consulta.SessaoRepository;
import br.com.psiconnect.consultorio.financeiro.dto.DadosCadastroDespesa;
import br.com.psiconnect.consultorio.financeiro.dto.DadosDetalheDespesa;
import br.com.psiconnect.consultorio.financeiro.dto.DadosResumoFinanceiro;
import br.com.psiconnect.infra.exception.ConsultorioException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
public class FinanceiroService {

    private final SessaoRepository sessaoRepository;
    private final DespesaRepository despesaRepository;

    public FinanceiroService(SessaoRepository sessaoRepository, DespesaRepository despesaRepository) {
        this.sessaoRepository = sessaoRepository;
        this.despesaRepository = despesaRepository;
    }

    public DadosDetalheDespesa cadastrarDespesa(DadosCadastroDespesa dados) {
        Despesa despesa = new Despesa(dados);
        despesaRepository.save(despesa);
        return new DadosDetalheDespesa(despesa);
    }

    public List<DadosDetalheDespesa> listarDespesas(LocalDate inicioMes, LocalDate fimMes) {
        validarPeriodo(inicioMes, fimMes);
        return despesaRepository.findAllByDataBetween(inicioMes, fimMes).stream()
                .map(DadosDetalheDespesa::new)
                .toList();
    }

    public DadosResumoFinanceiro gerarResumo(LocalDate inicioMes, LocalDate fimMes) {
        validarPeriodo(inicioMes, fimMes);
        LocalDateTime inicio = inicioMes.atStartOfDay();
        LocalDateTime fim = fimMes.atTime(LocalTime.MAX);
        List<Sessao> sessoesAtendidas = sessaoRepository.findAllByDataBetweenAndCompareceuTrue(inicio, fim);
        long totalSessoes = sessoesAtendidas.size();
        long totalPacientes = sessoesAtendidas.stream()
                .map(sessao -> sessao.getPaciente().getId())
                .filter(Objects::nonNull)
                .distinct()
                .count();
        BigDecimal totalReceita = sessoesAtendidas.stream()
                .map(Sessao::getValorSessao)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDespesas = despesaRepository.findAllByDataBetween(inicioMes, fimMes).stream()
                .map(Despesa::getValor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldo = totalReceita.subtract(totalDespesas);
        return new DadosResumoFinanceiro(inicioMes, fimMes, totalPacientes, totalSessoes, totalReceita, totalDespesas, saldo);
    }

    private void validarPeriodo(LocalDate inicioMes, LocalDate fimMes) {
        if (inicioMes == null || fimMes == null) {
            throw new ConsultorioException("Período para consulta é obrigatório!");
        }
        if (inicioMes.isAfter(fimMes)) {
            throw new ConsultorioException("Data inicial não pode ser maior que a data final!");
        }
    }
}
