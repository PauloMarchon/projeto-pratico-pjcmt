package com.paulomarchon.projetopratico.cidade;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CidadeRepository extends JpaRepository<Cidade, Integer> {
    Optional<Cidade> findByNomeIgnoreCaseAndUf(String nome, UF uf);
}
