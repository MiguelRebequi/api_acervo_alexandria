package com.MMR.api_acervo_alexandria.service;

import com.MMR.api_acervo_alexandria.dto.ItemPedidoResponseDTO;
import com.MMR.api_acervo_alexandria.dto.PedidoRequestDTO;
import com.MMR.api_acervo_alexandria.dto.PedidoResponseDTO;
import com.MMR.api_acervo_alexandria.exception.RegraDeNegocioException;
import com.MMR.api_acervo_alexandria.model.*;
import com.MMR.api_acervo_alexandria.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private static final Set<String> CUPONS_VALIDOS = Set.of(
            "ALEXANDRIA10", "BEMVINDO20"
    );

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
    public PedidoResponseDTO finalizarCompra(String emailUsuario, PedidoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(emailUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado ou inativo."));

        String cupomValidado = validarENormalizarCupom(dto.cupom());

        boolean usarItensDoJson = dto.itens() != null && !dto.itens().isEmpty();
        List<Carrinho> itensCarrinho = carrinhoRepository.findByUsuarioId(usuario.getId());

        if (!usarItensDoJson && itensCarrinho.isEmpty()) {
            throw new RegraDeNegocioException("O pedido não possui itens e o carrinho está vazio.");
        }

        Pedido novoPedido = new Pedido(usuario, dto);
        novoPedido.setCupom(cupomValidado);
        Pedido pedidoSalvo = pedidoRepository.save(novoPedido);

        List<ItemPedidoResponseDTO> itensResponse = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        if (usarItensDoJson) {
            for (PedidoRequestDTO.ItemPedidoRequestDTO itemDTO : dto.itens()) {
                Livro livro = livroRepository.findByIdAndAtivoTrueComTrava(itemDTO.livroId())
                        .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado ou inativo: " + itemDTO.livroId()));
                subtotal = processarItemPedido(pedidoSalvo, livro, itemDTO.quantidade(), subtotal, itensResponse);
            }
        } else {
            for (Carrinho item : itensCarrinho) {
                Livro livro = livroRepository.findByIdAndAtivoTrueComTrava(item.getLivro().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado ou inativo: " + item.getLivro().getId()));
                subtotal = processarItemPedido(pedidoSalvo, livro, item.getQuantidade(), subtotal, itensResponse);
            }
        }

        BigDecimal valorFrete = calcularFrete(pedidoSalvo.getMetodoEntrega());
        BigDecimal descontoCupom = calcularDescontoCupom(subtotal, cupomValidado);
        BigDecimal subtotalComCupom = subtotal.subtract(descontoCupom).max(BigDecimal.ZERO);
        BigDecimal totalBase = subtotalComCupom.add(valorFrete);

        BigDecimal ajustePagamento = BigDecimal.ZERO;
        if ("PIX".equals(pedidoSalvo.getMetodoPagamento())) {
            ajustePagamento = subtotalComCupom.multiply(new BigDecimal("-0.05")).setScale(2, RoundingMode.HALF_UP);
        } else if ("CRYPTO".equals(pedidoSalvo.getMetodoPagamento())) {
            ajustePagamento = totalBase.multiply(new BigDecimal("0.02")).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal valorDescontoTotal = descontoCupom.subtract(ajustePagamento).max(BigDecimal.ZERO);
        BigDecimal valorTotal = totalBase.add(ajustePagamento).max(BigDecimal.ZERO);

        pedidoSalvo.setSubtotal(subtotal);
        pedidoSalvo.setValorFrete(valorFrete);
        pedidoSalvo.setValorDesconto(valorDescontoTotal);
        pedidoSalvo.setValorTotal(valorTotal);
        pedidoRepository.save(pedidoSalvo);

        if (!itensCarrinho.isEmpty()) {
            carrinhoRepository.deleteAll(itensCarrinho);
        }

        return montarResponse(pedidoSalvo, itensResponse);
    }

    public List<PedidoResponseDTO> listarHistorico(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(emailUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado ou inativo."));

        return pedidoRepository.findByUsuarioId(usuario.getId()).stream()
                .map(pedido -> montarResponse(pedido, buscarItensDoPedido(pedido.getId())))
                .collect(Collectors.toList());
    }

    public PedidoResponseDTO buscarDetalhes(Long pedidoId, UserDetails userDetails) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado."));

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (!isAdmin && !pedido.getUsuario().getEmail().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("Acesso negado: você só pode visualizar os detalhes dos seus próprios pedidos.");
        }

        return montarResponse(pedido, buscarItensDoPedido(pedido.getId()));
    }

    @Transactional
    public PedidoResponseDTO atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado."));

        pedido.setStatus(novoStatus);
        Pedido salvo = pedidoRepository.save(pedido);

        return montarResponse(salvo, buscarItensDoPedido(salvo.getId()));
    }

    private BigDecimal processarItemPedido(Pedido pedidoSalvo, Livro livro, int quantidade,
                                           BigDecimal subtotalAtual, List<ItemPedidoResponseDTO> itensResponse) {
        if (!Boolean.TRUE.equals(livro.getAtivo()) || livro.getQuantidadeEstoque() < quantidade) {
            throw new RegraDeNegocioException("O livro " + livro.getTitulo() + " está indisponível ou sem estoque suficiente.");
        }

        livro.setQuantidadeEstoque(livro.getQuantidadeEstoque() - quantidade);
        livroRepository.save(livro);

        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setPedido(pedidoSalvo);
        itemPedido.setLivro(livro);
        itemPedido.setQuantidade(quantidade);
        itemPedido.setPrecoUnitario(livro.getPreco());
        itemPedidoRepository.save(itemPedido);

        itensResponse.add(new ItemPedidoResponseDTO(
                itemPedido.getId(), livro.getTitulo(), quantidade, livro.getPreco()
        ));

        return subtotalAtual.add(livro.getPreco().multiply(BigDecimal.valueOf(quantidade)));
    }

    private String validarENormalizarCupom(String cupom) {
        if (cupom == null || cupom.isBlank()) {
            return null;
        }
        String normalizado = cupom.trim().toUpperCase();
        if (!CUPONS_VALIDOS.contains(normalizado)) {
            throw new RegraDeNegocioException("Cupom inválido, esgotado ou expirado.");
        }
        return normalizado;
    }

    private BigDecimal calcularFrete(String metodoEntrega) {
        return switch (metodoEntrega) {
            case "EXPRESS", "EXPRESSO" -> new BigDecimal("32.90");
            case "RETIRADA" -> BigDecimal.ZERO;
            default -> new BigDecimal("15.90");
        };
    }

    private BigDecimal calcularDescontoCupom(BigDecimal subtotal, String cupom) {
        if (cupom == null) {
            return BigDecimal.ZERO;
        }
        return switch (cupom) {
            case "ALEXANDRIA10" -> subtotal.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
            case "BEMVINDO20" -> new BigDecimal("20.00").min(subtotal);
            default -> BigDecimal.ZERO;
        };
    }

    private List<ItemPedidoResponseDTO> buscarItensDoPedido(Long pedidoId) {
        return itemPedidoRepository.findByPedidoId(pedidoId).stream()
                .map(item -> new ItemPedidoResponseDTO(
                        item.getId(), item.getLivro().getTitulo(),
                        item.getQuantidade(), item.getPrecoUnitario()
                )).collect(Collectors.toList());
    }

    private PedidoResponseDTO montarResponse(Pedido pedido, List<ItemPedidoResponseDTO> itens) {
        String enderecoCompleto = null;
        if (pedido.getEnderecoEntrega() != null) {
            enderecoCompleto = String.format("%s, %s%s - %s/%s (CEP: %s)",
                    pedido.getEnderecoEntrega(),
                    pedido.getNumeroEntrega() != null ? pedido.getNumeroEntrega() : "S/N",
                    (pedido.getComplementoEntrega() != null && !pedido.getComplementoEntrega().isBlank())
                            ? " - " + pedido.getComplementoEntrega() : "",
                    pedido.getCidadeEntrega(),
                    pedido.getEstadoEntrega(),
                    pedido.getCepEntrega());
        }

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getUsuario().getNome(),
                pedido.getDataPedido(),
                pedido.getStatus(),
                pedido.getSubtotal(),
                pedido.getValorFrete(),
                pedido.getValorDesconto(),
                pedido.getValorTotal(),
                pedido.getCupom(),
                pedido.getMetodoEntrega(),
                enderecoCompleto,
                pedido.getMetodoPagamento(),
                pedido.getParcelas(),
                itens
        );
    }
}