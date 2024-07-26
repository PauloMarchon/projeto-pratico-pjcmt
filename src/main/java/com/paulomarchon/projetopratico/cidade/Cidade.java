package com.paulomarchon.projetopratico.cidade;

import jakarta.persistence.*;

@Entity
@Table(name = "cidade", schema = "enderecos")
public class Cidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid_id")
    private Integer id;

    @Column(name = "cid_nome")
    private String nome;

    @Column(name = "cid_uf")
    @Enumerated(EnumType.STRING)
    private UF uf;

    public Cidade() {
    }

    public Cidade(String nome, UF uf) {
        this.nome = nome;
        this.uf = uf;
    }

    public Cidade(Integer id, String nome, UF uf) {
        this.id = id;
        this.nome = nome;
        this.uf = uf;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }

    @Override
    public String toString() {
        return  nome + " - " + uf.name();
    }
}
