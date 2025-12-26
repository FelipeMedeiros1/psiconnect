package br.com.psiconnect.consultorio.financeiro.dto;

import br.com.psiconnect.consultorio.financeiro.Despesa;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosDetalheDespesa(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate data
) {
    public DadosDetalheDespesa(Despesa despesa) {
        this(despesa.getId(), despesa.getDescricao(), despesa.getValor(), despesa.getData());
    }
}
