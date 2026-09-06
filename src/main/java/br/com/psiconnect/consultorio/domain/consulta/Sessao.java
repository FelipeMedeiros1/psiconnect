package br.com.psiconnect.consultorio.domain.consulta;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import br.com.psiconnect.consultorio.domain.paciente.Paciente;
import br.com.psiconnect.consultorio.domain.psicologo.Psicologo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Table(name = "sessoes")
@Entity(name = "Sessao")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Sessao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psicologo_id")
    private Psicologo psicologo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;
    private LocalDateTime data;
    private String prontuario;
    private BigDecimal valorSessao;
    private boolean compareceu;

    public Sessao(LocalDateTime data, Paciente paciente, Psicologo psicologo) {
        this.data = data;
        this.paciente = paciente;
        this.psicologo = psicologo;
        this.prontuario = paciente.getProntuario();
        this.compareceu = false;
    }

    public void marcarPresenca() {
        this.compareceu = true;
    }

    public void registrarEvolucao(String informacoes) {
        String dataFormatada = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.prontuario += "\n" + dataFormatada + " - " + informacoes;
        this.paciente.atualizarProntuario(this.prontuario);
    }
    public void definirValorSessao(BigDecimal valorSessao) {
        this.valorSessao = valorSessao;
    }
}