package com.MMR.api_acervo_alexandria.dto;

import jakarta.validation.constraints.NotNull;

public record PedidoRequestDTO(
        @NotNull(message = "O ID do usuário é obrigatório para finalizar a compra.")
        Long usuarioId
) {
}
