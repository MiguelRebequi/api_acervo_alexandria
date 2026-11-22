package com.MMR.api_acervo_alexandria.security;

import com.MMR.api_acervo_alexandria.exception.TooManyRequestsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class RateLimitService {

    private static final int MAX_TENTATIVAS = 5;
    private static final long JANELA_SEGUNDOS = 15 * 60; // 15 minutos

    private final ConcurrentHashMap<String, Deque<Instant>> tentativas = new ConcurrentHashMap<>();

    public void verificarLimite(String chave) {
        verificarLimite(chave, MAX_TENTATIVAS);
    }

    public void verificarLimite(String chave, int maxTentativas) {
        Instant agora = Instant.now();
        Instant limiteJanela = agora.minusSeconds(JANELA_SEGUNDOS);

        Deque<Instant> historico = tentativas.computeIfAbsent(chave, k -> new ConcurrentLinkedDeque<>());

        synchronized (historico) {
            while (!historico.isEmpty() && historico.peekFirst().isBefore(limiteJanela)) {
                historico.pollFirst();
            }

            if (historico.size() >= maxTentativas) {
                throw new TooManyRequestsException("Muitas tentativas seguidas. Aguarde 15 minutos e tente novamente.");
            }

            historico.addLast(agora);
        }
    }

    public void limparTentativas(String chave) {
        tentativas.remove(chave);
    }
}

