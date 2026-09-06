package br.com.psiconnect.consultorio.domain.paciente;

import lombok.Getter;
import lombok.AccessLevel;
import br.com.psiconnect.consultorio.domain.exception.ConsultorioException;

import br.com.psiconnect.consultorio.domain.consulta.Sessao;
import jakarta.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import br.com.psiconnect.consultorio.domain.contato.Contato;
import br.com.psiconnect.consultorio.domain.endereco.Endereco;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name = "pacientes")
@Entity(name = "Paciente")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private BigDecimal valorSessao;
    private String prontuario;
    private static int contadorProntuario = 1;
    private Boolean status ;
    private String motivoAlta;
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sessao> sessoes = new ArrayList<>();

    public Paciente(Responsavel responsavel, String nome, LocalDate dataNascimento, String profissao, String cpf, Endereco endereco, Contato contato) {
        this.status = true;
        this.responsavel = responsavel;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.profissao = profissao;
        this.cpf = cpf;
        this.endereco = endereco;
        this.contato = contato;
        this.prontuario = "PR" + String.format("%03d", contadorProntuario);
        contadorProntuario++;
        this.valorSessao = BigDecimal.ZERO;

    }

    public void atualizarProntuario(String prontuario) {
        this.prontuario = prontuario;
    }

    public void atualizarInformacoes(String nome, BigDecimal valorConsulta, Contato contato, Endereco endereco) {
        if (valorConsulta != null) {
            definirValorSessao(valorConsulta);
        }
        if (nome != null) {
            this.nome = nome;
        }
        if (contato != null) {
            this.contato = this.contato == null ? contato : this.contato.atualizarContato(contato);
        }

        if (endereco != null) {
            this.endereco = this.endereco == null ? endereco : this.endereco.atualizarEndereco(endereco);
        }
    }

    public void altaPaciente(String motivoAlta) {
        this.status = false;
        this.motivoAlta = motivoAlta;
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

    public void definirValorSessao(BigDecimal valorSessao) {
        if (valorSessao == null || valorSessao.signum() < 0) {
            throw new ConsultorioException("O valor da sessão deve ser informado e não pode ser negativo!");
        }
        this.valorSessao = valorSessao;
    }

    public BigDecimal definirValorParaAgendamento(BigDecimal valorInformado) {
        if (valorInformado != null && valorInformado.signum() < 0) {
            throw new ConsultorioException("O valor da sessão não pode ser negativo!");
        }
        if (valorSessao == null || valorSessao.signum() == 0) {
            definirValorSessao(valorInformado);
        }
        return valorSessao;
    }

    public void validarAgendamento() {
        if (!Boolean.TRUE.equals(status)) {
            throw new ConsultorioException("Consulta não pode ser agendada! Paciente de alta");
        }
    }
}
