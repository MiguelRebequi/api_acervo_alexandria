package com.MMR.api_acervo_alexandria.controller;

import com.MMR.api_acervo_alexandria.dto.PedidoRequestDTO;
import com.MMR.api_acervo_alexandria.dto.PedidoResponseDTO;
import com.MMR.api_acervo_alexandria.model.StatusPedido;
import com.MMR.api_acervo_alexandria.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PedidoResponseDTO> finalizarCompra(@Valid @RequestBody PedidoRequestDTO dto) {
        PedidoResponseDTO response = pedidoService.finalizarCompra(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarHistorico(@PathVariable Long usuarioId) {
        List<PedidoResponseDTO> response = pedidoService.listarHistorico(usuarioId);
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
}
