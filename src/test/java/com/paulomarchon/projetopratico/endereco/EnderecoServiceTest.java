package com.paulomarchon.projetopratico.endereco;

import com.paulomarchon.projetopratico.cidade.Cidade;
import com.paulomarchon.projetopratico.cidade.CidadeService;
import com.paulomarchon.projetopratico.cidade.UF;
import com.paulomarchon.projetopratico.cidade.dto.RequisicaoCadastroCidade;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoAlteracaoEndereco;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoCadastroEndereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class EnderecoServiceTest {

    @Mock
    private EnderecoDao enderecoDao;
    @Mock
    private CidadeService cidadeService;
    private EnderecoService emTeste;

    @BeforeEach
    public void setUp() {
        emTeste = new EnderecoService(enderecoDao, cidadeService);
    }

    @Test
    @DisplayName("Deve salvar endereco com sucesso")
    void salvarEndereco_quandoInformado_entaoCadastraEnderecoComSucesso(){
        RequisicaoCadastroEndereco cadastroEndereco = new RequisicaoCadastroEndereco("AVENIDA", "GUIMARAES", 155, "CENTRO", "SAO PAULO", "SP");
        RequisicaoCadastroCidade cadastroCidade = new RequisicaoCadastroCidade(cadastroEndereco.cidade(), cadastroEndereco.uf());
        Cidade cidade = new Cidade("SAO PAULO", UF.SP);

        when(cidadeService.processaRequisicaoDeCidade(cadastroCidade)).thenReturn(cidade);

        emTeste.salvarEndereco(cadastroEndereco);

        ArgumentCaptor<Endereco> enderecoArgumentCaptor = ArgumentCaptor.forClass(Endereco.class);

        verify(enderecoDao, times(1)).salvarEndereco(enderecoArgumentCaptor.capture());

        Endereco enderecoCapturado = enderecoArgumentCaptor.getValue();

        assertThat(enderecoCapturado.getTipoLogradouro()).isEqualTo(cadastroEndereco.tipoLogradouro());
        assertThat(enderecoCapturado.getLogradouro()).isEqualTo(cadastroEndereco.logradouro());
        assertThat(enderecoCapturado.getNumero()).isEqualTo(cadastroEndereco.numero());
        assertThat(enderecoCapturado.getBairro()).isEqualTo(cadastroEndereco.bairro());
        assertThat(enderecoCapturado.getCidade()).isEqualTo(cidade);
    }

    @Test
    @DisplayName("Deve retornar o endereco cadastrado")
    void salvarEndereco_quandoCadastradoComSucesso_entaoRetornaEndereco(){
        RequisicaoCadastroEndereco cadastroEndereco = new RequisicaoCadastroEndereco("AVENIDA", "GUIMARAES", 155, "CENTRO", "SAO PAULO", "SP");
        Cidade cidade = new Cidade("SAO PAULO", UF.SP);
        Endereco endereco = new Endereco(cadastroEndereco.tipoLogradouro(), cadastroEndereco.logradouro(), cadastroEndereco.numero(), cadastroEndereco.bairro(), cidade);
        when(enderecoDao.salvarEndereco(any(Endereco.class))).thenReturn(endereco);

        Endereco resultadoAtual = emTeste.salvarEndereco(cadastroEndereco);

        assertThat(resultadoAtual.getTipoLogradouro()).isEqualTo(cadastroEndereco.tipoLogradouro());
        assertThat(resultadoAtual.getLogradouro()).isEqualTo(cadastroEndereco.logradouro());
        assertThat(resultadoAtual.getNumero()).isEqualTo(cadastroEndereco.numero());
        assertThat(resultadoAtual.getBairro()).isEqualTo(cadastroEndereco.bairro());
        assertThat(resultadoAtual.getCidade()).isEqualTo(cidade);
    }

    @Test
    @DisplayName("Deve alterar todos as propriedades do endereço e salvar a alteração com sucesso")
    void alterarEndereco_quandoAlterarTodasAsPropriedades_entaoSalvaAlteracaoComSucesso(){
        Integer id = 1;
        Cidade cidade = new Cidade("SAO PAULO", UF.SP);
        Endereco endereco = new Endereco(id, "AVENIDA", "GUIMARAES", 155, "CENTRO", cidade);
        when(enderecoDao.selecionarEnderecoPorReferenciaDeId(id)).thenReturn(endereco);

        RequisicaoAlteracaoEndereco requisicaoAlteracaoEndereco = new RequisicaoAlteracaoEndereco("RUA", "SAO JOSE", 20, "VILA NOVA", null, null);

        emTeste.alterarEndereco(id, requisicaoAlteracaoEndereco);

        ArgumentCaptor<Endereco> enderecoArgumentCaptor = ArgumentCaptor.forClass(Endereco.class);

        verify(enderecoDao).alterarEndereco(enderecoArgumentCaptor.capture());
        Endereco enderecoCapturado = enderecoArgumentCaptor.getValue();

        assertThat(enderecoCapturado.getTipoLogradouro()).isEqualTo(requisicaoAlteracaoEndereco.tipoLogradouro());
        assertThat(enderecoCapturado.getLogradouro()).isEqualTo(requisicaoAlteracaoEndereco.logradouro());
        assertThat(enderecoCapturado.getNumero()).isEqualTo(requisicaoAlteracaoEndereco.numero());
        assertThat(enderecoCapturado.getBairro()).isEqualTo(requisicaoAlteracaoEndereco.bairro());
    }

    @Test
    @DisplayName("Deve alterar apenas o TipoLogradouro do endereço e salvar a alteração com sucesso")
    void alterarEndereco_quandoAlterarTipoLogradouro_entaoSalvaAlteracaoComSucesso(){ //so logradouro
        Integer id = 1;
        Cidade cidade = new Cidade("SAO PAULO", UF.SP);
        Endereco endereco = new Endereco(id, "AVENIDA", "GUIMARAES", 155, "CENTRO", cidade);
        when(enderecoDao.selecionarEnderecoPorReferenciaDeId(id)).thenReturn(endereco);

        RequisicaoAlteracaoEndereco requisicaoAlteracaoEndereco = new RequisicaoAlteracaoEndereco("RUA", null, null, null, null, null);

        emTeste.alterarEndereco(id, requisicaoAlteracaoEndereco);

        ArgumentCaptor<Endereco> enderecoArgumentCaptor = ArgumentCaptor.forClass(Endereco.class);

        verify(enderecoDao).alterarEndereco(enderecoArgumentCaptor.capture());
        Endereco enderecoCapturado = enderecoArgumentCaptor.getValue();

        assertThat(enderecoCapturado.getTipoLogradouro()).isEqualTo(requisicaoAlteracaoEndereco.tipoLogradouro());
        assertThat(enderecoCapturado.getLogradouro()).isEqualTo(endereco.getLogradouro());
        assertThat(enderecoCapturado.getNumero()).isEqualTo(endereco.getNumero());
        assertThat(enderecoCapturado.getBairro()).isEqualTo(endereco.getBairro());
        assertThat(enderecoCapturado.getCidade()).isEqualTo(endereco.getCidade());
    }

    @Test
    @DisplayName("Deve alterar apenas o numero do endereço e salvar a alteração com sucesso")
    void alterarEndereco_QuandoAlterarNumero_entaoSalvaAlteracaoComSucesso(){
        Integer id = 1;
        Cidade cidade = new Cidade("SAO PAULO", UF.SP);
        Endereco endereco = new Endereco(id, "AVENIDA", "GUIMARAES", 155, "CENTRO", cidade);
        when(enderecoDao.selecionarEnderecoPorReferenciaDeId(id)).thenReturn(endereco);

        RequisicaoAlteracaoEndereco requisicaoAlteracaoEndereco = new RequisicaoAlteracaoEndereco(null, null, 555, null, null, null);

        emTeste.alterarEndereco(id, requisicaoAlteracaoEndereco);

        ArgumentCaptor<Endereco> enderecoArgumentCaptor = ArgumentCaptor.forClass(Endereco.class);

        verify(enderecoDao).alterarEndereco(enderecoArgumentCaptor.capture());
        Endereco enderecoCapturado = enderecoArgumentCaptor.getValue();

        assertThat(enderecoCapturado.getTipoLogradouro()).isEqualTo(endereco.getTipoLogradouro());
        assertThat(enderecoCapturado.getLogradouro()).isEqualTo(endereco.getLogradouro());
        assertThat(enderecoCapturado.getNumero()).isEqualTo(requisicaoAlteracaoEndereco.numero());
        assertThat(enderecoCapturado.getBairro()).isEqualTo(endereco.getBairro());
        assertThat(enderecoCapturado.getCidade()).isEqualTo(endereco.getCidade());
    }

    @Test
    @DisplayName("Deve alterar apenas o logradouro do endereço e salvar a alteração com sucesso")
    void alterarEndereco_quandoAlterarLogradouro_entaoSalvaAlteracaoComSucesso(){
        Integer id = 1;
        Cidade cidade = new Cidade("SAO PAULO", UF.SP);
        Endereco endereco = new Endereco(id, "AVENIDA", "GUIMARAES", 155, "CENTRO", cidade);
        when(enderecoDao.selecionarEnderecoPorReferenciaDeId(id)).thenReturn(endereco);

        RequisicaoAlteracaoEndereco requisicaoAlteracaoEndereco = new RequisicaoAlteracaoEndereco(null, "BEIJA-FLOR", null, null, null, null);

        emTeste.alterarEndereco(id, requisicaoAlteracaoEndereco);

        ArgumentCaptor<Endereco> enderecoArgumentCaptor = ArgumentCaptor.forClass(Endereco.class);

        verify(enderecoDao).alterarEndereco(enderecoArgumentCaptor.capture());
        Endereco enderecoCapturado = enderecoArgumentCaptor.getValue();

        assertThat(enderecoCapturado.getTipoLogradouro()).isEqualTo(endereco.getTipoLogradouro());
        assertThat(enderecoCapturado.getLogradouro()).isEqualTo(requisicaoAlteracaoEndereco.logradouro());
        assertThat(enderecoCapturado.getNumero()).isEqualTo(endereco.getNumero());
        assertThat(enderecoCapturado.getBairro()).isEqualTo(endereco.getBairro());
        assertThat(enderecoCapturado.getCidade()).isEqualTo(endereco.getCidade());
    }

    @Test
    @DisplayName("Deve alterar apenas a Cidade do endereço e salvar a alteração com sucesso")
    void alterarEndereco_quandoAlterarCidade_entaoSalvaAlteracaoComSucesso(){
        Integer id = 1;
        Cidade cidade = new Cidade("SAO PAULO", UF.SP);
        Endereco endereco = new Endereco(id, "AVENIDA", "GUIMARAES", 155, "CENTRO", cidade);
        when(enderecoDao.selecionarEnderecoPorReferenciaDeId(id)).thenReturn(endereco);

        RequisicaoAlteracaoEndereco requisicaoAlteracaoEndereco = new RequisicaoAlteracaoEndereco(null, null, null, null, "RIO DE JANEIRO", "RJ");
        RequisicaoCadastroCidade requisicaoCidade = new RequisicaoCadastroCidade(requisicaoAlteracaoEndereco.cidade(), requisicaoAlteracaoEndereco.uf());
        Cidade novaCidade = new Cidade("RIO DE JANEIRO", UF.RJ);
        when(cidadeService.processaRequisicaoDeCidade(requisicaoCidade)).thenReturn(novaCidade);

        emTeste.alterarEndereco(id, requisicaoAlteracaoEndereco);

        ArgumentCaptor<Endereco> enderecoArgumentCaptor = ArgumentCaptor.forClass(Endereco.class);

        verify(enderecoDao).alterarEndereco(enderecoArgumentCaptor.capture());
        Endereco enderecoCapturado = enderecoArgumentCaptor.getValue();

        assertThat(enderecoCapturado.getTipoLogradouro()).isEqualTo(endereco.getTipoLogradouro());
        assertThat(enderecoCapturado.getLogradouro()).isEqualTo(endereco.getLogradouro());
        assertThat(enderecoCapturado.getNumero()).isEqualTo(endereco.getNumero());
        assertThat(enderecoCapturado.getBairro()).isEqualTo(endereco.getBairro());
        assertThat(enderecoCapturado.getCidade()).isEqualTo(novaCidade);

        verify(cidadeService, times(1)).processaRequisicaoDeCidade(requisicaoCidade);
    }

    @Test
    @DisplayName("Deve falhar ao tentar alterar endereco com propriedades iguais as ja cadastradas")
    void alterarEndereco_quandoPropriedadesForemIguais_entaoLancaExcecao(){
        Integer id = 1;
        Cidade cidade = new Cidade("SAO PAULO", UF.SP);
        Endereco endereco = new Endereco(id, "AVENIDA", "GUIMARAES", 155, "CENTRO", cidade);
        when(enderecoDao.selecionarEnderecoPorReferenciaDeId(id)).thenReturn(endereco);

        RequisicaoAlteracaoEndereco requisicaoAlteracaoEndereco = new RequisicaoAlteracaoEndereco(endereco.getTipoLogradouro(), endereco.getLogradouro(), endereco.getNumero(), endereco.getBairro(), null, null);

        assertThatThrownBy(() -> emTeste.alterarEndereco(id, requisicaoAlteracaoEndereco)).isInstanceOf(IllegalArgumentException.class).hasMessage("Erro ao alterar endereco, nenhuma informacao alterada");

        verify(enderecoDao, never()).alterarEndereco(any());
    }

    @Test
    @DisplayName("Deve retornar Cidade quando cidade e uf existir")
    void buscaCidade_quandoExistir_entaoRetornaCidade(){
        RequisicaoCadastroEndereco cadastroEndereco = new RequisicaoCadastroEndereco("AVENIDA", "GUIMARAES", 155, "CENTRO", "SAO PAULO", "SP");

        emTeste.buscaCidade(cadastroEndereco.cidade(), cadastroEndereco.uf());

        ArgumentCaptor<RequisicaoCadastroCidade> cadastroCidadeCaptor = ArgumentCaptor.forClass(RequisicaoCadastroCidade.class);

        verify(cidadeService).processaRequisicaoDeCidade(cadastroCidadeCaptor.capture());

        RequisicaoCadastroCidade requisicaoCapturada = cadastroCidadeCaptor.getValue();

        assertThat(requisicaoCapturada.nome()).isEqualTo(cadastroEndereco.cidade());
        assertThat(requisicaoCapturada.uf()).isEqualTo(cadastroEndereco.uf());
    }
}
