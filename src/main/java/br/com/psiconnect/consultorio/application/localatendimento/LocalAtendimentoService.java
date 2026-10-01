package br.com.psiconnect.consultorio.application.localatendimento;

import br.com.psiconnect.consultorio.application.localatendimento.dto.*;
import br.com.psiconnect.consultorio.application.port.LocalAtendimentoRepository;
import br.com.psiconnect.consultorio.domain.endereco.Endereco;
import br.com.psiconnect.consultorio.domain.exception.ConsultorioException;
import br.com.psiconnect.consultorio.domain.localatendimento.LocalAtendimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional
public class LocalAtendimentoService {
    private final LocalAtendimentoRepository repository;
    public LocalAtendimentoService(LocalAtendimentoRepository repository) { this.repository = repository; }

    public DetalheLocalAtendimento cadastrar(DadosLocalAtendimento dados) {
        if (repository.existsByNomeLugarIgnoreCase(dados.nomeLugar()))
            throw new ConsultorioException("Local de atendimento já cadastrado!");
        return new DetalheLocalAtendimento(repository.save(new LocalAtendimento(dados.nomeLugar(), endereco(dados))));
    }
    @Transactional(readOnly = true)
    public Page<DetalheLocalAtendimento> listar(Pageable pageable) { return repository.findAll(pageable).map(DetalheLocalAtendimento::new); }
    @Transactional(readOnly = true)
    public DetalheLocalAtendimento buscar(Long id) { return new DetalheLocalAtendimento(local(id)); }
    public DetalheLocalAtendimento atualizar(Long id, DadosLocalAtendimento dados) {
        var local = local(id); local.atualizar(dados.nomeLugar(), endereco(dados));
        return new DetalheLocalAtendimento(repository.save(local));
    }
    public void inativar(Long id) { var local = local(id); local.inativar(); repository.save(local); }
    public LocalAtendimento local(Long id) { return repository.findById(id).orElseThrow(() -> new ConsultorioException("Local não encontrado!")); }
    private Endereco endereco(DadosLocalAtendimento dados) {
        var e = dados.endereco();
        return e == null ? null : new Endereco(e.logradouro(), e.bairro(), e.cep(), e.numero(), e.complemento(), e.cidade(), e.uf());
    }
}
