package com.MMR.api_acervo_alexandria.service;

import com.MMR.api_acervo_alexandria.dto.UsuarioRequestDTO;
import com.MMR.api_acervo_alexandria.dto.UsuarioResponseDTO;
import com.MMR.api_acervo_alexandria.exception.RegraDeNegocioException;
import com.MMR.api_acervo_alexandria.model.Credencial;
import com.MMR.api_acervo_alexandria.model.Role;
import com.MMR.api_acervo_alexandria.model.Usuario;
import com.MMR.api_acervo_alexandria.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,  PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findByAtivoTrue().stream()
                .map(usuario -> new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getTelefoneFixo(),
                        usuario.getTelefoneMovel(),
                        usuario.getEndereco()))
                .toList();
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByCpf(dto.cpf())) {
            throw new RegraDeNegocioException("CPF já cadastrado no sistema.");
        }
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraDeNegocioException("E-mail já cadastrado no sistema.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setCpf(dto.cpf());
        usuario.setTelefoneFixo(dto.telefoneFixo());
        usuario.setTelefoneMovel(dto.telefoneMovel());
        usuario.setEmail(dto.email());
        usuario.setEndereco(dto.endereco());

        Credencial credencial = new Credencial();
        credencial.setSenhaHash(passwordEncoder.encode(dto.senha())); // Futuramente aplicaremos o BCrypt aqui
        credencial.setUsuario(usuario);

        usuario.setCredencial(credencial);

        Usuario salvo = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(
                salvo.getId(), salvo.getNome(),
                salvo.getEmail(), salvo.getTelefoneFixo(), salvo.getTelefoneMovel(), salvo.getEndereco()
        );
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        validarDonoOuAdmin(id);
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para o ID: " + id));

        return new UsuarioResponseDTO(
                usuario.getId(), usuario.getNome(),
                usuario.getEmail(), usuario.getTelefoneFixo(), usuario.getTelefoneMovel(), usuario.getEndereco()
        );
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        validarDonoOuAdmin(id);
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para o ID: " + id));
        usuario.setNome(dto.nome());
        usuario.setCpf(dto.cpf());
        usuario.setTelefoneFixo(dto.telefoneFixo());
        usuario.setTelefoneMovel(dto.telefoneMovel());
        usuario.setEmail(dto.email());
        usuario.setEndereco(dto.endereco());

        // Como CascadeType.ALL está ativo, alterar a credencial aqui já atualiza o banco
        usuario.getCredencial().setSenhaHash(passwordEncoder.encode(dto.senha()));

        Usuario salvo = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(
                salvo.getId(), salvo.getNome(),
                salvo.getEmail(), salvo.getTelefoneFixo(), salvo.getTelefoneMovel(), salvo.getEndereco()
        );
    }


    @Transactional
    public void excluir(Long id) {
        validarDonoOuAdmin(id);
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para o ID: " + id));

        usuario.setAtivo(false); // Exclusão Lógica
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void reativar(Long id) {
        validarDonoOuAdmin(id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para o ID: " + id));

        if (Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new RuntimeException("O usuário já está ativo!.");
        }

        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }

    private void validarDonoOuAdmin(Long idAlvo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Usuário não autenticado.");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (isAdmin) {
            return;
        }

        Usuario logado = usuarioRepository.findByEmailAndAtivoTrue(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Usuário autenticado não encontrado."));

        if (!logado.getId().equals(idAlvo)) {
            throw new AccessDeniedException("Acesso negado: você só pode acessar ou modificar a sua própria conta.");        }
    }

    @Transactional
    public void promoverParaAdmin(Long id){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
        usuario.setRole(Role.ADMIN);
        usuarioRepository.save(usuario);
    }


}
