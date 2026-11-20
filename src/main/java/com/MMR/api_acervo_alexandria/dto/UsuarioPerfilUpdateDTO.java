package com.MMR.api_acervo_alexandria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioPerfilUpdateDTO(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 100, message = "O nome deve conter no máximo 100 caracteres.")
        String nome,

        @Size(max = 20, message = "O telefone fixo deve ter no máximo 20 caracteres.")
        String telefoneFixo,

        @NotBlank(message = "O telefone móvel é obrigatório.")
        @Size(max = 20, message = "O telefone móvel deve ter no máximo 20 caracteres.")
        String telefoneMovel,

        @NotBlank(message = "O endereço é obrigatório.")
        @Size(max = 255, message = "O endereço deve conter no máximo 255 caracteres.")
        String endereco
) {}