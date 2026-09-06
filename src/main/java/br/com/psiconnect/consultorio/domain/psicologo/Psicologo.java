package br.com.psiconnect.consultorio.domain.psicologo;

import br.com.psiconnect.consultorio.domain.contato.Contato;
import br.com.psiconnect.consultorio.domain.endereco.Endereco;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "psicologos")
@Entity(name = "Psicologo")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Psicologo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String crp;
    @Enumerated(EnumType.STRING)
    private Especialidade especialidade;
    @Embedded
    private Contato contato;
    @Embedded
    private Endereco endereco;
    private Boolean ativo;

    public Psicologo(String nome, String crp, Especialidade especialidade, Contato contato, Endereco endereco) {
        this.ativo = true;
        this.nome = nome;
        this.crp = crp;
        this.especialidade = especialidade;
        this.contato = contato;
        this.endereco = endereco;
    }

    public void atualizarInformacoes(String nome, Contato contato, Endereco endereco) {
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

    public void desativar() {
        this.ativo = false;
    }

}