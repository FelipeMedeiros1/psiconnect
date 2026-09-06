package br.com.psiconnect.consultorio.domain.endereco;

import lombok.EqualsAndHashCode;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {
    private String logradouro;
    private String bairro;
    private String cep;
    private String numero;
    private String complemento;
    private String cidade;
    private String uf;

    public Endereco atualizarEndereco(Endereco dados) {
        return new Endereco(
                dados.getLogradouro() != null ? dados.getLogradouro() : this.logradouro,
                dados.getBairro() != null ? dados.getBairro() : this.bairro,
                dados.getCep() != null ? dados.getCep() : this.cep,
                dados.getNumero() != null ? dados.getNumero() : this.numero,
                dados.getComplemento() != null ? dados.getComplemento() : this.complemento,
                dados.getCidade() != null ? dados.getCidade() : this.cidade,
                dados.getUf() != null ? dados.getUf() : this.uf);
    }
}