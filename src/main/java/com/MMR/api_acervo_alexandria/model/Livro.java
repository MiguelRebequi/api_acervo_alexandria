package com.MMR.api_acervo_alexandria.model;

import com.MMR.api_acervo_alexandria.dto.LivroRequestDTO;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "livro")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(nullable = false, length = 100)
    private String autor;

    @Column(nullable = false, unique = true, length = 17)
    private String isbn;

    @Column(length = 150)
    private String editora;

    @Column(length = 50)
    private String idioma;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private FormatoLivro formato;

    @Column(precision = 3, scale = 2)
    private BigDecimal avaliacao =  BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Integer quantidadeEstoque;

    @Column(name = "ano_publicacao", nullable = false)
    private Integer anoPublicacao;

    @Column(nullable = false)
    private Boolean ativo = true; // Todo registro de Livro já nasce ativo por padrão

    public Livro() {}

    public Livro(String titulo, String autor, String isbn, String editora, String idioma, FormatoLivro formato, BigDecimal avaliacao, BigDecimal preco, Integer quantidadeEstoque, Integer anoPublicacao) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.editora = editora;
        this.idioma = idioma;
        this.formato = formato;
        this.avaliacao = (avaliacao != null) ? avaliacao : BigDecimal.ZERO;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.anoPublicacao = anoPublicacao;
    }

    public Livro(LivroRequestDTO dto) {
        this.atualizarDados(dto);
    }

    public void atualizarDados(LivroRequestDTO dto) {
        this.titulo = dto.titulo();
        this.autor = dto.autor();
        this.isbn = dto.isbn();
        this.editora = dto.editora();
        this.idioma = dto.idioma();
        this.formato = dto.formato();
        this.avaliacao = (dto.avaliacao() != null) ? dto.avaliacao() : BigDecimal.ZERO;
        this.preco = dto.preco();
        this.quantidadeEstoque = dto.quantidadeEstoque();
        this.anoPublicacao = dto.anoPublicacao();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(Integer anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public FormatoLivro getFormato() {
        return formato;
    }

    public void setFormato(FormatoLivro formato) {
        this.formato = formato;
    }

    public BigDecimal getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(BigDecimal avaliacao) {
        this.avaliacao = avaliacao;
    }
}
