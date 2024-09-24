package br.com.psiconnect.consultorio.contato;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import br.com.psiconnect.consultorio.contato.dto.DadosContato;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Contato {
    private String telefone;
    private String email;

    public Contato(DadosContato dados) {
        this.telefone = dados.telefone();
        this.email = dados.email();
    }

    public void atualizarContato(DadosContato dados){
        if (dados.telefone() != null){
            this.telefone = dados.telefone();
        }
        if (dados.email() != null){
            this.email = dados.email();
        }
    }



}
