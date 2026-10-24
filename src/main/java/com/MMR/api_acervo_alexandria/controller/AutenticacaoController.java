package com.MMR.api_acervo_alexandria.controller;

import com.MMR.api_acervo_alexandria.dto.LoginRequestDTO;
import com.MMR.api_acervo_alexandria.dto.TokenResponseDTO;
import com.MMR.api_acervo_alexandria.model.Usuario;
import com.MMR.api_acervo_alexandria.repository.UsuarioRepository;
import com.MMR.api_acervo_alexandria.security.TokenService;
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
    private final UsuarioRepository usuarioRepository;

    public AutenticacaoController(AuthenticationManager manager, TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.manager = manager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseEntity<TokenResponseDTO> efetuarLogin(@RequestBody @Valid LoginRequestDTO dados) {
        var authToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        var authenticateToken = manager.authenticate(authToken);

        Usuario usuario = usuarioRepository.findByEmail(dados.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        String tokenJWT = tokenService.gerarToken(usuario);

        return ResponseEntity.ok(new TokenResponseDTO(tokenJWT));
    }
}
