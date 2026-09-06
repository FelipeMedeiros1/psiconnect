package br.com.psiconnect.consultorio;

import br.com.psiconnect.consultorio.domain.contato.Contato;
import br.com.psiconnect.consultorio.domain.endereco.Endereco;
import br.com.psiconnect.consultorio.domain.psicologo.Especialidade;
import br.com.psiconnect.consultorio.domain.psicologo.Psicologo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PsicologoDomainTest {
    @Test
    void cadastroAtivoPodeSerDesativadoSemPerderIdentificacao() {
        var psicologo = new Psicologo("Ana", "123456", Especialidade.ADULTO, null, null);
        assertThat(psicologo.getAtivo()).isTrue();

        psicologo.desativar();

        assertThat(psicologo.getAtivo()).isFalse();
        assertThat(psicologo.getNome()).isEqualTo("Ana");
        assertThat(psicologo.getCrp()).isEqualTo("123456");
        assertThat(psicologo.getEspecialidade()).isEqualTo(Especialidade.ADULTO);
    }

    @Test
    void atualizacaoParcialPreservaCamposAusentes() {
        var contato = new Contato("11999999999", "ana@example.com");
        var endereco = new Endereco("Rua A", "Centro", "12345678", "1", "Casa", "Cidade", "SP");
        var psicologo = new Psicologo("Ana", "123456", Especialidade.ADULTO, contato, endereco);

        psicologo.atualizarInformacoes(null, null, null);
        assertThat(psicologo.getNome()).isEqualTo("Ana");
        assertThat(psicologo.getContato()).isEqualTo(contato);
        assertThat(psicologo.getEndereco()).isEqualTo(endereco);

        psicologo.atualizarInformacoes("Ana Silva", new Contato("11888888888", null),
                new Endereco(null, null, null, "2", null, null, null));

        assertThat(psicologo.getNome()).isEqualTo("Ana Silva");
        assertThat(psicologo.getContato()).isEqualTo(new Contato("11888888888", "ana@example.com"));
        assertThat(psicologo.getEndereco()).isEqualTo(
                new Endereco("Rua A", "Centro", "12345678", "2", "Casa", "Cidade", "SP"));
        assertThat(contato.getTelefone()).isEqualTo("11999999999");
        assertThat(endereco.getNumero()).isEqualTo("1");
    }

    @Test
    void permiteCompletarContatoEEnderecoAusentes() {
        var psicologo = new Psicologo("Ana", "123456", Especialidade.ADULTO, null, null);
        var contato = new Contato("11999999999", "ana@example.com");
        var endereco = new Endereco("Rua A", "Centro", "12345678", "1", "", "Cidade", "SP");

        psicologo.atualizarInformacoes(null, contato, endereco);

        assertThat(psicologo.getContato()).isEqualTo(contato);
        assertThat(psicologo.getEndereco()).isEqualTo(endereco);
    }
}
