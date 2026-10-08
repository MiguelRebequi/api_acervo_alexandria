package com.MMR.api_acervo_alexandria.dto;

public record ItemPedidoResponseDTO(
        Long id,
        String tituloLivro,
        Integer quantidade,
        Double precoUnitario
) {
}
