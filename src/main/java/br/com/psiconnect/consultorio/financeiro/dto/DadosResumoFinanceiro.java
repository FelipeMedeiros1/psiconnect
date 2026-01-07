package br.com.psiconnect.consultorio.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosResumoFinanceiro(
        LocalDate inicioMes,
        LocalDate fimMes,
        Long pacientesAtendidos,
        Long sessoesAtendidas,
        BigDecimal totalReceita,
        BigDecimal totalDespesas,
        BigDecimal saldo
) {}
