package com.MMR.api_acervo_alexandria.service;

import com.MMR.api_acervo_alexandria.dto.UsuarioRequestDTO;
import com.MMR.api_acervo_alexandria.dto.UsuarioResponseDTO;
import com.MMR.api_acervo_alexandria.model.Credencial;
import com.MMR.api_acervo_alexandria.model.Usuario;
import com.MMR.api_acervo_alexandria.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        return usuarioRepository.findAll().stream()
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
            throw new RuntimeException("CPF já cadastrado no sistema.");
        }
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("E-mail já cadastrado no sistema.");
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
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para o ID: " + id));

        return new UsuarioResponseDTO(
                usuario.getId(), usuario.getNome(),
                usuario.getEmail(), usuario.getTelefoneFixo(), usuario.getTelefoneMovel(), usuario.getEndereco()
        );
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para o ID: " + id));
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
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para o ID: " + id));

        usuario.setAtivo(false); // Exclusão Lógica
        usuarioRepository.save(usuario);
    }
}
