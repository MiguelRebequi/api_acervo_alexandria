package com.MMR.api_acervo_alexandria.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MetodoPagamento {
    CREDIT_CARD,
    PIX,
    DIGITAL_WALLET,
    PAYPAL,
    CRYPTO,
    BOLETO;

    @JsonCreator
    public static MetodoPagamento fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String normalizado = valor.trim().toUpperCase();
        if ("CARTAO_CREDITO".equals(normalizado) || "CARTÃO DE CRÉDITO".equals(normalizado)) {
            return CREDIT_CARD;
        }
        return MetodoPagamento.valueOf(normalizado);
    }
}