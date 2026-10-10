package com.MMR.api_acervo_alexandria.service;

import com.MMR.api_acervo_alexandria.dto.LivroRequestDTO;
import com.MMR.api_acervo_alexandria.dto.LivroResponseDTO;
import com.MMR.api_acervo_alexandria.model.Livro;
import com.MMR.api_acervo_alexandria.repository.LivroRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

        Livro salvo = livroRepository.save(livro);

        return new LivroResponseDTO(
                salvo.getId(), salvo.getTitulo(), salvo.getAutor(),
                salvo.getIsbn(), salvo.getPreco(), salvo.getQuantidadeEstoque()
        );


    }
    public List<LivroResponseDTO> listarTodos() {
        return livroRepository.findByAtivoTrue().stream()
                .map(livro -> new LivroResponseDTO(
                        livro.getId(), livro.getTitulo(), livro.getAutor(),
                        livro.getIsbn(), livro.getPreco(), livro.getQuantidadeEstoque()
                ))
                .collect(Collectors.toList());
    }

    public LivroResponseDTO buscarPorId(Long id) {
        Livro livro = livroRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado para o ID: " + id));

        return new LivroResponseDTO(
                livro.getId(), livro.getTitulo(), livro.getAutor(),
                livro.getIsbn(), livro.getPreco(), livro.getQuantidadeEstoque()
        );
    }

    @Transactional
    public LivroResponseDTO atualizar(Long id, LivroRequestDTO dto) {
        Livro livro = livroRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado para o ID: " + id));

        // Regra de negócio: se o ISBN enviado for diferente do atual, verificar se já existe
        if (!livro.getIsbn().equals(dto.isbn()) && livroRepository.existsByIsbn(dto.isbn())) {
            throw new RuntimeException("Já existe outro livro cadastrado com este ISBN.");
        }

        livro.setTitulo(dto.titulo());
        livro.setAutor(dto.autor());
        livro.setIsbn(dto.isbn());
        livro.setPreco(dto.preco());
        livro.setQuantidadeEstoque(dto.quantidadeEstoque());

        Livro salvo = livroRepository.save(livro);

        return new LivroResponseDTO(
                salvo.getId(), salvo.getTitulo(), salvo.getAutor(),
                salvo.getIsbn(), salvo.getPreco(), salvo.getQuantidadeEstoque()
        );
    }

    @Transactional
    public void excluir(Long id) {
        Livro livro = livroRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado para o ID: " + id));

        livro.setAtivo(false); // Exclusão Lógica
        livroRepository.save(livro);
    }
}
