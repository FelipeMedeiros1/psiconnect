package br.com.psiconnect.consultorio.financeiro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosCadastroDespesa(
        @NotBlank String descricao,
        @NotNull @Positive BigDecimal valor,
        @NotNull LocalDate data
) {}
