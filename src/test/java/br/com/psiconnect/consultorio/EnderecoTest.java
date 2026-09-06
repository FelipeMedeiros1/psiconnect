package br.com.psiconnect.consultorio;

import br.com.psiconnect.consultorio.domain.endereco.Endereco;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnderecoTest {
    @Test
    void mudancaDeEnderecoSubstituiValoresSemModificarOriginal() {
        var original = new Endereco("Rua A", "Centro", "12345678", "1", "Casa", "Cidade", "SP");
        var novo = new Endereco("Rua B", "Jardim", "87654321", "2", "", "Outra Cidade", "RJ");

        var atualizado = original.atualizarEndereco(novo);

        assertThat(atualizado).isEqualTo(novo);
        assertThat(original.getLogradouro()).isEqualTo("Rua A");
        assertThat(original.getComplemento()).isEqualTo("Casa");
    }

    @Test
    void camposNulosPreservamEnderecoCompleto() {
        var original = new Endereco("Rua A", "Centro", "12345678", "1", "Casa", "Cidade", "SP");

        assertThat(original.atualizarEndereco(new Endereco(null, null, null, null, null, null, null)))
                .isEqualTo(original);
    }
}
