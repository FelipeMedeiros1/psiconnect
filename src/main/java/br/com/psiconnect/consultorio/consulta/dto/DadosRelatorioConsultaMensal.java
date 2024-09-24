package br.com.psiconnect.consultorio.consulta.dto;

import java.math.BigDecimal;

public record DadosRelatorioConsultaMensal(
        String nomePsicologo,
        String crp,
        Long quantidadeConsultasNoMes,
        String nomePaciente,
        BigDecimal valorTotalConsultas
) {}
