package com.MMR.api_acervo_alexandria.service;

import com.MMR.api_acervo_alexandria.dto.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private static final long TAMANHO_MAXIMO_FOTO = 2 * 1024 * 1024; // 2 MB
    private static final String MSG_REDEFINICAO_INVALIDA =
            "Não foi possível redefinir a senha. Verifique os dados informados.";

    public UsuarioService(UsuarioRepository usuarioRepository,  PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private String normalizarCpf(String cpf) {
        return cpf != null ? cpf.replaceAll("\\D", "") : null;
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
        String cpfLimpo = normalizarCpf(dto.cpf());

        if (usuarioRepository.existsByCpf(cpfLimpo)) {
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
        String cpfLimpo = normalizarCpf(dto.cpf());
        if (!cpfLimpo.equals(usuario.getCpf()) && usuarioRepository.existsByCpf(cpfLimpo)) {
            throw new RegraDeNegocioException("CPF já cadastrado no sistema.");
        }
        if (!dto.email().equalsIgnoreCase(usuario.getEmail()) && usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraDeNegocioException("E-mail já cadastrado no sistema.");
        }

        usuario.setNome(dto.nome());
        usuario.setCpf(cpfLimpo);
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
            throw new RegraDeNegocioException("O usuário já está ativo!.");
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

    private Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário autenticado não encontrado."));
    }

    public UsuarioResponseDTO buscarPerfil(String email) {
        Usuario u = buscarUsuarioPorEmail(email);
        return new UsuarioResponseDTO(
                u.getId(), u.getNome(), u.getEmail(),
                u.getTelefoneFixo(), u.getTelefoneMovel(), u.getEndereco()
        );
    }

    @Transactional
    public UsuarioResponseDTO atualizarPerfil(String email, UsuarioPerfilUpdateDTO dto) {
        Usuario u = buscarUsuarioPorEmail(email);
        u.setNome(dto.nome());
        u.setTelefoneFixo(dto.telefoneFixo());
        u.setTelefoneMovel(dto.telefoneMovel());
        u.setEndereco(dto.endereco());

        Usuario salvo = usuarioRepository.save(u);
        return new UsuarioResponseDTO(
                salvo.getId(), salvo.getNome(), salvo.getEmail(),
                salvo.getTelefoneFixo(), salvo.getTelefoneMovel(), salvo.getEndereco()
        );
    }

    @Transactional
    public void alterarSenha(String email, AlterarSenhaRequestDTO dto) {
        Usuario u = buscarUsuarioPorEmail(email);
        if (!passwordEncoder.matches(dto.senhaAtual(), u.getCredencial().getSenhaHash())) {
            throw new RegraDeNegocioException("A senha atual informada está incorreta.");
        }

        u.getCredencial().setSenhaHash(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(u);
    }

    @Transactional
    public void redefinirSenha(RedefinirSenhaRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(dto.email())
                .orElseThrow(() -> new RegraDeNegocioException(MSG_REDEFINICAO_INVALIDA));

        String cpfInformado = normalizarCpf(dto.cpf());
        String cpfSalvo = normalizarCpf(usuario.getCpf());

        if (cpfInformado == null || cpfInformado.isBlank() || !cpfInformado.equals(cpfSalvo)) {
            throw new RegraDeNegocioException(MSG_REDEFINICAO_INVALIDA);
        }

        usuario.getCredencial().setSenhaHash(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void atualizarFoto(String email, MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RegraDeNegocioException("Nenhum arquivo de imagem foi enviado.");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO_FOTO) {
            throw new RegraDeNegocioException("A imagem deve ter no máximo 2 MB.");
        }

        try {
            byte[] bytes = arquivo.getBytes();
            String tipoReal = detectarTipoImagem(bytes);
            if (tipoReal == null) {
                throw new RegraDeNegocioException("Arquivo inválido. Envie uma imagem real nos formatos JPG, PNG ou WebP.");
            }

            Usuario u = buscarUsuarioPorEmail(email);
            u.setFoto(bytes);
            u.setFotoTipo(tipoReal);
            usuarioRepository.save(u);
        } catch (IOException e) {
            throw new RegraDeNegocioException("Não foi possível processar o arquivo enviado.");
        }
    }

    private String detectarTipoImagem(byte[] bytes) {
        if (bytes == null || bytes.length < 12) {
            return null;
        }

        // JPEG: FF D8 FF
        if ((bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8 && (bytes[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }

        // PNG: 89 50 4E 47 0D 0A 1A 0A
        if ((bytes[0] & 0xFF) == 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E && bytes[3] == 0x47
                && bytes[4] == 0x0D && bytes[5] == 0x0A && bytes[6] == 0x1A && bytes[7] == 0x0A) {
            return "image/png";
        }

        // WebP: RIFF....WEBP
        if (bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
                && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P') {
            return "image/webp";
        }

        return null;
    }

    public Usuario buscarEntidadeComFoto(String email) {
        Usuario u = buscarUsuarioPorEmail(email);
        if (u.getFoto() == null || u.getFoto().length == 0) {
            throw new EntityNotFoundException("O usuário ainda não possui foto de perfil.");
        }
        return u;
    }



}
