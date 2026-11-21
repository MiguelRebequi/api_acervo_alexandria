package com.MMR.api_acervo_alexandria.dto;

import com.MMR.api_acervo_alexandria.model.MetodoEntrega;
import com.MMR.api_acervo_alexandria.model.MetodoPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record PedidoRequestDTO(
        @Valid
        List<ItemPedidoRequestDTO> itens,

        @NotNull(message = "Os dados de entrega são obrigatórios.")
        @Valid
        EntregaRequestDTO entrega,

        @NotNull(message = "Os dados de pagamento são obrigatórios.")
        @Valid
        PagamentoRequestDTO pagamento,

        @Size(max = 50, message = "O cupom deve ter no máximo 50 caracteres.")
        String cupom
) {
    public record ItemPedidoRequestDTO(
            @NotNull(message = "O ID do livro é obrigatório.")
            Long livroId,

            @NotNull(message = "A quantidade é obrigatória.")
            @Min(value = 1, message = "A quantidade mínima é 1.")
            Integer quantidade
    ) {}

    public record EntregaRequestDTO(
            @NotBlank(message = "O método de entrega é obrigatório.")
            MetodoEntrega metodo,

            @NotBlank(message = "O nome do destinatário é obrigatório.")
            String nome,

            @NotBlank(message = "O sobrenome do destinatário é obrigatório.")
            String sobrenome,

            @NotBlank(message = "O e-mail de entrega é obrigatório.")
            @Email(message = "Formato de e-mail inválido.")
            String email,

            @NotBlank(message = "O país é obrigatório.")
            String pais,

            @NotBlank(message = "O CEP é obrigatório.")
            String cep,

            @NotBlank(message = "O estado é obrigatório.")
            String estado,

            @NotBlank(message = "A cidade é obrigatória.")
            String cidade,

            @NotBlank(message = "O endereço é obrigatório.")
            String endereco,

            @NotBlank(message = "O número é obrigatório.")
            String numero,

            String complemento,
            String observacoes
    ) {}

    public record PagamentoRequestDTO(
            @NotBlank(message = "O método de pagamento é obrigatório.")
            MetodoPagamento metodo,

            @NotNull(message = "O número de parcelas é obrigatório.")
            @Min(value = 1, message = "Mínimo de 1 parcela.")
            @Max(value = 4, message = "Máximo de 4 parcelas.")
            Integer parcelas
    ) {}
}