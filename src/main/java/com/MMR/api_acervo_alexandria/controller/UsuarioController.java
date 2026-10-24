package com.MMR.api_acervo_alexandria.controller;

import com.MMR.api_acervo_alexandria.dto.LivroRequestDTO;
import com.MMR.api_acervo_alexandria.dto.UsuarioRequestDTO;
import com.MMR.api_acervo_alexandria.dto.UsuarioResponseDTO;
import com.MMR.api_acervo_alexandria.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos(){
        List<UsuarioResponseDTO> response = usuarioService.listarTodos();
        return ResponseEntity.ok(response);
    }


    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody UsuarioRequestDTO dto){
        UsuarioResponseDTO response = usuarioService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id){
        UsuarioResponseDTO response = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> excluir(@PathVariable Long id){
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}
