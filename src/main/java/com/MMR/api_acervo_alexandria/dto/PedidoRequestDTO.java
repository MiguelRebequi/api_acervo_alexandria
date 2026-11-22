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
            @NotNull(message = "O método de entrega é obrigatório.")
            MetodoEntrega metodo,

            @NotBlank(message = "O nome do destinatário é obrigatório.")
            @Size(max = 74, message = "O nome deve ter no máximo 74 caracteres.")
            String nome,

            @NotBlank(message = "O sobrenome do destinatário é obrigatório.")
            @Size(max = 75, message = "O sobrenome deve ter no máximo 75 caracteres.")
            String sobrenome,

            @NotBlank(message = "O e-mail de entrega é obrigatório.")
            @Email(message = "Formato de e-mail inválido.")
            @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
            String email,

            @NotBlank(message = "O país é obrigatório.")
            @Size(max = 10, message = "O país deve ter no máximo 10 caracteres.")
            String pais,

            @NotBlank(message = "O CEP é obrigatório.")
            @Size(max = 20, message = "O CEP deve ter no máximo 20 caracteres.")
            String cep,

            @NotBlank(message = "O estado é obrigatório.")
            @Size(max = 50, message = "O estado deve ter no máximo 50 caracteres.")
            String estado,

            @NotBlank(message = "A cidade é obrigatória.")
            @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres.")
            String cidade,

            @NotBlank(message = "O endereço é obrigatório.")
            @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres.")
            String endereco,

            @NotBlank(message = "O número é obrigatório.")
            @Size(max = 20, message = "O número deve ter no máximo 20 caracteres.")
            String numero,

            @Size(max = 100, message = "O complemento deve ter no máximo 100 caracteres.")
            String complemento,

            @Size(max = 255, message = "As observações devem ter no máximo 255 caracteres.")
            String observacoes
    ) {}

    public record PagamentoRequestDTO(
            @NotNull(message = "O método de pagamento é obrigatório.")
            MetodoPagamento metodo,

            @NotNull(message = "O número de parcelas é obrigatório.")
            @Min(value = 1, message = "Mínimo de 1 parcela.")
            @Max(value = 4, message = "Máximo de 4 parcelas.")
            Integer parcelas
    ) {}
}