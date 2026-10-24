package com.MMR.api_acervo_alexandria.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CarrinhoRequestDTO(
        @NotNull(message = "O ID do livro é obrigatório.")
        Long livroId,

        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser maior que zero.")
        Integer quantidade
) {
}
