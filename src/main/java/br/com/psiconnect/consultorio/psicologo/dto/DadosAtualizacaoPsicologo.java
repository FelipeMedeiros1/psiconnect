package br.com.psiconnect.consultorio.psicologo.dto;

import jakarta.validation.constraints.NotNull;
import br.com.psiconnect.consultorio.contato.dto.DadosContato;
import br.com.psiconnect.consultorio.endereco.dto.DadosEndereco;


public record DadosAtualizacaoPsicologo(
        @NotNull
        Long id,
        String nome,
        DadosContato contato,
        DadosEndereco endereco) {
}
