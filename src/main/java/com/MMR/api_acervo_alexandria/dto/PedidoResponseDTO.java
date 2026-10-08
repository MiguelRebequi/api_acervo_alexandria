package com.MMR.api_acervo_alexandria.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
        Long id,
        String nomeCliente,
        LocalDateTime dataRealizacao,
        String status,
        Double valorTotal,
        List<ItemPedidoResponseDTO> itensPedido
) {
}
