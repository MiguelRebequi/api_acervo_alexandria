package com.MMR.api_acervo_alexandria.dto;

import com.MMR.api_acervo_alexandria.model.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
        Long id,
        String nomeCliente,
        LocalDateTime dataRealizacao,
        StatusPedido status,
        BigDecimal subtotal,
        BigDecimal valorFrete,
        BigDecimal valorDesconto,
        BigDecimal valorTotal,
        String cupom,
        String metodoEntrega,
        String enderecoCompleto,
        String metodoPagamento,
        Integer parcelas,
        List<ItemPedidoResponseDTO> itensPedido
) {}