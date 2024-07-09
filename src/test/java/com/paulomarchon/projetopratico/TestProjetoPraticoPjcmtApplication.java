package com.paulomarchon.projetopratico;

import org.springframework.boot.SpringApplication;

public class TestProjetoPraticoPjcmtApplication {

    public static void main(String[] args) {
        SpringApplication.from(ProjetoPraticoPjcmtApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
