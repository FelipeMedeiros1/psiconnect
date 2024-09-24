package br.com.psiconnect.consultorio.paciente.dto;

import br.com.psiconnect.consultorio.contato.dto.DadosContato;
import br.com.psiconnect.consultorio.endereco.dto.DadosEndereco;
import br.com.psiconnect.consultorio.paciente.Responsavel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;


public record DadosCadastroPaciente(

        Responsavel responsavel,
        @NotBlank
        LocalDate dataNascimento,
        @NotBlank
        String nome,
        @NotBlank
        @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}\\-?\\d{2}")
        String cpf,
        @NotBlank
        String profissao,
        @NotNull
        @Valid
        DadosContato contato,
        @NotNull
        @Valid
        DadosEndereco endereco,
        @NotNull
        @Valid
        BigDecimal valorConsulta

) {
}
