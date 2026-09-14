package br.com.psiconnect.consultorio.domain.localatendimento;

import br.com.psiconnect.consultorio.domain.endereco.Endereco;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "locais_atendimento")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalAtendimento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nomeLugar;
    @Embedded
    private Endereco endereco;
    private boolean ativo = true;

    public LocalAtendimento(String nomeLugar, Endereco endereco) {
        this.nomeLugar = nomeLugar;
        this.endereco = endereco;
    }

    public void atualizar(String nomeLugar, Endereco endereco) {
        if (nomeLugar != null && !nomeLugar.isBlank()) this.nomeLugar = nomeLugar;
        if (endereco != null) this.endereco = this.endereco == null ? endereco : this.endereco.atualizarEndereco(endereco);
    }

    public void inativar() { this.ativo = false; }
}
