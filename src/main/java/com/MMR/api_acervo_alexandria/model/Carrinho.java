package com.MMR.api_acervo_alexandria.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "carrinho")
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private LocalDateTime dataAdicao;

    public Carrinho() {}

    public Carrinho(Usuario usuario, Livro livro, Integer quantidade, LocalDateTime dataAdicao) {
        this.usuario = usuario;
        this.livro = livro;
        this.quantidade = quantidade;
        this.dataAdicao = dataAdicao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDateTime getDataAdicao() {
        return dataAdicao;
    }

    public void setDataAdicao(LocalDateTime dataAdicao) {
        this.dataAdicao = dataAdicao;
    }
}