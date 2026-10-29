package com.MMR.api_acervo_alexandria.controller;

import com.MMR.api_acervo_alexandria.dto.PedidoRequestDTO;
import com.MMR.api_acervo_alexandria.dto.PedidoResponseDTO;
import com.MMR.api_acervo_alexandria.model.StatusPedido;
import com.MMR.api_acervo_alexandria.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> finalizarCompra(@AuthenticationPrincipal UserDetails userDetails) {
        PedidoResponseDTO response = pedidoService.finalizarCompra(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/historico")
    public ResponseEntity<List<PedidoResponseDTO>> listarHistorico(@AuthenticationPrincipal UserDetails userDetails) {
        List<PedidoResponseDTO> response = pedidoService.listarHistorico(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarDetalhes(@PathVariable Long id){
        PedidoResponseDTO response = pedidoService.buscarDetalhes(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(
            @PathVariable Long id, @RequestParam StatusPedido status){
        PedidoResponseDTO response = pedidoService.atualizarStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status-disponiveis")
    public ResponseEntity<StatusPedido[]> listarStatus() {
        // O método .values() do Enum já devolve um array com todas as opções
        return ResponseEntity.ok(StatusPedido.values());
    }
}
