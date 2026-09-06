package br.com.psiconnect.consultorio;

import br.com.psiconnect.consultorio.domain.paciente.Paciente;
import br.com.psiconnect.consultorio.domain.contato.Contato;
import br.com.psiconnect.consultorio.domain.endereco.Endereco;
import br.com.psiconnect.consultorio.domain.consulta.Sessao;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class PacienteDomainTest {
    private Paciente paciente() {
        return new Paciente(null, "Ana", LocalDate.of(1990, 1, 1), "Professora",
                "12345678901", new Endereco("Rua A", "Centro", "12345678", "1", "", "Cidade", "SP"),
                new Contato("11999999999", "ana@example.com"));
    }

    @Test
    void atualizaValorSemExigirAlteracaoDoNome() {
        var paciente = paciente();
        paciente.atualizarInformacoes(null, new BigDecimal("150.00"), null, null);
        assertThat(paciente.getNome()).isEqualTo("Ana");
        assertThat(paciente.getValorSessao()).isEqualByComparingTo("150.00");
        paciente.atualizarInformacoes("Ana Silva", null, null, null);
        assertThat(paciente.getValorSessao()).isEqualByComparingTo("150.00");
    }

    @Test
    void altaRegistraMotivoEEncerraAtividade() {
        var paciente = paciente();
        paciente.altaPaciente("Tratamento concluído");
        assertThat(paciente.getStatus()).isFalse();
        assertThat(paciente.getMotivoAlta()).isEqualTo("Tratamento concluído");
    }

    @Test
    void presencaEEvolucaoAtualizamHistoricoDoPaciente() {
        var paciente = paciente();
        var sessao = new Sessao(LocalDateTime.now(), paciente, null);
        paciente.adicionarSessao(sessao);
        assertThat(paciente.calcularFaltas()).isEqualTo(1);
        sessao.marcarPresenca();
        sessao.registrarEvolucao("Evolução positiva");
        assertThat(paciente.calcularSessoesRealizadas()).isEqualTo(1);
        assertThat(paciente.calcularFaltas()).isZero();
        assertThat(paciente.getProntuario()).contains("Evolução positiva");
    }
    @Test
    void atualizarContatoCriaNovoValorEPreservaCamposNaoInformados() {
        var original = new Contato("11999999999", "ana@example.com");
        var atualizado = original.atualizarContato(new Contato(null, "novo@example.com"));
        assertThat(original.getEmail()).isEqualTo("ana@example.com");
        assertThat(atualizado).isEqualTo(new Contato("11999999999", "novo@example.com"));
        var paciente = paciente();
        paciente.atualizarInformacoes(null, null, new Contato(null, "novo@example.com"), null);
        assertThat(paciente.getContato()).isEqualTo(atualizado);
    }
}