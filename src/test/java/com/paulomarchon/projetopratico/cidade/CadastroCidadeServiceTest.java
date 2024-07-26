package com.paulomarchon.projetopratico.cidade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CadastroCidadeServiceTest {

    @Mock
    private CidadeDao cidadeDao;
    private CadastroCidadeService emTeste;

    @BeforeEach
    public void setUp() {
        emTeste = new CadastroCidadeService(cidadeDao);
    }

    @Test
    void deveCadastrarUmaNovaCidadeCorretamente(){
       String nome = "RIO DE JANEIRO";
       UF uf = UF.RJ;

       emTeste.cadastrarNovaCidade(nome, uf);

       ArgumentCaptor<Cidade> cidadeArgumentCaptor = ArgumentCaptor.forClass(Cidade.class);

       verify(cidadeDao, times(1)).cadastrarCidade(cidadeArgumentCaptor.capture());

       Cidade cidadeCapturada = cidadeArgumentCaptor.getValue();

       assertThat(cidadeCapturada.getId()).isNull();
       assertThat(cidadeCapturada.getNome()).isEqualTo(nome);
       assertThat(cidadeCapturada.getUf()).isEqualTo(uf);
    }

    @Test
    void deveRetornarNovaCidadeCadastradaCorretamente(){
        String nome = "RIO DE JANEIRO";
        UF uf = UF.RJ;
        Cidade cidade = new Cidade(nome, uf);
        when(cidadeDao.cadastrarCidade(any(Cidade.class))).thenReturn(cidade);

        Cidade resultadoAtual = emTeste.cadastrarNovaCidade(nome, uf);

        assertThat(resultadoAtual).isNotNull();
        assertThat(resultadoAtual.getNome()).isEqualTo(nome);
        assertThat(resultadoAtual.getUf()).isEqualTo(uf);
    }
}
