package br.com.psiconnect.consultorio.application.psicologo;

import org.springframework.transaction.annotation.Transactional;
import static br.com.psiconnect.consultorio.application.mapper.DadosPessoaisMapper.*;

import br.com.psiconnect.consultorio.application.port.PsicologoRepository;

import br.com.psiconnect.consultorio.domain.psicologo.Psicologo;

import br.com.psiconnect.consultorio.application.psicologo.dto.DadosDetalhePsicologo;
import br.com.psiconnect.consultorio.domain.exception.ConsultorioException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import br.com.psiconnect.consultorio.application.psicologo.dto.DadosAtualizacaoPsicologo;
import br.com.psiconnect.consultorio.application.psicologo.dto.DadosCadastroPsicologo;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
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
        Psicologo psicologo = new Psicologo(dados.nome(), dados.crp(), dados.especialidade(), contato(dados.contato()), endereco(dados.endereco()));
        return psicologoRepository.save(psicologo);
    }

    @Transactional(readOnly = true)
    public List<Psicologo> listar() {
        Sort ordenacao = Sort.by("especialidade").descending().and(Sort.by("nome").ascending());
        return psicologoRepository.findAll(ordenacao);
    }

    @Transactional(readOnly = true)
    public Page<DadosDetalhePsicologo> consultar(Pageable paginacao) {
        return psicologoRepository.findAll(paginacao).map(DadosDetalhePsicologo::new);
    }

    @Transactional(readOnly = true)
    public List<DadosDetalhePsicologo> buscarPorNome(String nome) {
        return psicologoRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(DadosDetalhePsicologo::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Psicologo buscarPorId(Long id) {
        return psicologoRepository.findById(id).orElseThrow(() -> new RuntimeException("Psicólogo com id: " + id + ", não encontrado"));
    }

    public void atualizar(Long id, DadosAtualizacaoPsicologo dados) {
        Psicologo psicologo = buscarPorId(id);
        psicologo.atualizarInformacoes(dados.nome(), contato(dados.contato()), endereco(dados.endereco()));
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