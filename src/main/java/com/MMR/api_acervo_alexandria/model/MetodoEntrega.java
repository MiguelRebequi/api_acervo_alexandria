package com.MMR.api_acervo_alexandria.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MetodoEntrega {
    STANDARD,
    EXPRESS,
    RETIRADA;

    @JsonCreator
    public static MetodoEntrega fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String normalizado = valor.trim().toUpperCase();
        return switch (normalizado) {
            case "PADRAO", "PADRÃO" -> STANDARD;
            case "EXPRESSO" -> EXPRESS;
            default -> MetodoEntrega.valueOf(normalizado);
        };
    }
}