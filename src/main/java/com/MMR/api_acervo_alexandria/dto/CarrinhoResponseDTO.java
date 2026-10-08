package com.MMR.api_acervo_alexandria.dto;

public record CarrinhoResponseDTO(
        Long id,
        Long livroId,
        String tituloLivro,
        Integer quantidade,
        Double precoUnitario,
        Double subtotal
) {
}
