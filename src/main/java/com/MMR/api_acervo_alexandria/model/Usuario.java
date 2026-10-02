package com.MMR.api_acervo_alexandria.model;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String endereco;

    @Column(length = 20)
    private String telefoneFixo;

    @Column(length = 20)
    private String telefoneMovel;

    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Credencial credencial;

    private List<Carrinho> itensCarrinho = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<Pedido> pedidos = new ArrayList<>();



    public Usuario() {}

    public Usuario(String nome, String email, String endereco) {
        this.nome = nome;
        this.email = email;
        this.endereco = endereco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefoneFixo() {
        return telefoneFixo;
    }

    public void setTelefoneFixo(String telefoneFixo) {
        this.telefoneFixo = telefoneFixo;
    }

    public String getTelefoneMovel() {
        return telefoneMovel;
    }

    public void setTelefoneMovel(String telefoneMovel) {
        this.telefoneMovel = telefoneMovel;
    }

    public Credencial getCredencial() {
        return credencial;
    }

    public void setCredencial(Credencial credencial) {
        this.credencial = credencial;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public List<Carrinho> getItensCarrinho() {
        return itensCarrinho;
    }

    public void setItensCarrinho(List<Carrinho> itensCarrinho) {
        this.itensCarrinho = itensCarrinho;
    }
}
