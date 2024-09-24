package br.com.psiconnect.consultorio.paciente.dto;

import jakarta.validation.constraints.NotNull;
import br.com.psiconnect.consultorio.contato.dto.DadosContato;
import br.com.psiconnect.consultorio.endereco.dto.DadosEndereco;

import java.math.BigDecimal;

public record DadosAtualizacaoPaciente(
        @NotNull
        Long id,
        BigDecimal valorConsulta,
        String motivoAlta,
        String nome,
        DadosContato contato,
        DadosEndereco endereco

) {
}
