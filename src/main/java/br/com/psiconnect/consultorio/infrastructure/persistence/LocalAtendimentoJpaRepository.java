package br.com.psiconnect.consultorio.infrastructure.persistence;

import br.com.psiconnect.consultorio.application.port.LocalAtendimentoRepository;
import br.com.psiconnect.consultorio.domain.localatendimento.LocalAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalAtendimentoJpaRepository extends LocalAtendimentoRepository, JpaRepository<LocalAtendimento, Long> {
}
