package com.MMR.api_acervo_alexandria.service;

import com.MMR.api_acervo_alexandria.dto.LivroRequestDTO;
import com.MMR.api_acervo_alexandria.dto.LivroResponseDTO;
import com.MMR.api_acervo_alexandria.exception.RegraDeNegocioException;
import com.MMR.api_acervo_alexandria.model.Livro;
import com.MMR.api_acervo_alexandria.repository.LivroRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LivroService {

    private final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @Transactional
    public LivroResponseDTO cadastrar(LivroRequestDTO dto) {
        if (livroRepository.existsByIsbn(dto.isbn())) {
            throw new RegraDeNegocioException("Já existe um livro cadastrado com este ISBN.");
        }

        Livro salvo = livroRepository.save(new Livro(dto));

        return new LivroResponseDTO(salvo);
    }
    public Page<LivroResponseDTO> listarTodos(String categoria, BigDecimal precoMin, BigDecimal precoMax, Pageable pageable) {
        boolean semFiltros = (categoria == null || categoria.isBlank()) && precoMin == null && precoMax == null;
        if (semFiltros) {
            return livroRepository.findByAtivoTrue(pageable)
                    .map(LivroResponseDTO::new);
        }

        String categoriaFiltro = (categoria == null) ? "" : categoria.trim();
        BigDecimal min = (precoMin != null) ? precoMin : BigDecimal.ZERO;
        BigDecimal max = (precoMax != null) ? precoMax : new BigDecimal("99999999.99");

        return livroRepository.filtrarLivros(categoriaFiltro, min, max, pageable)
                .map(LivroResponseDTO::new);
    }

    public List<String> listarCategorias() {
        return livroRepository.listarCategoriasAtivas();
    }

    public LivroResponseDTO buscarPorId(Long id) {
        Livro livro = livroRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado para o ID: " + id));

        return new LivroResponseDTO(livro);
    }

    public LivroResponseDTO buscarPorIsbn(String isbn) {
        Livro livro = livroRepository.findByIsbnAndAtivoTrue(isbn)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado para o ISBN: " + isbn));

        return new LivroResponseDTO(livro);
    }

    public Page<LivroResponseDTO> buscarPorTitulo(String titulo, Pageable pageable) {
        return livroRepository.buscarPorTituloOuAutor(titulo, pageable)
                .map(LivroResponseDTO::new);
    }

    @Transactional
    public LivroResponseDTO atualizar(Long id, LivroRequestDTO dto) {
        Livro livro = livroRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado para o ID: " + id));

        // Regra de negócio: se o ISBN enviado for diferente do atual, verificar se já existe
        if (!livro.getIsbn().equals(dto.isbn()) && livroRepository.existsByIsbn(dto.isbn())) {
            throw new RegraDeNegocioException("Já existe outro livro cadastrado com este ISBN.");
        }

        livro.atualizarDados(dto);
        return new LivroResponseDTO(livroRepository.save(livro));
    }

    @Transactional
    public void excluir(Long id) {
        Livro livro = livroRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado para o ID: " + id));

        livro.setAtivo(false); // Exclusão Lógica
        livroRepository.save(livro);
    }

    @Transactional
    public LivroResponseDTO reativar(Long id) {
        Livro livro = livroRepository.findById(id) // Busca pelo ID geral, sem filtrar por ativo=true
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado para o ID: " + id));

        if (Boolean.TRUE.equals(livro.getAtivo())) {
            throw new RegraDeNegocioException("O livro já está ativo no catálogo.");
        }

        livro.setAtivo(true);
        return new LivroResponseDTO(livro);
    }
}
