package com.MMR.api_acervo_alexandria.security;

import com.MMR.api_acervo_alexandria.model.Role;
import com.MMR.api_acervo_alexandria.model.Usuario;
import com.MMR.api_acervo_alexandria.repository.UsuarioRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        // 1. Busca o usuário pelo e-mail no PostgreSQL
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(username).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));

        // Se for ADMIN, ele recebe um array com "ADMIN" e "USER" (para poder comprar livros também).
        // Se não, recebe apenas "USER".
        String[] permissoes = (usuario.getRole() == Role.ADMIN)
                ? new String[]{"ADMIN", "USER"}
                : new String[]{"USER"};

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getCredencial().getSenhaHash())
                .roles(permissoes)
                .build();// Papel padrão de acesso
    }
}
