package br.com.psiconnect.consultorio.financeiro;

import br.com.psiconnect.consultorio.consulta.Sessao;
import br.com.psiconnect.consultorio.consulta.SessaoRepository;
import br.com.psiconnect.consultorio.financeiro.dto.DadosResumoFinanceiro;
import br.com.psiconnect.consultorio.paciente.Paciente;
import br.com.psiconnect.consultorio.psicologo.Psicologo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinanceiroServiceTest {

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private DespesaRepository despesaRepository;

    @Test
    void deveGerarResumoComTotaisDeReceitaEDespesa() {
        LocalDate inicioMes = LocalDate.of(2024, 1, 1);
        LocalDate fimMes = LocalDate.of(2024, 1, 31);
        List<Sessao> sessoes = List.of(
                criarSessao(10L, new BigDecimal("100.00")),
                criarSessao(10L, new BigDecimal("150.00")),
                criarSessao(20L, new BigDecimal("200.00"))
        );
        List<Despesa> despesas = List.of(
                criarDespesa("Locação", new BigDecimal("300.00"), inicioMes),
                criarDespesa("Energia", new BigDecimal("100.00"), fimMes)
        );

        when(sessaoRepository.findAllByDataBetweenAndCompareceuTrue(inicioMes.atStartOfDay(), fimMes.atTime(23, 59, 59, 999999999)))
                .thenReturn(sessoes);
        when(despesaRepository.findAllByDataBetween(inicioMes, fimMes)).thenReturn(despesas);

        FinanceiroService service = new FinanceiroService(sessaoRepository, despesaRepository);
        DadosResumoFinanceiro resumo = service.gerarResumo(inicioMes, fimMes);

        Assertions.assertEquals(2L, resumo.pacientesAtendidos());
        Assertions.assertEquals(3L, resumo.sessoesAtendidas());
        Assertions.assertEquals(new BigDecimal("450.00"), resumo.totalReceita());
        Assertions.assertEquals(new BigDecimal("400.00"), resumo.totalDespesas());
        Assertions.assertEquals(new BigDecimal("50.00"), resumo.saldo());
    }

    private Sessao criarSessao(Long idPaciente, BigDecimal valorSessao) {
        Psicologo psicologo = new Psicologo();
        Paciente paciente = new Paciente();
        paciente.setId(idPaciente);
        Sessao sessao = new Sessao(LocalDateTime.now(), paciente, psicologo);
        sessao.setValorSessao(valorSessao);
        return sessao;
    }

    private Despesa criarDespesa(String descricao, BigDecimal valor, LocalDate data) {
        Despesa despesa = new Despesa();
        despesa.setDescricao(descricao);
        despesa.setValor(valor);
        despesa.setData(data);
        return despesa;
    }
}
