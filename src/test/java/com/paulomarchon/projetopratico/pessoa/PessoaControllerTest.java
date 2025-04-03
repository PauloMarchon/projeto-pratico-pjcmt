package com.paulomarchon.projetopratico.pessoa;

import com.paulomarchon.projetopratico.AbstractIntegrationTest;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoCadastroEndereco;
import com.paulomarchon.projetopratico.pessoa.dto.PessoaDto;
import com.paulomarchon.projetopratico.pessoa.dto.RequisicaoCadastroPessoa;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PessoaControllerTest extends AbstractIntegrationTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private PessoaService pessoaService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @Sql("/dados/dados_de_teste_pessoa.sql")
    @DisplayName("Deve buscar todas as pessoas com paginacao padrao e retornar Status 200")
    void buscarTodasPessoa_quandoPaginacaoPadrao_entaoRetornaStatus200ComPaginacao() {
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/v1/pessoas")
                .then()
                .statusCode(200)
                .header("X-Page-Number", is("0"))
                .header("X-Page-Size", is("10"))
                .body("totalElements", is(20))
                .body("totalPages", is(2))
                .body("size", is(10))
                .body("numberOfElements", is(10))
                .body("last", equalTo(false));
    }

    @Test
    @Sql("/dados/dados_de_teste_pessoa.sql")
    @DisplayName("Deve buscar todas as pessoas com paginacao customizada e retornar Status 200")
    void buscarTodasPessoas_quandoPaginacaoCustomizada_entaoRetornaStatus200ComPaginacaoCorrespondente() {
        given().contentType(ContentType.JSON)
                .param("pagina", "0")
                .param("tamanho", "20")
                .when()
                .get("/api/v1/pessoas")
                .then()
                .statusCode(200)
                .header("X-Page-Number", is("0"))
                .header("X-Page-Size", is("20"))
                .body("totalElements", is(20))
                .body("totalPages", is(1))
                .body("size", is(20))
                .body("numberOfElements", is(20))
                .body("last", equalTo(true));
    }

    @Test
    @Sql("/dados/dados_de_teste_pessoa.sql")
    @DisplayName("Deve retornar as pessoas com nomes correspondentes de forma paginada")
    void buscarPessoaPorNome_quandoHouverCorrespondencia_entaoRetornaStatus200ComResultadoPaginado() {
        String nomeParaBusca = "PEDRO"; //HA 2 OCORRENCIAS CONTENDO PEDRO NO NOME NOS DADOS DE TESTE

        given().contentType(ContentType.JSON)
                .when()
                .get("/api/v1/pessoas/{nome}", nomeParaBusca)
                .then()
                .statusCode(200)
                .header("X-Page-Number", equalTo("0"))
                .header("X-Page-Size", equalTo("10"))
                .body("totalElements", is(2))
                .body("totalPages", is(1))
                .body("size", is(10))
                .body("numberOfElements", is(2))
                .body("last", equalTo(true));
    }

    @Test
    @DisplayName("cadastrarPessoa: Deve retornar Status 201 com pessoa cadastrada no corpo da requisicao")
    void cadastrarPessoa_quandoDadosValidos_entaoRetornaStatus201ComPessoaCadastradaNoCorpoDaRequisicao() {
        RequisicaoCadastroPessoa cadastroPessoa = new RequisicaoCadastroPessoa("MARCELO FERNANDES", LocalDate.of(1990,12,10), SexoPessoa.MASCULINO, "REGINA", "AFONSO");

        PessoaDto pessoaDto = pessoaService.cadastrarPessoa(cadastroPessoa);

        given().contentType(ContentType.JSON)
                .body("""
                    {
                      "nome": "MARCELO FERNANDES",
                      "dataNascimento": "1990-12-10",
                      "sexo": "MASCULINO",
                      "nomeMae": "REGINA",
                      "nomePai": "AFONSO"
                    }  
                """)
                .when()
                .post("/api/v1/pessoas")
                .then()
                .statusCode(201)
                .body("nome", is(pessoaDto.nome()))
                .body("dataNascimento", is(pessoaDto.dataNascimento()))
                .body("sexo", is(pessoaDto.sexo()))
                .body("nomeMae", is(pessoaDto.nomeMae()))
                .body("nomePai", is(pessoaDto.nomePai()));
    }

    @Test
    @DisplayName("alterarPessoa: Deve alterar pessoa quando houver alteracao valida e retornar Status 200")
    void alterarPessoa_quandoHouverAlteracao_entaoRetornaStatus200() {
        RequisicaoCadastroPessoa cadastroPessoa = new RequisicaoCadastroPessoa("ROBERTO", LocalDate.now(), SexoPessoa.MASCULINO, "ROSA", "VALMIR");

        PessoaDto pessoaDto = pessoaService.cadastrarPessoa(cadastroPessoa);

        given().contentType(ContentType.JSON)
                .body(
                        """
                          {
                            "nome": "SAMARA",
                            "dataNascimento": "1987-12-18",
                            "sexo": "FEMININO",
                            "nomeMae": "ELENA",
                            "nomePai": "BETO"
                          } 
                        """
                )
                .when()
                .put("/api/v1/pessoas/{id}", pessoaDto.id())
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("cadastrarEndereo: Deve cadastrar o endereco de uma pessoa com sucesso e retornar Status 201")
    void cadastrarEnderecoPessoa_quandoEnderecoValido_entaoRetornaStatus201() {
        RequisicaoCadastroPessoa cadastroPessoa = new RequisicaoCadastroPessoa("ROBERTO", LocalDate.now(), SexoPessoa.MASCULINO, "ROSA", "VALMIR");

        PessoaDto pessoaDto = pessoaService.cadastrarPessoa(cadastroPessoa);

        given().contentType(ContentType.JSON)
                .body(
                        """
                          {
                            "tipoLogradouro": "AVENIDA",
                            "logradouro": "GUIMARAES",
                            "numero": 155,
                            "bairro": "CENTRO",
                            "cidade": "SAO PAULO",
                            "uf": "SP"
                          }
                        """
                )
                .when()
                .post("/api/v1/pessoas/cadastro-endereco/{id}", pessoaDto.id())
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("cadastrarEndereco: Deve alterar o endereco de uma pessoa com sucesso e retornar Status 200")
    void alterarEnderecoPessoa_quandoHouverAlteracao_entaoRetornaStatus200() {
        RequisicaoCadastroPessoa cadastroPessoa = new RequisicaoCadastroPessoa("ROBERTO", LocalDate.now(), SexoPessoa.MASCULINO, "ROSA", "VALMIR");
        RequisicaoCadastroEndereco cadastroEndereco = new RequisicaoCadastroEndereco("RUA", "SAO JOSE", 20, "VILA NOVA", "RIO DE JANEIRO", "RJ");

        PessoaDto pessoaDto = pessoaService.cadastrarPessoa(cadastroPessoa);
        pessoaService.cadastrarEnderecoDePessoa(pessoaDto.id(), cadastroEndereco);

        given().contentType(ContentType.JSON)
                .body(
                        """
                          {
                            "tipoLogradouro": "",
                            "logradouro": "BOULEVARD",
                            "numero": 125,
                            "bairro": "",
                            "cidade": "SAO PAULO",
                            "uf": "SP"
                          }
                        """
                )
                .when()
                .put("/api/v1/pessoas/alteracao-endereco/{id}", pessoaDto.id())
                .then()
                .statusCode(200);
    }


    @Test
    @DisplayName("excluirPessoa: Deve excluir pessoa com sucesso e retornar Status 204")
    void excluirPessoa_quandoSucesso_entaoRetornaStatus204() {
        RequisicaoCadastroPessoa cadastroPessoa = new RequisicaoCadastroPessoa("ROBERTO", LocalDate.now(), SexoPessoa.MASCULINO, "ROSA", "VALMIR");

        PessoaDto pessoaDto = pessoaService.cadastrarPessoa(cadastroPessoa);

        given().contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/pessoas/{id}", pessoaDto.id())
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("salvarFotosDePessoa: Deve salvar as fotos de uma pessoa e retornar Status 201 com mensagem de sucesso")
    void salvarFotosDePessoa_quandoSucesso_entaoRetornaStatus201ComMensagemNoCorpoDaRequisicao() {
        RequisicaoCadastroPessoa cadastroPessoa = new RequisicaoCadastroPessoa("ROBERTO", LocalDate.now(), SexoPessoa.MASCULINO, "ROSA", "VALMIR");
        PessoaDto pessoaDto = pessoaService.cadastrarPessoa(cadastroPessoa);

        byte[] conteudo = "bytesDaImagem".getBytes(StandardCharsets.UTF_8);

        given()
                .multiPart("fotos", "foto.jpg", conteudo, "image/jpeg")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .when()
                .post("/api/v1/pessoas/{id}/foto", pessoaDto.id())
                .then()
                .statusCode(201)
                .body(equalTo("Foto armazenada com sucesso!"));
    }

    @Test
    @DisplayName("")
    void recuperarFotosDePessoa_quandoSucesso_entaoRetornaStatus200ComLinkDasFotosNoCorpoDaRequisicao() {
        //TODO
    }

    @Test
    @DisplayName("Deve excluir as fotos correspondentes e retornar Status 204")
    void excluirFotosDePessoa_quandoSucesso_entaoRetornaStatus204() {
        RequisicaoCadastroPessoa cadastroPessoa = new RequisicaoCadastroPessoa("ROBERTO", LocalDate.now(), SexoPessoa.MASCULINO, "ROSA", "VALMIR");
        PessoaDto pessoaDto = pessoaService.cadastrarPessoa(cadastroPessoa);

        String hash = UUID.randomUUID().toString();
        List<String> hashes = Arrays.asList(hash);

        given().contentType(ContentType.JSON)
                .body(
                        hashes
                )
                .when()
                .delete("/api/v1/pessoas/foto")
                .then()
                .statusCode(204);
    }
}
