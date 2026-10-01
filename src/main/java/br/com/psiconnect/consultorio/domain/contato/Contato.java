package br.com.psiconnect.consultorio.domain.contato;

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
public class Contato {
    private String telefone;
    private String email;

    public Contato atualizarContato(Contato dados) {
        return new Contato(
                dados.getTelefone() != null ? dados.getTelefone() : this.telefone,
                dados.getEmail() != null ? dados.getEmail() : this.email);
    }

}