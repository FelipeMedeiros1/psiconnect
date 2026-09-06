package br.com.psiconnect.consultorio.infrastructure.configuration;

import br.com.psiconnect.consultorio.domain.consulta.AgendaConsultas;
import br.com.psiconnect.consultorio.domain.consulta.AgendamentoSessao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class DominioConfiguration {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public AgendamentoSessao agendamentoSessao(AgendaConsultas agenda, Clock clock) {
        return new AgendamentoSessao(agenda, clock);
    }
}
