package com.MMR.api_acervo_alexandria.dto;

public record LivroResponseDTO(Long id,
                               String titulo,
                               String autor,
                               String isbn,
                               Double preco,
                               Integer quantidadeEstoque) {
}
