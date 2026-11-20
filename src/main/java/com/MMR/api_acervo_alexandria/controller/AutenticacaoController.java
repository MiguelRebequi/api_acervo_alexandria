package com.MMR.api_acervo_alexandria.controller;

import com.MMR.api_acervo_alexandria.dto.LoginRequestDTO;
import com.MMR.api_acervo_alexandria.dto.RedefinirSenhaRequestDTO;
import com.MMR.api_acervo_alexandria.dto.TokenResponseDTO;
import com.MMR.api_acervo_alexandria.exception.RegraDeNegocioException;
import com.MMR.api_acervo_alexandria.model.Usuario;
import com.MMR.api_acervo_alexandria.repository.UsuarioRepository;
import com.MMR.api_acervo_alexandria.security.TokenService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    public AutenticacaoController(AuthenticationManager manager, TokenService tokenService, UsuarioRepository usuarioRepository,  PasswordEncoder passwordEncoder) {
        this.manager = manager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<TokenResponseDTO> efetuarLogin(@RequestBody @Valid LoginRequestDTO dados) {
        var authToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        manager.authenticate(authToken);

        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(dados.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        String tokenJWT = tokenService.gerarToken(usuario);

        return ResponseEntity.ok(new TokenResponseDTO(tokenJWT));
    }

    @PostMapping("/redefinir-senha")
    @Transactional
    public ResponseEntity<Void> redefinirSenha(@RequestBody @Valid RedefinirSenhaRequestDTO dados) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(dados.email())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para o e-mail informado."));

        // CPF será obrigatório para redefinir a senha
        String cpfLimpo = dados.cpf().replaceAll("\\D", "");
        if (!usuario.getCpf().equals(cpfLimpo)) {
                throw new RegraDeNegocioException("Os dados de validação não conferem com o titular da conta.");
        }

        // Gera o novo hash BCrypt e atualiza a credencial vinculada ao usuário
        String novaSenhaHash = passwordEncoder.encode(dados.novaSenha());
        usuario.getCredencial().setSenhaHash(novaSenhaHash);

        usuarioRepository.save(usuario);

        return ResponseEntity.noContent().build(); // Retorna 204 No Content (Sucesso)
    }
}
