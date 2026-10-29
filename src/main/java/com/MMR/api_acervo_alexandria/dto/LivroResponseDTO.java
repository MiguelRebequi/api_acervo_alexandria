package com.MMR.api_acervo_alexandria.dto;

import com.MMR.api_acervo_alexandria.model.Livro;

import java.math.BigDecimal;

public record LivroResponseDTO(Long id,
                               String titulo,
                               String autor,
                               String isbn,
                               BigDecimal preco,
                               Integer quantidadeEstoque,
                               Integer anoPublicacao) {
    public LivroResponseDTO(Livro livro) {
        this(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getPreco(),
                livro.getQuantidadeEstoque(),
                livro.getAnoPublicacao()
        );
    }
}
