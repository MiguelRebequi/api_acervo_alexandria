package com.MMR.api_acervo_alexandria.repository;

import com.MMR.api_acervo_alexandria.model.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends CrudRepository<Livro,Long> {
    Optional<Livro> findByIsbn(String isbn);
    Optional<Livro> findByTituloContainingIgnoreCase(String titulo);
    boolean existsByIsbn(String isbn);
    List<Livro> findByAtivoTrue();
    Optional<Livro> findByIdAndAtivoTrue(Long id);

    Page<Livro> findByAtivoTrue(Pageable pageable);

    Optional<Livro> findByIsbnAndAtivoTrue(String isbn);

    Page<Livro> findByTituloContainingIgnoreCaseAndAtivoTrue(String titulo, Pageable pageable);

    @Query("""
            SELECT l FROM Livro l
            WHERE l.ativo = true
              AND (
                LOWER(function('unaccent', l.titulo)) LIKE LOWER(CONCAT('%', function('unaccent', :termo), '%'))
                OR LOWER(function('unaccent', l.autor)) LIKE LOWER(CONCAT('%', function('unaccent', :termo), '%'))
              )
            """)
    Page<Livro> buscarPorTituloOuAutor(@Param("termo") String termo, Pageable pageable);
}
