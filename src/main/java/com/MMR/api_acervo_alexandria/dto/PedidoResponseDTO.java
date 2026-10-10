package com.MMR.api_acervo_alexandria.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
        Long id,
        String nomeCliente,
        LocalDateTime dataRealizacao,
        String status,
        BigDecimal valorTotal,
        List<ItemPedidoResponseDTO> itensPedido
) {
}
