package com.MMR.api_acervo_alexandria.repository;

import com.MMR.api_acervo_alexandria.model.Livro;
import com.MMR.api_acervo_alexandria.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    Optional<Usuario> findByEmailAndAtivoTrue(String email);
    Optional<Usuario> findByCpfAndAtivoTrue(String cpf);
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);

    Optional<Usuario> findByIdAndAtivoTrue(Long id);
    List<Usuario> findByAtivoTrue();
}
