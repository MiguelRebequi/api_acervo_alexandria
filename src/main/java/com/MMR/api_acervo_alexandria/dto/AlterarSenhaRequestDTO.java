package com.MMR.api_acervo_alexandria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaRequestDTO(
        @NotBlank(message = "A senha atual é obrigatória.")
        @Size(max = 72, message = "A senha atual deve ter no máximo 72 caracteres.")
        String senhaAtual,

        @NotBlank(message = "A nova senha é obrigatória.")
        @Size(min = 6, max = 72, message = "A nova senha deve ter entre 6 e 72 caracteres.")
        String novaSenha
) {}