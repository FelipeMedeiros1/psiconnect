package br.com.psiconnect.consultorio.psicologo;

import br.com.psiconnect.consultorio.contato.Contato;
import br.com.psiconnect.consultorio.endereco.Endereco;
import br.com.psiconnect.consultorio.psicologo.dto.DadosAtualizacaoPsicologo;
import br.com.psiconnect.consultorio.psicologo.dto.DadosCadastroPsicologo;
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

    public Psicologo(DadosCadastroPsicologo dados) {
        this.ativo = true;
        this.nome = dados.nome();
        this.crp = dados.crp();
        this.especialidade = dados.especialidade();
        this.contato = new Contato(dados.contato());
        this.endereco = new Endereco(dados.endereco());
    }

    public void atualizarInformacoes(DadosAtualizacaoPsicologo dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }

        if (dados.contato() != null) {
            this.contato.atualizarContato(dados.contato());
        }

        if (dados.endereco() != null) {
            this.endereco.atualizarEndereco(dados.endereco());
        }
    }

    public void desativar() {
        this.ativo = false;
    }


}
