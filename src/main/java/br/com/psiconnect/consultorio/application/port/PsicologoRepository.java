package br.com.psiconnect.consultorio.application.port;

import br.com.psiconnect.consultorio.domain.psicologo.Psicologo;
import br.com.psiconnect.consultorio.domain.psicologo.Especialidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface PsicologoRepository {
    <S extends Psicologo> S save(S entidade);
    Optional<Psicologo> findById(Long id);
    Psicologo getReferenceById(Long id);
    boolean existsById(Long id);
    void deleteById(Long id);
    List<Psicologo> findAll();
    Page<Psicologo> findAll(Pageable paginacao);
    boolean findAtivoById(Long id);
    boolean existsByCrp(String crp);
    Psicologo escolherPsicologoLivreNaData(Especialidade especialidade, LocalDateTime data);
    List<Psicologo> findByNomeContainingIgnoreCase(String nome);
    List<Psicologo> findAll(Sort ordenacao);
}
