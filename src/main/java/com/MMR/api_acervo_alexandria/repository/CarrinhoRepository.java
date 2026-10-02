package com.MMR.api_acervo_alexandria.repository;

import com.MMR.api_acervo_alexandria.model.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    List<Carrinho> findByUsuarioId(Long usuarioId);
    Optional<Carrinho> findByUsuarioIdAndLivroId(Long usuarioId, Long livroId);
    void deleteByUsuarioId(Long usuarioId);
}
