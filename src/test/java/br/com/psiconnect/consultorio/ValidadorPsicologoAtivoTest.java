package br.com.psiconnect.consultorio;

import br.com.psiconnect.consultorio.application.consulta.agendamento.ValidadorPsicologoAtivo;
import br.com.psiconnect.consultorio.application.consulta.dto.DadosAgendamentoSessao;
import br.com.psiconnect.consultorio.application.port.PsicologoRepository;
import br.com.psiconnect.consultorio.domain.exception.ConsultorioException;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidadorPsicologoAtivoTest {
    private final PsicologoRepository repository = mock(PsicologoRepository.class);
    private final ValidadorPsicologoAtivo validador = new ValidadorPsicologoAtivo(repository);

    @Test
    void rejeitaPsicologoInativoUsandoSeuProprioRepositorio() {
        when(repository.findAtivoById(7L)).thenReturn(false);
        var dados = new DadosAgendamentoSessao(7L, 2L, LocalDateTime.now().plusDays(1), null, null);
        assertThatThrownBy(() -> validador.validar(dados)).isInstanceOf(ConsultorioException.class);
        verify(repository).findAtivoById(7L);
    }

    @Test
    void aceitaPsicologoAtivo() {
        when(repository.findAtivoById(7L)).thenReturn(true);
        var dados = new DadosAgendamentoSessao(7L, 2L, LocalDateTime.now().plusDays(1), null, null);
        assertThatCode(() -> validador.validar(dados)).doesNotThrowAnyException();
    }

    @Test
    void selecaoAutomaticaNaoConsultaIdNulo() {
        validador.validar(new DadosAgendamentoSessao(null, 2L, LocalDateTime.now().plusDays(1), null, null));
        verifyNoInteractions(repository);
    }
}