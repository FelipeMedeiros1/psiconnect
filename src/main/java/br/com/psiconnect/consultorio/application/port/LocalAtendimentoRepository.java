package br.com.psiconnect.consultorio.application.port;

import br.com.psiconnect.consultorio.domain.localatendimento.LocalAtendimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface LocalAtendimentoRepository {
    <S extends LocalAtendimento> S save(S entidade);
    Optional<LocalAtendimento> findById(Long id);
    Page<LocalAtendimento> findAll(Pageable pageable);
    boolean existsByNomeLugarIgnoreCase(String nomeLugar);
    Optional<LocalAtendimento> findByNomeLugarIgnoreCase(String nomeLugar);
}
