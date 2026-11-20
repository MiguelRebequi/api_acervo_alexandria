package com.MMR.api_acervo_alexandria.dto;

import com.MMR.api_acervo_alexandria.model.FormatoLivro;
import com.MMR.api_acervo_alexandria.model.Livro;

import java.math.BigDecimal;

public record LivroResponseDTO(Long id,
                               String titulo,
                               String autor,
                               String isbn,
                               String editora,
                               String idioma,
                               FormatoLivro formato,
                               BigDecimal avaliacao,
                               BigDecimal preco,
                               Integer quantidadeEstoque,
                               Integer anoPublicacao,
                               String urlCapa,
                               String sinopse,
                               String categoria) {
    public LivroResponseDTO(Livro livro) {
        this(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getEditora(),
                livro.getIdioma(),
                livro.getFormato(),
                livro.getAvaliacao(),
                livro.getPreco(),
                livro.getQuantidadeEstoque(),
                livro.getAnoPublicacao(),
                livro.getUrlCapa(),
                livro.getSinopse(),
                livro.getCategoria()
        );
    }
}
