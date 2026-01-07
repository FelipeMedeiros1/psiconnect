package br.com.psiconnect.consultorio.financeiro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {
    List<Despesa> findAllByDataBetween(LocalDate inicioMes, LocalDate fimMes);
}
