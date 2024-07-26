package com.paulomarchon.projetopratico.cidade;

import com.paulomarchon.projetopratico.cidade.dto.RequisicaoCadastroCidade;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CidadeServiceTest {

    @Mock
    private CidadeDao cidadeDao;
    @Mock
    private CadastroCidadeService cadastroCidadeService;
    private CidadeService emTeste;

    @BeforeEach
    public void setUp() {
        emTeste = new CidadeService(cidadeDao, cadastroCidadeService);
    }

    @Test
    void deveBuscarCidadeAoReceberUmaRequisicaoDeUmaCidadeExistente(){
        Integer id = 1;
        RequisicaoCadastroCidade requisicao = new RequisicaoCadastroCidade("rio de janeiro", "rj");
        UF uf = emTeste.obtemUFInformado(requisicao.uf());

        Cidade cidade = new Cidade(id, requisicao.nome(), uf);
        when(cidadeDao.buscarCidade(requisicao.nome(), uf)).thenReturn(Optional.of(cidade));

        Cidade resultadoAtual = emTeste.selecionaOuCadastraNovaCidade(requisicao.nome(), uf);

        assertThat(resultadoAtual).isNotNull();
        assertThat(resultadoAtual.getId()).isEqualTo(id);
        assertThat(resultadoAtual.getNome()).isEqualTo(requisicao.nome());
        assertThat(resultadoAtual.getUf()).isEqualTo(uf);

        verify(cidadeDao, times(1)).buscarCidade(requisicao.nome(), uf);
        verify(cidadeDao, never()).cadastrarCidade(any());
        verify(cadastroCidadeService, never()).cadastrarNovaCidade(anyString(), any());
        verifyNoMoreInteractions(cidadeDao);
    }

    @Test
    void deveChamarServicoDeCadastroAoReceberUmaRequisicaoDeUmaCidadeNaoCadastrada(){
        RequisicaoCadastroCidade requisicao = new RequisicaoCadastroCidade("rio de janeiro", "rj");
        UF uf = emTeste.obtemUFInformado(requisicao.uf());

        emTeste.selecionaOuCadastraNovaCidade(requisicao.nome(), uf);

        verify(cidadeDao, times(1)).buscarCidade(eq(requisicao.nome()), eq(uf));
        verify(cadastroCidadeService, times(1)).cadastrarNovaCidade(eq(requisicao.nome()), eq(uf));
    }

    @Test
    void obtemUFInformadoComSucesso(){
        RequisicaoCadastroCidade requisicao = new RequisicaoCadastroCidade("rio de janeiro", "rj");

        UF resultadoAtual = emTeste.obtemUFInformado(requisicao.uf());

        assertThat(resultadoAtual).isEqualTo(UF.RJ);
    }

    @Test
    void falhaAoTentarObterUFInexistente(){
        RequisicaoCadastroCidade requisicao = new RequisicaoCadastroCidade("rio de janeiro", "aa");

        assertThatThrownBy(() -> emTeste.obtemUFInformado(requisicao.uf()))
                .isInstanceOf(IllegalArgumentException.class)
                .satisfies(exception -> {
                    assertThat(exception.getMessage()).isEqualTo("A UF informada nao existe");
                });
    }
}
