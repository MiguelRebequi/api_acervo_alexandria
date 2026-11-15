package com.MMR.api_acervo_alexandria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaRequestDTO(
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        String email,

        String cpf, // Opcional: Se enviado pelo front-end, valida titularidade

        @NotBlank(message = "A nova senha é obrigatória.")
        @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres.")
        String novaSenha
) {


}
