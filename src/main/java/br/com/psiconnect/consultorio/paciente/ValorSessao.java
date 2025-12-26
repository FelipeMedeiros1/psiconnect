package br.com.psiconnect.consultorio.paciente;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class ValorSessao {
    @Column(name = "valor_sessao")
    private BigDecimal valor;

    protected ValorSessao() {
        this.valor = BigDecimal.ZERO;
    }

    private ValorSessao(BigDecimal valor) {
        this.valor = valor;
    }

    public static ValorSessao zero() {
        return new ValorSessao(BigDecimal.ZERO);
    }

    public BigDecimal valor() {
        return valor;
    }

    public void atualizar(BigDecimal valor) {
        this.valor = valor;
    }
}
