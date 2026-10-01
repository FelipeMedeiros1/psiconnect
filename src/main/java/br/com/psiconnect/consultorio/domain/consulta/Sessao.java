package br.com.psiconnect.consultorio.domain.consulta;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import br.com.psiconnect.consultorio.domain.paciente.Paciente;
import br.com.psiconnect.consultorio.domain.psicologo.Psicologo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Table(name = "sessoes")
@Entity(name = "Sessao")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Sessao {
    private static final Pattern INICIO_EVOLUCAO = Pattern.compile(
            "(?m)^\\d{2}/\\d{2}/\\d{4}(?: \\d{2}:\\d{2})? - ");
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
    @Column(columnDefinition = "TEXT")
    private String prontuario;
    private BigDecimal valorSessao;
    private boolean compareceu;

    Sessao(LocalDateTime data, Paciente paciente, Psicologo psicologo, BigDecimal valorSessao) {
        this.data = data;
        this.paciente = paciente;
        this.psicologo = psicologo;
        this.prontuario = paciente.getProntuario();
        this.compareceu = false;
        this.valorSessao = valorSessao;
    }

    public void marcarPresenca() {
        this.compareceu = true;
    }

    public static Sessao atendimentoRealizado(LocalDateTime data, Paciente paciente, Psicologo psicologo,
                                               BigDecimal valorSessao, String evolucao) {
        Sessao sessao = new Sessao(data, paciente, psicologo, valorSessao);
        sessao.marcarPresenca();
        sessao.registrarEvolucao(evolucao);
        return sessao;
    }

    public void reagendar(LocalDateTime data, Paciente paciente, Psicologo psicologo, BigDecimal valorSessao) {
        this.data = data;
        this.paciente = paciente;
        this.psicologo = psicologo;
        this.valorSessao = valorSessao;
        this.prontuario = paciente.getProntuario();
    }

    public void registrarEvolucao(String informacoes) {
        // A sessao pode ter sido agendada antes de atendimentos anteriores serem
        // concluidos. Use sempre o prontuario atual do paciente para nao sobrescrever
        // evolucoes registradas depois do agendamento desta sessao.
        this.prontuario = this.paciente.getProntuario();
        this.prontuario = prontuarioComQuebraDeLinha() + registroEvolucao(informacoes);
        this.paciente.atualizarProntuario(this.prontuario);
    }

    public void editarEvolucao(String informacoes) {
        int inicioUltimaEvolucao = inicioUltimaEvolucao();
        String registro = registroEvolucao(informacoes);
        this.prontuario = inicioUltimaEvolucao >= 0
                ? this.prontuario.substring(0, inicioUltimaEvolucao) + registro
                : prontuarioComQuebraDeLinha() + registro;
        this.paciente.atualizarProntuario(this.prontuario);
    }

    public String getEvolucao() {
        int inicioUltimaEvolucao = inicioUltimaEvolucao();
        if (inicioUltimaEvolucao < 0) return "";
        String registro = this.prontuario.substring(inicioUltimaEvolucao);
        int primeiroSeparador = registro.indexOf(" - ");
        if (primeiroSeparador < 0) return registro;
        int segundoSeparador = registro.indexOf(" - ", primeiroSeparador + 3);
        return segundoSeparador >= 0 ? registro.substring(segundoSeparador + 3) : registro.substring(primeiroSeparador + 3);
    }

    private int inicioUltimaEvolucao() {
        var matcher = INICIO_EVOLUCAO.matcher(this.prontuario);
        int inicio = -1;
        while (matcher.find()) inicio = matcher.start();
        return inicio;
    }

    private String prontuarioComQuebraDeLinha() {
        return this.prontuario.endsWith("\n") ? this.prontuario : this.prontuario + "\n";
    }

    private String registroEvolucao(String informacoes) {
        String dataEdicao = this.data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return dataEdicao + " - " + this.psicologo.getNome() + " - " + informacoes;
    }
}
