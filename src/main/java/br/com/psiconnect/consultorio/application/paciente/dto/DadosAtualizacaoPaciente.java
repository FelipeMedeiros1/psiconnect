package br.com.psiconnect.consultorio.application.paciente.dto;

import jakarta.validation.constraints.NotNull;
import br.com.psiconnect.consultorio.application.contato.dto.DadosContato;
import br.com.psiconnect.consultorio.application.endereco.dto.DadosEndereco;

import java.math.BigDecimal;

public record DadosAtualizacaoPaciente(
        @NotNull
        Long id,
        BigDecimal valorConsulta,
        String motivoAlta,
        String nome,
        Boolean status,
        DadosContato contato,
        DadosEndereco endereco

) {
}
