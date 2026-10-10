package com.MMR.api_acervo_alexandria.controller;


import com.MMR.api_acervo_alexandria.dto.CarrinhoRequestDTO;
import com.MMR.api_acervo_alexandria.dto.CarrinhoResponseDTO;
import com.MMR.api_acervo_alexandria.service.CarrinhoService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carrinho")
public class CarrinhoController {
    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @PostMapping("/itens")
    public ResponseEntity<CarrinhoResponseDTO> adicionarItem(@Valid @RequestBody CarrinhoRequestDTO dto){
        CarrinhoResponseDTO response = carrinhoService.adicionarItem(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Passamos o usuarioId na URL provisoriamente até implementarmos a segurança com Tokens
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CarrinhoResponseDTO>> listarCarrinho(@PathVariable Long usuarioId) {
        List<CarrinhoResponseDTO> response = carrinhoService.listarCarrinho(usuarioId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/usuario/{usuarioId}/itens/{livroId}")
    public ResponseEntity<CarrinhoResponseDTO> atualizarQuantidade(
            @PathVariable Long usuarioId,
            @PathVariable Long livroId,
            @RequestParam Integer quantidade) {
        CarrinhoResponseDTO response = carrinhoService.atualizarQuantidade(usuarioId, livroId, quantidade);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/usuario/{usuarioId}/itens/{livroId}")
    public ResponseEntity<Void> removerItem(@PathVariable Long usuarioId, @PathVariable Long livroId) {
        carrinhoService.removerItem(usuarioId, livroId);
        return ResponseEntity.noContent().build();
    }
}
