package com.MMR.api_acervo_alexandria.service;

import com.MMR.api_acervo_alexandria.dto.ItemPedidoResponseDTO;
import com.MMR.api_acervo_alexandria.dto.PedidoResponseDTO;
import com.MMR.api_acervo_alexandria.model.*;
import com.MMR.api_acervo_alexandria.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;

    public PedidoService(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository,
                         CarrinhoRepository carrinhoRepository, UsuarioRepository usuarioRepository,
                         LivroRepository livroRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.usuarioRepository = usuarioRepository;
        this.livroRepository = livroRepository;
    }

    @Transactional
    public PedidoResponseDTO finalizarCompra(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado ou inativo."));

        List<Carrinho> itensCarrinho = carrinhoRepository.findByUsuarioId(usuario.getId());
        if (itensCarrinho.isEmpty()) {
            throw new RuntimeException("O carrinho está vazio.");
        }

        BigDecimal valorTotal = BigDecimal.ZERO;
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PROCESSANDO);

        pedido.setValorTotal(BigDecimal.ZERO);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        List<ItemPedidoResponseDTO> itensResponse = new ArrayList<>();

        for (Carrinho item : itensCarrinho) {
            Livro livro = item.getLivro();

            // Validação dupla: O livro precisa estar ativo e ter estoque suficiente
            if (!livro.getAtivo() || livro.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new RuntimeException("O livro " + livro.getTitulo() + " está indisponível ou sem estoque suficiente.");
            }

            livro.setQuantidadeEstoque(livro.getQuantidadeEstoque() - item.getQuantidade());
            livroRepository.save(livro);

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedidoSalvo);
            itemPedido.setLivro(livro);
            itemPedido.setQuantidade(item.getQuantidade());
            itemPedido.setPrecoUnitario(livro.getPreco());
            itemPedidoRepository.save(itemPedido);

            BigDecimal subtotalItem = livro.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));
            valorTotal = valorTotal.add(subtotalItem);

            itensResponse.add(new ItemPedidoResponseDTO(
                    itemPedido.getId(), livro.getTitulo(), item.getQuantidade(), livro.getPreco()
            ));
        }

        pedidoSalvo.setValorTotal(valorTotal);
        pedidoRepository.save(pedidoSalvo);

        // Ao invés de deletar um a um, passamos a lista inteira para o repository limpar o carrinho
        carrinhoRepository.deleteAll(itensCarrinho);

        return new PedidoResponseDTO(
                pedidoSalvo.getId(), usuario.getNome(), pedidoSalvo.getDataPedido(),
                pedidoSalvo.getStatus(), pedidoSalvo.getValorTotal(), itensResponse
        );
    }

    public List<PedidoResponseDTO> listarHistorico(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado ou inativo."));

        List<Pedido> pedidos = pedidoRepository.findByUsuarioId(usuario.getId());

        return pedidos.stream().map(pedido -> {
            List<ItemPedidoResponseDTO> itens = buscarItensDoPedido(pedido.getId());

            return new PedidoResponseDTO(
                    pedido.getId(), pedido.getUsuario().getNome(), pedido.getDataPedido(),
                    pedido.getStatus(), pedido.getValorTotal(), itens
            );
        }).collect(Collectors.toList());
    }

    public PedidoResponseDTO buscarDetalhes(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado."));

        List<ItemPedidoResponseDTO> itens = buscarItensDoPedido(pedido.getId());

        return new PedidoResponseDTO(
                pedido.getId(), pedido.getUsuario().getNome(), pedido.getDataPedido(),
                pedido.getStatus(), pedido.getValorTotal(), itens
        );
    }

    @Transactional
    public PedidoResponseDTO atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado."));

        pedido.setStatus(novoStatus);
        Pedido salvo = pedidoRepository.save(pedido);

        List<ItemPedidoResponseDTO> itens = buscarItensDoPedido(salvo.getId());

        return new PedidoResponseDTO(
                salvo.getId(), salvo.getUsuario().getNome(), salvo.getDataPedido(),
                salvo.getStatus(), salvo.getValorTotal(), itens
        );
    }

    private List<ItemPedidoResponseDTO> buscarItensDoPedido(Long pedidoId) {
        return itemPedidoRepository.findByPedidoId(pedidoId).stream()
                .map(item -> new ItemPedidoResponseDTO(
                        item.getId(), item.getLivro().getTitulo(),
                        item.getQuantidade(), item.getPrecoUnitario()
                )).collect(Collectors.toList());
    }
}
