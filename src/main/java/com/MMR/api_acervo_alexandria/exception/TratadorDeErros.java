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
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class TratadorDeErros {

    private static final Logger log = LoggerFactory.getLogger(TratadorDeErros.class);

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

    // 5. Intercepta regras de negócio (estoque insuficiente, CPF não confere, etc.) e devolve 400
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<String> tratarErroDeRegraDeNegocio(RegraDeNegocioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // 06. Parâmetro obrigatório ausente (ex.: /livros/busca sem "titulo")
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> tratarParametroAusente(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body("O parâmetro '" + ex.getParameterName() + "' é obrigatório.");
    }

    // 07. Método HTTP não permitido nessa rota
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> tratarMetodoNaoPermitido(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Método HTTP não permitido nesta rota.");
    }

    // 08. Rota inexistente
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> tratarRotaInexistente(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rota não encontrada.");
    }

    // 09. Tipo de conteúdo não suportado (ex.: foto fora de multipart)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> tratarTipoNaoSuportado(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Tipo de conteúdo não suportado.");
    }

    // 10. Intercepta qualquer erro interno inesperado (NullPointer, erro de banco, etc.) e devolve 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> tratarErro500(Exception ex) {
        log.error("Erro interno inesperado: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno no servidor. Tente novamente mais tarde.");
    }

    // 11. Intercepta após muitos requesições
    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<String> tratarErro429(TooManyRequestsException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.getMessage());
    }

    // 12. JSON errôneo baseado no formato mínimo
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> tratarErroJsonMalformado(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body("Corpo da requisição ausente ou com formato JSON inválido.");
    }

    // 13. Parâmetro inválido
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> tratarErroTipoParametro(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body("Parâmetro '" + ex.getName() + "' possui um valor ou tipo inválido.");
    }

    // 14. Ordernação diferente do padrão
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<String> tratarErroOrdenacaoInvalida(PropertyReferenceException ex) {
        return ResponseEntity.badRequest().body("Campo de ordenação inválido: '" + ex.getPropertyName() + "'.");
    }

    public record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }
}