package com.paulomarchon.projetopratico.pessoa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {
   Page<Pessoa> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
