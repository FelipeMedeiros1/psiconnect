package br.com.psiconnect.consultorio.paciente;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Responsavel {
    private String nomeResponsavel;
    private String cpfResponsavel;

}
