package com.MMR.api_acervo_alexandria.repository;

import com.MMR.api_acervo_alexandria.model.Livro;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

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

    @Query("""
            SELECT l FROM Livro l
            WHERE l.ativo = true
              AND (:categoria = '' OR LOWER(function('unaccent', COALESCE(l.categoria, ''))) = LOWER(function('unaccent', :categoria)))
              AND l.preco >= :precoMin
              AND l.preco <= :precoMax
            """)
    Page<Livro> filtrarLivros(
            @Param("categoria") String categoria,
            @Param("precoMin") BigDecimal precoMin,
            @Param("precoMax") BigDecimal precoMax,
            Pageable pageable
    );

    @Query("SELECT DISTINCT l.categoria FROM Livro l WHERE l.ativo = true AND l.categoria IS NOT NULL ORDER BY l.categoria ASC")
    List<String> listarCategoriasAtivas();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Livro l WHERE l.id = :id AND l.ativo = true")
    Optional<Livro> findByIdAndAtivoTrueComTrava(@Param("id") Long id);
}
