package com.MMR.api_acervo_alexandria.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class TratadorDeErros {

    // 1. Intercepta buscas de IDs ou ISBNs que não existem e devolve 404
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> tratarErro404(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // 2. Intercepta erro de login (e-mail ou senha inválidos) e devolve 401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> tratarErroCredenciaisInvalidas() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha incorretos.");
    }

    // 3. Intercepta acesso negado (dono da conta/pedido ou falta de ROLE_ADMIN) e devolve 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> tratarErroAcessoNegado(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    // 4. Intercepta erros do @Valid (campos obrigatórios/inválidos no JSON) e devolve 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosErroValidacao>> tratarErro400(MethodArgumentNotValidException ex) {
        List<FieldError> erros = ex.getFieldErrors();

        return ResponseEntity.badRequest().body(
                erros.stream().map(DadosErroValidacao::new).collect(Collectors.toList())
        );
    }

    // 5. Intercepta regras de negócio (estoque insuficiente, etc.) e devolve 400
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> tratarErroDeRegraDeNegocio(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    public record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }
}