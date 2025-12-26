package br.com.psiconnect.consultorio.paciente;

import br.com.psiconnect.consultorio.consulta.Sessao;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @Embedded
    private Responsavel responsavel;
    private String nome;
    private LocalDate dataNascimento;
    private String cpf;
    private String profissao;
    @Embedded
    private Contato contato;
    @Embedded
    private Endereco endereco;
    @Embedded
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private ValorSessao valorSessao;
    private String prontuario;
    private static int contadorProntuario = 1;
    private Boolean status ;
    private String motivoAlta;
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sessao> sessoes = new ArrayList<>();


    public Paciente(DadosCadastroPaciente dados) {
        this.status = true;
        this.responsavel = dados.responsavel();
        this.nome = dados.nome();
        this.dataNascimento = dados.dataNascimento();
        this.profissao = dados.profissao();
        this.cpf = dados.cpf();
        this.endereco = new Endereco(dados.endereco());
        this.contato = new Contato(dados.contato());
        this.prontuario = "PR" + String.format("%03d", contadorProntuario);
        contadorProntuario++;
        this.valorSessao = ValorSessao.zero();

    }

    public void atualizarProntuario(String prontuario) {
        this.prontuario = prontuario;
    }

    public void atualizarInformacoes(DadosAtualizacaoPaciente dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.valorConsulta() != null) {
            atualizarValorSessao(dados.valorConsulta());
        }
        if (dados.contato() != null) {
            this.contato.atualizarContato(dados.contato());
        }

        if (dados.endereco() != null) {
            this.endereco.atualizarEndereco(dados.endereco());
        }
    }

    public DadosAtualizacaoPaciente altaPaciente(String motivoAlta) {
        this.status = false;
        this.motivoAlta = motivoAlta;
        return null;
    }

    public BigDecimal getValorSessao() {
        return obterValorSessao().valor();
    }

    public void atualizarValorSessao(BigDecimal valorSessao) {
        obterValorSessao().atualizar(valorSessao);
    }

    private ValorSessao obterValorSessao() {
        if (this.valorSessao == null) {
            this.valorSessao = ValorSessao.zero();
        }
        return this.valorSessao;
    }



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
