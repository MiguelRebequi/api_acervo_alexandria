package com.MMR.api_acervo_alexandria.controller;

import com.MMR.api_acervo_alexandria.dto.LoginRequestDTO;
import com.MMR.api_acervo_alexandria.dto.RedefinirSenhaRequestDTO;
import com.MMR.api_acervo_alexandria.dto.TokenResponseDTO;
import com.MMR.api_acervo_alexandria.model.Usuario;
import com.MMR.api_acervo_alexandria.security.RateLimitService;
import com.MMR.api_acervo_alexandria.security.TokenService;
import com.MMR.api_acervo_alexandria.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    private final AuthenticationManager manager;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final RateLimitService rateLimitService;

    public AutenticacaoController(AuthenticationManager manager,
                                  TokenService tokenService,
                                  UsuarioService usuarioService,
                                  RateLimitService rateLimitService) {
        this.manager = manager;
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping
    public ResponseEntity<TokenResponseDTO> efetuarLogin(@RequestBody @Valid LoginRequestDTO dados,
                                                         HttpServletRequest request) {
        String ip = extrairIp(request);
        String chaveConta = "login:" + ip + ":" + dados.email().trim().toLowerCase();
        rateLimitService.verificarLimite("login-ip:" + ip, 50); // freio geral por IP (nunca zera no acerto)
        rateLimitService.verificarLimite(chaveConta, 5);        // tentativas contra uma mesma conta

        var authToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = manager.authenticate(authToken);

        // Login deu certo: limpa só as tentativas dessa conta (o freio geral do IP continua)
        rateLimitService.limparTentativas(chaveConta);

        Usuario usuario = usuarioService.buscarEntidadePorEmail(authentication.getName());
        String tokenJWT = tokenService.gerarToken(usuario);
        return ResponseEntity.ok(new TokenResponseDTO(tokenJWT));
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> redefinirSenha(@RequestBody @Valid RedefinirSenhaRequestDTO dados,
                                               HttpServletRequest request) {
        rateLimitService.verificarLimite("redefinir:" + extrairIp(request));
        usuarioService.redefinirSenha(dados);
        return ResponseEntity.noContent().build();
    }

    private String extrairIp(HttpServletRequest request) {
        // Com server.forward-headers-strategy=native o Tomcat já troca o remoteAddr pelo IP real do cliente.
        // Não confie em X-Forwarded-For lido à mão: o cliente consegue forjar esse cabeçalho.
        return request.getRemoteAddr();
    }
}