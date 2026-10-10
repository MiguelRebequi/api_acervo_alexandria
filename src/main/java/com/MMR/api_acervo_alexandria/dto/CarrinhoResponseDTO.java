package com.MMR.api_acervo_alexandria.dto;

import java.math.BigDecimal;

public record CarrinhoResponseDTO(
        Long id,
        Long livroId,
        String tituloLivro,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {
}
