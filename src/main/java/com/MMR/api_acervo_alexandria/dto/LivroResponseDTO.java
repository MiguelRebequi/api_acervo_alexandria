package com.MMR.api_acervo_alexandria.dto;

import java.math.BigDecimal;

public record LivroResponseDTO(Long id,
                               String titulo,
                               String autor,
                               String isbn,
                               BigDecimal preco,
                               Integer quantidadeEstoque,
                               Integer anoPublicacao) {
}
