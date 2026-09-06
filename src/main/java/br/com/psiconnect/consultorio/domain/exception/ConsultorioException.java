package br.com.psiconnect.consultorio.domain.exception;

public class ConsultorioException extends RuntimeException{
    public ConsultorioException(String mensagem){
        super(mensagem);
    }
}
