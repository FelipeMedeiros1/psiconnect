package br.com.psiconnect.consultorio.paciente.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosRelatorioPacienteMensal(
        String nome,
        int quantidadeSessoes,
        LocalDate data,
        BigDecimal valorSessaoUnitario,
        BigDecimal valorTotal
) {
}