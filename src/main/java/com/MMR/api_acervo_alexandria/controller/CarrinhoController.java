package com.MMR.api_acervo_alexandria.controller;


import com.MMR.api_acervo_alexandria.dto.CarrinhoRequestDTO;
import com.MMR.api_acervo_alexandria.dto.CarrinhoResponseDTO;
import com.MMR.api_acervo_alexandria.service.CarrinhoService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carrinhos")
public class CarrinhoController {
    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }



    @PostMapping("/itens")
    public ResponseEntity<CarrinhoResponseDTO> adicionarItem(@Valid @RequestBody CarrinhoRequestDTO dto,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        CarrinhoResponseDTO response = carrinhoService.adicionarItem(dto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Passamos o usuarioId na URL provisoriamente até implementarmos a segurança com Tokens
    @GetMapping("/itens")
    public ResponseEntity<List<CarrinhoResponseDTO>> listarCarrinho(@AuthenticationPrincipal UserDetails userDetails) {
        List<CarrinhoResponseDTO> response = carrinhoService.listarCarrinho(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/itens/{livroId}")
    public ResponseEntity<CarrinhoResponseDTO> atualizarQuantidade(
            @PathVariable Long livroId,
            @RequestParam Integer quantidade,
            @AuthenticationPrincipal UserDetails userDetails) {
        CarrinhoResponseDTO response = carrinhoService.atualizarQuantidade(userDetails.getUsername(), livroId, quantidade);        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/itens/{livroId}")
    public ResponseEntity<Void> removerItem(@PathVariable Long livroId, @AuthenticationPrincipal UserDetails userDetails) {
        carrinhoService.removerItem(userDetails.getUsername(), livroId);
        return ResponseEntity.noContent().build();
    }
}
