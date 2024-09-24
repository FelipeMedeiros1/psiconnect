package br.com.psiconnect.consultorio.psicologo;

import br.com.psiconnect.consultorio.psicologo.dto.DadosDetalhamentoPsicologo;
import br.com.psiconnect.infra.exception.ConsultorioException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import br.com.psiconnect.consultorio.psicologo.dto.DadosAtualizacaoPsicologo;
import br.com.psiconnect.consultorio.psicologo.dto.DadosCadastroPsicologo;


import java.util.List;
import java.util.stream.Collectors;


@Service
public class PsicologoService {
    private final PsicologoRepository psicologoRepository;

    public PsicologoService(PsicologoRepository psicologoRepository) {
        this.psicologoRepository = psicologoRepository;
    }

    public Psicologo cadastrar(DadosCadastroPsicologo dados) {
        var jaCadastrado = psicologoRepository.existsByCrp(dados.crp());

        if (jaCadastrado) {
            throw new ConsultorioException("Este CRP: " + dados.crp() + ", já está cadastrado");
        }
        Psicologo psicologo = new Psicologo(dados);
        return psicologoRepository.save(psicologo);
    }

    public List<Psicologo> listar() {
        Sort ordenacao = Sort.by("especialidade").descending().and(Sort.by("nome").ascending());
        return psicologoRepository.findAll(ordenacao);
    }

    public Page<DadosDetalhamentoPsicologo> consultar(Pageable paginacao) {
        return psicologoRepository.findAll(paginacao).map(DadosDetalhamentoPsicologo::new);
    }

    public List<DadosDetalhamentoPsicologo> buscarPorNome(String nome) {
        return psicologoRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(DadosDetalhamentoPsicologo::new)
                .collect(Collectors.toList());
    }

    public Psicologo buscarPorId(Long id) {
        return psicologoRepository.findById(id).orElseThrow(() -> new RuntimeException("Psicólogo com id: " + id + ", não encontrado"));
    }

    public void atualizar(Long id, DadosAtualizacaoPsicologo dados) {
        Psicologo psicologo = buscarPorId(id);
        psicologo.atualizarInformacoes(dados);
        psicologoRepository.save(psicologo);
    }

    public void deletar(Long id) {
        psicologoRepository.deleteById(id);
    }

    public void desativar(Long id) {
        Psicologo psicologo = buscarPorId(id);
        psicologo.desativar();
        psicologoRepository.save(psicologo);
    }
}