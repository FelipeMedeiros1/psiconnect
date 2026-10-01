package br.com.psiconnect.consultorio.infrastructure.web;

import br.com.psiconnect.consultorio.domain.exception.ConsultorioException;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class TratadorDeErros {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TratadorDeErros.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> tratarErro404() {
        log.warn("event=request_rejected reason=entity_not_found status=404");
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosErroValidacao>> tratarErro400(MethodArgumentNotValidException ex) {
        var erros = ex.getFieldErrors();
        log.warn("event=request_rejected reason=validation status=400 violationCount={}", erros.size());
        return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> tratarErro400(HttpMessageNotReadableException ex) {
        log.warn("event=request_rejected reason=unreadable_body status=400");
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ConsultorioException.class)
    public ResponseEntity<String> tratarErroRegraDeNegocio(ConsultorioException ex) {
        log.warn("event=request_rejected reason=business_rule status=400");
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> tratarErro500(Exception ex) {
        // Do not log exception messages: they can contain SQL values, CPF or request bodies.
        log.error("event=request_failed status=500 errorType={} frames={}",
                ex.getClass().getName(), java.util.Arrays.toString(ex.getStackTrace()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + ex.getLocalizedMessage());
    }

    private record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }
}
