package com.MMR.api_acervo_alexandria.config;


import com.MMR.api_acervo_alexandria.model.Credencial;
import com.MMR.api_acervo_alexandria.model.Role;
import com.MMR.api_acervo_alexandria.model.Usuario;
import com.MMR.api_acervo_alexandria.repository.UsuarioRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("$app.admin.email:$")
    private String adminEmail;

    @Value("$app.admin.password:$")
    private String adminPassword;

    public AdminSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String @NonNull ... args){
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            return;
        }

        if (usuarioRepository.findByEmailAndAtivoTrue(adminEmail).isPresent()) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setNome("Administrador");
        admin.setEmail(adminEmail);
        admin.setCpf("00000000000");
        admin.setEndereco("Sede Administrativa");
        admin.setRole(Role.ADMIN);
        admin.setAtivo(true);

        Credencial credencial = new Credencial();
        credencial.setSenhaHash(passwordEncoder.encode(adminPassword));
        admin.setCredencial(credencial);

        usuarioRepository.save(admin);
    }
}
