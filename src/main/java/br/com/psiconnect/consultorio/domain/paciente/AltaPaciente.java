package br.com.psiconnect.consultorio.domain.paciente;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AltaPaciente {
    private LocalDateTime data;
    private String usuario;
    private String motivo;

    public AltaPaciente(LocalDateTime data, String usuario, String motivo) {
        this.data = data;
        this.usuario = usuario;
        this.motivo = motivo;
    }
}
