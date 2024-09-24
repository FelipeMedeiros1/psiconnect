package br.com.psiconnect.infra.exception;

public class ConsultorioException extends RuntimeException{
    public ConsultorioException(String mensagem){
        super(mensagem);
    }
}
