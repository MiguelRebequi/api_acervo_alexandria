package com.MMR.api_acervo_alexandria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaRequestDTO(
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        String email,

        @NotBlank(message = "O CPF é obrigatório para redefinir a senha.")
        String cpf,

        @NotBlank(message = "A nova senha é obrigatória.")
        @Size(min = 6, max = 72, message = "A nova senha deve ter entre 6 e 72 caracteres.")
        String novaSenha
) {


}
