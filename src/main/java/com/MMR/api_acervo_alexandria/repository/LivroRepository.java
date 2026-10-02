package com.MMR.api_acervo_alexandria.repository;

import com.MMR.api_acervo_alexandria.model.Livro;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LivroRepository extends CrudRepository<Livro,Long> {
    Optional<Livro> findByIsbn(String isbn);
    Optional<Livro> findByTituloContainingIgnoreCase(String titulo);
}
