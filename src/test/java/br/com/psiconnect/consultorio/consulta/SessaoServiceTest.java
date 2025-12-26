package br.com.psiconnect.consultorio.consulta;

import br.com.psiconnect.consultorio.consulta.dto.DadosCancelamentoSessao;
import br.com.psiconnect.consultorio.paciente.Paciente;
import br.com.psiconnect.consultorio.paciente.PacienteRepository;
import br.com.psiconnect.consultorio.psicologo.Psicologo;
import br.com.psiconnect.consultorio.psicologo.PsicologoRepository;
import br.com.psiconnect.infra.exception.ConsultorioException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessaoServiceTest {

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private PsicologoRepository psicologoRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private RelatorioConsultaMensalAssembler relatorioConsultaMensalAssembler;

    @Test
    void deveCancelarSessaoComoCobravel() {
        Sessao sessao = criarSessao();
        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));

        SessaoService service = new SessaoService(sessaoRepository, psicologoRepository, pacienteRepository, List.of(), relatorioConsultaMensalAssembler);
        service.inativar(1L, new DadosCancelamentoSessao(true, false));

        Assertions.assertEquals(StatusSessao.CANCELADA_COBRAVEL, sessao.getStatus());
        verify(sessaoRepository).save(sessao);
    }

    @Test
    void deveCancelarSessaoComoReagendada() {
        Sessao sessao = criarSessao();
        when(sessaoRepository.findById(2L)).thenReturn(Optional.of(sessao));

        SessaoService service = new SessaoService(sessaoRepository, psicologoRepository, pacienteRepository, List.of(), relatorioConsultaMensalAssembler);
        service.inativar(2L, new DadosCancelamentoSessao(false, true));

        Assertions.assertEquals(StatusSessao.CANCELADA_REAGENDADA, sessao.getStatus());
        verify(sessaoRepository).save(sessao);
    }

    @Test
    void deveLancarExcecaoQuandoCancelamentoForCobravelEReagendado() {
        Sessao sessao = criarSessao();
        when(sessaoRepository.findById(3L)).thenReturn(Optional.of(sessao));

        SessaoService service = new SessaoService(sessaoRepository, psicologoRepository, pacienteRepository, List.of(), relatorioConsultaMensalAssembler);

        Assertions.assertThrows(ConsultorioException.class, () -> service.inativar(3L, new DadosCancelamentoSessao(true, true)));
    }

    private Sessao criarSessao() {
        Psicologo psicologo = new Psicologo();
        Paciente paciente = new Paciente();
        return new Sessao(LocalDateTime.now(), paciente, psicologo);
    }
}
