package com.MMR.api_acervo_alexandria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record UsuarioRequestDTO(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150, message = "O nome deve conter no máximo 150 caracteres.")
        String nome,

        @NotBlank(message = "O CPF é obrigatório.")
        @CPF(message = "O CPF informado é inválido.")
        String cpf,


        @Size(max = 20, message = "O telefone fixo deve ter no máximo 20 caracteres.")
        String telefoneFixo,

        @NotBlank(message = "O telefone móvel é obrigatório.")
        @Size(max = 20, message = "O telefone móvel deve ter no máximo 20 caracteres.")
        String telefoneMovel,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        @Size(max = 255, message = "O e-mail deve conter no máximo 255 caracteres.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
        String senha,

        @NotBlank(message = "O endereço é obrigatório.")
        @Size(max = 255, message = "O endereço deve conter no máximo 255 caracteres.")
                String endereco
) {}
