package com.paulomarchon.projetopratico.pessoa;

import com.paulomarchon.projetopratico.AbstractIntegrationTest;
import com.paulomarchon.projetopratico.endereco.Endereco;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoAlteracaoEndereco;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoCadastroEndereco;
import com.paulomarchon.projetopratico.pessoa.dto.PessoaDto;
import com.paulomarchon.projetopratico.pessoa.dto.RequisicaoCadastroPessoa;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import static org.mockito.Mockito.*;

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
    void deveBuscarTodasPessoasComPaginacaoPadrao() {
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
    void deveBuscarTodasPessoasComPaginacaoCustomizada() {
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
    void deveBuscarPessoaPorNome() {
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
    void deveCadastrarPessoa() {
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
    void deveAlterarPessoa() {
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
    void deveCadastrarEnderecoDePessoa() {
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

    /*
    @Test
    void deveAlterarEnderecoDePessoa() {
        RequisicaoCadastroPessoa cadastroPessoa = new RequisicaoCadastroPessoa("ROBERTO", LocalDate.now(), SexoPessoa.MASCULINO, "ROSA", "VALMIR");
        RequisicaoCadastroEndereco alteracaoEndereco = new RequisicaoCadastroEndereco("RUA", "SAO JOSE", 20, "VILA NOVA", "SAO PAULO", "SP");

        PessoaDto pessoaDto = pessoaService.cadastrarPessoa(cadastroPessoa);
        pessoaService.cadastrarEnderecoDePessoa(pessoaDto.id(), alteracaoEndereco);

        given().contentType(ContentType.JSON)
                .body(
                        """
                          {
                            "tipoLogradouro": "",
                            "logradouro": "BOULEVARD",
                            "numero": 125,
                            "bairro": "",
                            "cidade": "",
                            "uf": ""
                          }
                        """
                )
                .when()
                .put("/api/v1/pessoas/alteracao-endereco/{id}", pessoaDto.id())
                .then()
                .statusCode(200);
    }
    */

    @Test
    void deveExcluirPessoa() {
        RequisicaoCadastroPessoa cadastroPessoa = new RequisicaoCadastroPessoa("ROBERTO", LocalDate.now(), SexoPessoa.MASCULINO, "ROSA", "VALMIR");

        PessoaDto pessoaDto = pessoaService.cadastrarPessoa(cadastroPessoa);

        given().contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/pessoas/{id}", pessoaDto.id())
                .then()
                .statusCode(204);
    }
}
