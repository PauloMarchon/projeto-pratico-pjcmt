package com.paulomarchon.projetopratico.unidade;

import com.paulomarchon.projetopratico.endereco.Endereco;
import jakarta.persistence.*;

@Entity
@Table(name = "unidade", schema = "unidades")
public class Unidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unid_id")
    private Integer id;

    @Column(name = "unid_nome")
    private String nome;

    @Column(name = "unid_sigla")
    private String sigla;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(
            name = "unidade_endereco",
            schema = "unidades",
            joinColumns =
                @JoinColumn(name = "unid_id", referencedColumnName = "unid_id"),
            inverseJoinColumns =
                @JoinColumn(name = "end_id", referencedColumnName = "end_id")
    )
    private Endereco endereco;

    public Unidade() {
    }

    public Unidade(Integer id, String nome, String sigla) {
        this.id = id;
        this.nome = nome;
        this.sigla = sigla;
    }

    public Unidade(String nome, String sigla, Endereco endereco) {
        this.nome = nome;
        this.sigla = sigla;
        this.endereco = endereco;
    }

    public Unidade(Integer id, String nome, String sigla, Endereco endereco) {
        this.id = id;
        this.nome = nome;
        this.sigla = sigla;
        this.endereco = endereco;
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

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
}
