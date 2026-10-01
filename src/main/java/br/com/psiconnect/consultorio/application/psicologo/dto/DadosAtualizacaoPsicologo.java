package br.com.psiconnect.consultorio.application.psicologo.dto;

import jakarta.validation.constraints.NotNull;
import br.com.psiconnect.consultorio.application.contato.dto.DadosContato;
import br.com.psiconnect.consultorio.application.endereco.dto.DadosEndereco;

public record DadosAtualizacaoPsicologo(
        @NotNull
        Long id,
        String nome,
        DadosContato contato,
        DadosEndereco endereco) {
}