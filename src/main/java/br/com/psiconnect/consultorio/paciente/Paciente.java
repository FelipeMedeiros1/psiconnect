package br.com.psiconnect.consultorio.paciente;

import br.com.psiconnect.consultorio.consulta.Sessao;
import jakarta.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import br.com.psiconnect.consultorio.contato.Contato;
import br.com.psiconnect.consultorio.endereco.Endereco;
import br.com.psiconnect.consultorio.paciente.dto.DadosAtualizacaoPaciente;
import br.com.psiconnect.consultorio.paciente.dto.DadosCadastroPaciente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name = "pacientes")
@Entity(name = "Paciente")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Responsavel responsavel;
    private String nome;
    private LocalDate dataNascimento;
    private String cpf;
    private String profissao;
    @Embedded
    private Contato contato;
    @Embedded
    private Endereco endereco;
    private BigDecimal valorSessao;
    private String prontuario;
    private static int contadorProntuario = 1;
    private Boolean ativo;
    private String motivoAlta;

    public Paciente(DadosCadastroPaciente dados) {
        this.ativo = true;
        this.responsavel = dados.responsavel();
        this.nome = dados.nome();
        this.dataNascimento = dados.dataNascimento();
        this.profissao = dados.profissao();
        this.cpf = dados.cpf();
        this.endereco = new Endereco(dados.endereco());
        this.contato = new Contato(dados.contato());
        this.prontuario = "PR" + String.format("%03d", contadorProntuario);
        contadorProntuario++;
        this.valorSessao = BigDecimal.ZERO;

    }

    public void atualizarProntuario(String prontuario) {
        this.prontuario = prontuario;
    }

    public void atualizarInformacoes(DadosAtualizacaoPaciente dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.nome() != null) {
            this.valorSessao = dados.valorConsulta();
        }
        if (dados.contato() != null) {
            this.contato.atualizarContato(dados.contato());
        }

        if (dados.endereco() != null) {
            this.endereco.atualizarEndereco(dados.endereco());
        }
    }

    public DadosAtualizacaoPaciente altaPaciente(String motivoAlta) {
        this.ativo = false;
        this.motivoAlta = motivoAlta;
        return null;
    }

    private List<Sessao> sessoes = new ArrayList<>();

    public void adicionarSessao(Sessao sessao) {
        this.sessoes.add(sessao);
    }

    public long calcularSessoesRealizadas() {
        return this.sessoes.stream()
                .filter(Sessao::isCompareceu)
                .count();
    }

    public long calcularFaltas() {
        return this.sessoes.stream()
                .filter(sessao -> !sessao.isCompareceu())
                .count();
    }

}