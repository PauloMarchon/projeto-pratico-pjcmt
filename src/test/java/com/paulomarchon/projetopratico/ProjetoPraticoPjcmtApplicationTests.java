package com.paulomarchon.projetopratico;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ProjetoPraticoPjcmtApplicationTests {

    @Test
    void contextLoads() {
    }

}
