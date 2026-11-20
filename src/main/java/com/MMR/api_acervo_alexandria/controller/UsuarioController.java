package com.MMR.api_acervo_alexandria.controller;

import com.MMR.api_acervo_alexandria.dto.AlterarSenhaRequestDTO;
import com.MMR.api_acervo_alexandria.dto.UsuarioPerfilUpdateDTO;
import com.MMR.api_acervo_alexandria.dto.UsuarioRequestDTO;
import com.MMR.api_acervo_alexandria.dto.UsuarioResponseDTO;
import com.MMR.api_acervo_alexandria.model.Usuario;
import com.MMR.api_acervo_alexandria.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

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

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<UsuarioResponseDTO> reativar(@PathVariable Long id) {
        usuarioService.reativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<Void> promoverParaAdmin(@PathVariable Long id){
        usuarioService.promoverParaAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> buscarMeuPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(usuarioService.buscarPerfil(userDetails.getUsername()));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> atualizarMeuPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UsuarioPerfilUpdateDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizarPerfil(userDetails.getUsername(), dto));
    }

    @PatchMapping("/me/senha")
    public ResponseEntity<Void> alterarMinhaSenha(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AlterarSenhaRequestDTO dto) {
        usuarioService.alterarSenha(userDetails.getUsername(), dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/me/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadMinhaFoto(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("foto") MultipartFile arquivo) {
        usuarioService.atualizarFoto(userDetails.getUsername(), arquivo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/foto")
    public ResponseEntity<byte[]> buscarMinhaFoto(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioService.buscarEntidadeComFoto(userDetails.getUsername());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(usuario.getFotoTipo()))
                .body(usuario.getFoto());
    }

}
