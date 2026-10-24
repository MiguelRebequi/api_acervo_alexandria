package com.MMR.api_acervo_alexandria.service;

import com.MMR.api_acervo_alexandria.dto.CarrinhoRequestDTO;
import com.MMR.api_acervo_alexandria.dto.CarrinhoResponseDTO;
import com.MMR.api_acervo_alexandria.model.Carrinho;
import com.MMR.api_acervo_alexandria.model.Livro;
import com.MMR.api_acervo_alexandria.model.Usuario;
import com.MMR.api_acervo_alexandria.repository.CarrinhoRepository;
import com.MMR.api_acervo_alexandria.repository.LivroRepository;
import com.MMR.api_acervo_alexandria.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository, LivroRepository livroRepository, UsuarioRepository usuarioRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public CarrinhoResponseDTO adicionarItem(CarrinhoRequestDTO dto, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado ou inativo."));

        Livro livro = livroRepository.findByIdAndAtivoTrue(dto.livroId())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado ou inativo."));

        if (livro.getQuantidadeEstoque() < dto.quantidade()) {
            throw new RuntimeException("Estoque insuficiente para o livro: " + livro.getTitulo());
        }

        Optional<Carrinho> itemExistente = carrinhoRepository.findByUsuarioIdAndLivroId(usuario.getId(), livro.getId());
        Carrinho carrinho;


        if (itemExistente.isPresent()) {
            carrinho = itemExistente.get();
            carrinho.setQuantidade(carrinho.getQuantidade() + dto.quantidade());
        } else {
            carrinho = new Carrinho();
            carrinho.setUsuario(usuario);
            carrinho.setLivro(livro);
            carrinho.setQuantidade(dto.quantidade());
        }

        Carrinho salvo =  carrinhoRepository.save(carrinho);
        BigDecimal subtotal = livro.getPreco().multiply(BigDecimal.valueOf(salvo.getQuantidade()));

        return new CarrinhoResponseDTO(
                salvo.getId(), livro.getId(), livro.getTitulo(),
                salvo.getQuantidade(), livro.getPreco(), subtotal
        );
    }

    public List<CarrinhoResponseDTO> listarCarrinho(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado ou inativo."));

        List<Carrinho> itens = carrinhoRepository.findByUsuarioId(usuario.getId());

        return itens.stream().map(item -> {
            BigDecimal subtotal = item.getLivro().getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));
            return new CarrinhoResponseDTO(
                    item.getId(), item.getLivro().getId(), item.getLivro().getTitulo(),
                    item.getQuantidade(), item.getLivro().getPreco(), subtotal
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public CarrinhoResponseDTO atualizarQuantidade(String emailUsuario, Long livroId, Integer novaQuantidade) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado ou inativo."));

        Carrinho carrinho = carrinhoRepository.findByUsuarioIdAndLivroId(usuario.getId(), livroId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado no carrinho."));

        if(carrinho.getLivro().getQuantidadeEstoque() < novaQuantidade) {
            throw new RuntimeException("Estoque insuficiente para a quantidade solicitada.");
        }
        carrinho.setQuantidade(novaQuantidade);
        Carrinho salvo =  carrinhoRepository.save(carrinho);
        BigDecimal subtotal = salvo.getLivro().getPreco().multiply(BigDecimal.valueOf(salvo.getQuantidade()));

        return new CarrinhoResponseDTO(
                salvo.getId(), salvo.getLivro().getId(), salvo.getLivro().getTitulo(),
                salvo.getQuantidade(), salvo.getLivro().getPreco(), subtotal
        );
    }

    @Transactional
    public void removerItem(String emailUsuario, Long livroId) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado ou inativo."));

        Carrinho carrinho = carrinhoRepository.findByUsuarioIdAndLivroId(usuario.getId(), livroId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado no carrinho."));

        carrinhoRepository.delete(carrinho);
    }
}
