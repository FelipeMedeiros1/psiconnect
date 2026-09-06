package br.com.psiconnect.consultorio.application.port;

import br.com.psiconnect.consultorio.domain.paciente.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface PacienteRepository {
    <S extends Paciente> S save(S entidade);
    Optional<Paciente> findById(Long id);
    Paciente getReferenceById(Long id);
    boolean existsById(Long id);
    void deleteById(Long id);
    List<Paciente> findAll();
    Page<Paciente> findAll(Pageable paginacao);
    boolean findStatusById(Long id);
    boolean existsByCpf(String cpf);
    List<Paciente> findByNomeContainingIgnoreCase(String nome);
}
