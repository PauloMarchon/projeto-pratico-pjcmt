package com.paulomarchon.projetopratico.pessoa;

import com.paulomarchon.projetopratico.endereco.Endereco;
import com.paulomarchon.projetopratico.foto.FotoPessoa;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pessoa", schema = "pessoas")
@DynamicUpdate
public class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pes_id")
    private Integer id;

    @Column(name = "pes_nome")
    private String nome;

    @Column(name = "pes_data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "pes_sexo")
    @Enumerated(EnumType.STRING)
    private SexoPessoa sexo;

    @Column(name = "pes_mae")
    private String nomeMae;

    @Column(name = "pes_pai")
    private String nomePai;

    @ManyToOne
    @JoinTable(
            name = "pessoa_endereco",
            schema = "pessoas",
            joinColumns =
                    @JoinColumn(name = "pes_id", referencedColumnName = "pes_id"),
            inverseJoinColumns =
                    @JoinColumn(name = "end_id", referencedColumnName = "end_id")
    )
    private Endereco endereco;

    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FotoPessoa> fotos = new HashSet<>();

    public Pessoa() {
    }

    public Pessoa(String nome, LocalDate dataNascimento, SexoPessoa sexo, String nomeMae, String nomePai) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.nomeMae = nomeMae;
        this.nomePai = nomePai;
    }

    public Pessoa(Integer id, String nome, LocalDate dataNascimento, SexoPessoa sexo, String nomeMae, String nomePai) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.nomeMae = nomeMae;
        this.nomePai = nomePai;
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

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public SexoPessoa getSexo() {
        return sexo;
    }

    public void setSexo(SexoPessoa sexo) {
        this.sexo = sexo;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public String getNomePai() {
        return nomePai;
    }

    public void setNomePai(String nomePai) {
        this.nomePai = nomePai;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Set<FotoPessoa> getFotos() {
        return fotos;
    }

    public void setFotos(Set<FotoPessoa> fotos) {
        this.fotos = fotos;
    }
}
