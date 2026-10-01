package br.com.psiconnect.consultorio.application.mapper;

import br.com.psiconnect.consultorio.application.contato.dto.DadosContato;
import br.com.psiconnect.consultorio.application.endereco.dto.DadosEndereco;
import br.com.psiconnect.consultorio.domain.contato.Contato;
import br.com.psiconnect.consultorio.domain.endereco.Endereco;

public final class DadosPessoaisMapper {
    private DadosPessoaisMapper() {}

    public static Contato contato(DadosContato dados) {
        return dados == null ? null : new Contato(dados.telefone(), dados.email());
    }

    public static Endereco endereco(DadosEndereco dados) {
        return dados == null ? null : new Endereco(dados.logradouro(), dados.bairro(),
                dados.cep(), dados.numero(), dados.complemento(), dados.cidade(), dados.uf());
    }
}
