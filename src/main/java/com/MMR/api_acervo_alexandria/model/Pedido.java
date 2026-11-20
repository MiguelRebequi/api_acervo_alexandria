package com.MMR.api_acervo_alexandria.model;

import com.MMR.api_acervo_alexandria.dto.PedidoRequestDTO;
import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataPedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusPedido status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorFrete;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorDesconto;

    @Column(length = 50)
    private String cupom;

    // Dados de Entrega
    @Column(length = 30)
    private String metodoEntrega;

    @Column(length = 150)
    private String destinatarioNome;

    @Column(length = 150)
    private String destinatarioEmail;

    @Column(length = 20)
    private String cepEntrega;

    @Column(length = 10)
    private String paisEntrega;

    @Column(length = 50)
    private String estadoEntrega;

    @Column(length = 100)
    private String cidadeEntrega;

    @Column(length = 255)
    private String enderecoEntrega;

    @Column(length = 20)
    private String numeroEntrega;

    @Column(length = 100)
    private String complementoEntrega;

    @Column(length = 255)
    private String observacoesEntrega;

    // Dados de Pagamento
    @Column(length = 30)
    private String metodoPagamento;

    private Integer parcelas;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itens = new ArrayList<>();

    public  Pedido() {
    }

    public Pedido(LocalDateTime dataPedido, StatusPedido status, BigDecimal valorTotal) {
        this.dataPedido = dataPedido;
        this.status = status;
        this.valorTotal = valorTotal;
    }

    public Pedido(Usuario usuario, PedidoRequestDTO dto) {
        this.usuario = usuario;
        this.dataPedido = LocalDateTime.now();
        this.status = StatusPedido.PROCESSANDO;
        this.valorTotal = BigDecimal.ZERO;

        var entrega = dto.entrega();
        this.metodoEntrega = entrega.metodo().toUpperCase();
        this.destinatarioNome = (entrega.nome() + " " + entrega.sobrenome()).trim();
        this.destinatarioEmail = entrega.email();
        this.paisEntrega = entrega.pais();
        this.cepEntrega = entrega.cep();
        this.estadoEntrega = entrega.estado();
        this.cidadeEntrega = entrega.cidade();
        this.enderecoEntrega = entrega.endereco();
        this.numeroEntrega = entrega.numero();
        this.complementoEntrega = entrega.complemento();
        this.observacoesEntrega = entrega.observacoes();

        this.metodoPagamento = dto.pagamento().metodo().toUpperCase();
        this.parcelas = dto.pagamento().parcelas();
        this.cupom = (dto.cupom() != null && !dto.cupom().isBlank())
                ? dto.cupom().trim().toUpperCase()
                : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDateTime dataPedido) {
        this.dataPedido = dataPedido;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }



    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getValorFrete() {
        return valorFrete;
    }

    public void setValorFrete(BigDecimal valorFrete) {
        this.valorFrete = valorFrete;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public String getCupom() {
        return cupom;
    }

    public void setCupom(String cupom) {
        this.cupom = cupom;
    }

    public String getMetodoEntrega() {
        return metodoEntrega;
    }

    public void setMetodoEntrega(String metodoEntrega) {
        this.metodoEntrega = metodoEntrega;
    }

    public String getDestinatarioNome() {
        return destinatarioNome;
    }

    public void setDestinatarioNome(String destinatarioNome) {
        this.destinatarioNome = destinatarioNome;
    }

    public String getDestinatarioEmail() {
        return destinatarioEmail;
    }

    public void setDestinatarioEmail(String destinatarioEmail) {
        this.destinatarioEmail = destinatarioEmail;
    }

    public String getCepEntrega() {
        return cepEntrega;
    }

    public void setCepEntrega(String cepEntrega) {
        this.cepEntrega = cepEntrega;
    }

    public String getPaisEntrega() {
        return paisEntrega;
    }

    public void setPaisEntrega(String paisEntrega) {
        this.paisEntrega = paisEntrega;
    }

    public String getEstadoEntrega() {
        return estadoEntrega;
    }

    public void setEstadoEntrega(String estadoEntrega) {
        this.estadoEntrega = estadoEntrega;
    }

    public String getCidadeEntrega() {
        return cidadeEntrega;
    }

    public void setCidadeEntrega(String cidadeEntrega) {
        this.cidadeEntrega = cidadeEntrega;
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public String getNumeroEntrega() {
        return numeroEntrega;
    }

    public void setNumeroEntrega(String numeroEntrega) {
        this.numeroEntrega = numeroEntrega;
    }

    public String getComplementoEntrega() {
        return complementoEntrega;
    }

    public void setComplementoEntrega(String complementoEntrega) {
        this.complementoEntrega = complementoEntrega;
    }

    public String getObservacoesEntrega() {
        return observacoesEntrega;
    }

    public void setObservacoesEntrega(String observacoesEntrega) {
        this.observacoesEntrega = observacoesEntrega;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public Integer getParcelas() {
        return parcelas;
    }

    public void setParcelas(Integer parcelas) {
        this.parcelas = parcelas;
    }
}
