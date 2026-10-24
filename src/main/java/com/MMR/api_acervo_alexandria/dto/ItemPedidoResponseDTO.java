package com.MMR.api_acervo_alexandria.dto;

import java.math.BigDecimal;

public record ItemPedidoResponseDTO(
        Long id,
        String tituloLivro,
        Integer quantidade,
        BigDecimal precoUnitario
) {
}
