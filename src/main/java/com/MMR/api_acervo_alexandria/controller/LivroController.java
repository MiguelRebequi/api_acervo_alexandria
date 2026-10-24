package com.MMR.api_acervo_alexandria.controller;

import com.MMR.api_acervo_alexandria.dto.LivroRequestDTO;
import com.MMR.api_acervo_alexandria.dto.LivroResponseDTO;
import com.MMR.api_acervo_alexandria.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livros")
public class LivroController {
    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @PostMapping
    public ResponseEntity<LivroResponseDTO> cadastrar(@Valid @RequestBody LivroRequestDTO dto){
        LivroResponseDTO response = livroService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<Page<LivroResponseDTO>> listarTodos(@org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable) {
        Page<LivroResponseDTO> response = livroService.listarTodos(pageable);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> buscarPorId(@PathVariable Long id){
        LivroResponseDTO response = livroService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<LivroResponseDTO> buscarPorIsbn(@PathVariable String isbn) {
        LivroResponseDTO response = livroService.buscarPorIsbn(isbn);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/busca")
    public ResponseEntity<Page<LivroResponseDTO>> buscarPorTitulo(
            @RequestParam String titulo,
            @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable) {

        Page<LivroResponseDTO> response = livroService.buscarPorTitulo(titulo, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody LivroRequestDTO dto){
        LivroResponseDTO response = livroService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> excluir(@PathVariable Long id){
        livroService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
