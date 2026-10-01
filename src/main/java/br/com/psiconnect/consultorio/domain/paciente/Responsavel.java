package br.com.psiconnect.consultorio.domain.paciente;

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
public class Responsavel {
    private String nomeResponsavel;
    private String cpfResponsavel;

}