package com.MMR.api_acervo_alexandria.config;

import com.MMR.api_acervo_alexandria.model.Credencial;
import com.MMR.api_acervo_alexandria.model.Role;
import com.MMR.api_acervo_alexandria.model.Usuario;
import com.MMR.api_acervo_alexandria.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    public AdminSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            return;
        }

        if (usuarioRepository.existsByEmail(adminEmail)) {
            return;
        }

        try {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail(adminEmail);
            admin.setCpf("00000000000");
            admin.setTelefoneMovel("00000000000");
            admin.setEndereco("Sede Administrativa");
            admin.setRole(Role.ADMIN);
            admin.setAtivo(true);

            Credencial credencial = new Credencial();
            credencial.setSenhaHash(passwordEncoder.encode(adminPassword));
            credencial.setUsuario(admin);
            admin.setCredencial(credencial);

            usuarioRepository.save(admin);
            log.info("Usuário ADMIN inicial criado com sucesso.");
        } catch (Exception e) {
            log.error("Falha ao criar usuário ADMIN inicial (a API continuará rodando): {}", e.getMessage());
        }
    }
}