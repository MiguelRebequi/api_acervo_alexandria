package com.MMR.api_acervo_alexandria.service;

import com.MMR.api_acervo_alexandria.dto.LivroRequestDTO;
import com.MMR.api_acervo_alexandria.dto.LivroResponseDTO;
import com.MMR.api_acervo_alexandria.model.Livro;
import com.MMR.api_acervo_alexandria.repository.LivroRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class LivroService {

    private final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @Transactional
    public LivroResponseDTO cadastrar(LivroRequestDTO dto) {
        if (livroRepository.existsByIsbn(dto.isbn())) {
            throw new RuntimeException("Já existe um livro cadastrado com este ISBN.");
        }

        Livro livro = new Livro();
        livro.setTitulo(dto.titulo());
        livro.setAutor(dto.autor());
        livro.setIsbn(dto.isbn());
        livro.setPreco(dto.preco());
        livro.setQuantidadeEstoque(dto.quantidadeEstoque());
        livro.setAnoPublicacao(dto.anoPublicacao());

        Livro salvo = livroRepository.save(livro);

        return new LivroResponseDTO(salvo);


    }
    public Page<LivroResponseDTO> listarTodos(Pageable pageable) {
        return livroRepository.findByAtivoTrue(pageable)
                .map(livro -> new LivroResponseDTO(
                        livro.getId(), livro.getTitulo(), livro.getAutor(),
                        livro.getIsbn(), livro.getPreco(), livro.getQuantidadeEstoque(), livro.getAnoPublicacao()
                ));
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
        return livroRepository.findByTituloContainingIgnoreCaseAndAtivoTrue(titulo, pageable)
                .map(livro -> new LivroResponseDTO(
                        livro.getId(), livro.getTitulo(), livro.getAutor(),
                        livro.getIsbn(), livro.getPreco(), livro.getQuantidadeEstoque(),  livro.getAnoPublicacao()
                ));
    }

    @Transactional
    public LivroResponseDTO atualizar(Long id, LivroRequestDTO dto) {
        Livro livro = livroRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado para o ID: " + id));

        // Regra de negócio: se o ISBN enviado for diferente do atual, verificar se já existe
        if (!livro.getIsbn().equals(dto.isbn()) && livroRepository.existsByIsbn(dto.isbn())) {
            throw new RuntimeException("Já existe outro livro cadastrado com este ISBN.");
        }

        livro.setTitulo(dto.titulo());
        livro.setAutor(dto.autor());
        livro.setIsbn(dto.isbn());
        livro.setPreco(dto.preco());
        livro.setQuantidadeEstoque(dto.quantidadeEstoque());
        livro.setAnoPublicacao(dto.anoPublicacao());

        Livro salvo = livroRepository.save(livro);

        return new LivroResponseDTO(salvo);
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
            throw new RuntimeException("O livro já está ativo no catálogo.");
        }

        livro.setAtivo(true);
        return new LivroResponseDTO(livro);
    }
}
