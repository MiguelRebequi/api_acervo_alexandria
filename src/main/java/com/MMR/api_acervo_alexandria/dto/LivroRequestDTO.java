package com.MMR.api_acervo_alexandria.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record LivroRequestDTO(
        @NotBlank
        @Size(max = 200, message = "O título deve ter no máximo 200 caracteres.")
        String titulo,

        @NotBlank(message = "O autor é obrigatório.")
        String autor,

        @NotBlank(message = "O ISBN é obrigatório.")
        String isbn,

        @NotNull(message = "O preço é obrigatório.")
        @Positive(message = "O preço deve ser maior que zero.")
        BigDecimal preco,

        @NotNull(message = "A quantidade em estoque é obrigatório.")
        @Min(value = 0, message = "O estoque não pode ser negativo.")
        Integer quantidadeEstoque

) {
}
