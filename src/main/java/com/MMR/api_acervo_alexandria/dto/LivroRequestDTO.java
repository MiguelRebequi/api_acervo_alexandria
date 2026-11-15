package com.MMR.api_acervo_alexandria.dto;

import com.MMR.api_acervo_alexandria.model.FormatoLivro;
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

        @NotBlank(message = "A editora é obrigatória.")
        String editora,

        @NotBlank(message = "O idioma é obrigatório.")
        String idioma,

        @NotNull(message = "O formato do livro é obrigatório.")
        FormatoLivro formato,

        @DecimalMin(value = "0.0", message = "A avaliação mínima é 0.0.")
        @DecimalMax(value = "5.0", message = "A avaliação máxima é 5.0.")
        BigDecimal avaliacao,

        @NotNull(message = "O preço é obrigatório.")
        @Positive(message = "O preço deve ser maior que zero.")
        BigDecimal preco,

        @NotNull(message = "A quantidade em estoque é obrigatório.")
        @Min(value = 0, message = "O estoque não pode ser negativo.")
        Integer quantidadeEstoque,

        @NotNull(message = "O ano de publicação é obrigatório.")
        Integer anoPublicacao // Vou ignorar data miníma.

) {
}
