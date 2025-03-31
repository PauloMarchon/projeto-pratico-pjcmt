package com.paulomarchon.projetopratico.unidade;

import com.paulomarchon.projetopratico.AbstractIntegrationTest;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoCadastroEndereco;
import com.paulomarchon.projetopratico.unidade.dto.RequisicaoCadastroUnidade;
import com.paulomarchon.projetopratico.unidade.dto.UnidadeDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UnidadeControllerTest extends AbstractIntegrationTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private UnidadeService unidadeService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("listarUnidades: Deve retornas todas as unidades cadastradas como UnidadeDto de forma paginada")
    @Sql("/dados/dados_de_teste_unidade.sql")
    void listarUnidades_quandoChamado_entaoRetornaPaginaComTodasUnidadesComoUnidadeDto() {
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/v1/unidades?pag=0&results=10")
                .then()
                .statusCode(200)
                .body("totalPages", equalTo(2))
                .body("totalElements", equalTo(20))
                .body("size", equalTo(10))
                .body("numberOfElements", equalTo(10))
                .body("first", equalTo(true))
                .body("last", equalTo(false));
    }

    @Test
    @DisplayName("buscarUnidade: Deve retornar Status 200 com UnidadeDto no corpo da requisicao caso ID existir")
    void buscarUnidade_quandoIdExistir_entaoRetornaStatus200ComUnidadeDto(){
        RequisicaoCadastroEndereco requisicaoEndereco = new RequisicaoCadastroEndereco("AVENIDA", "GUIMARAES", 155, "CENTRO", "SAO PAULO", "SP");
        RequisicaoCadastroUnidade cadastroUnidade = new RequisicaoCadastroUnidade("UNIDADE DE TESTE PRIMEIRA", "UTP", requisicaoEndereco);

        UnidadeDto unidadeDto = unidadeService.cadastrarUnidade(cadastroUnidade);

        given().contentType(ContentType.JSON)
                .when()
                .get("/api/v1/unidades/{id}", unidadeDto.id())
                .then()
                .statusCode(200)
                .body("id", equalTo(unidadeDto.id()))
                .body("nome", equalTo("UNIDADE DE TESTE PRIMEIRA"))
                .body("sigla", equalTo("UTP"));
    }

    @Test
    @DisplayName("buscarUnidade: Deve retornar Status 404 quando ID nao existir")
    void buscarUnidade_quandoIdNaoExistir_entaoRetornaStatus404() {
        Integer id = 9999;

        given().contentType(ContentType.JSON)
                .when()
                .get("/api/v1/unidades/{id}", id)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("cadastrarUnidade: Deve retornar Status 201 com UnidadeDto cadastrada no corpo da requisicao")
    void cadastrarUnidade_quandoValidado_entaoRetornaStatus201ComUnidadeCadastrada() {
        RequisicaoCadastroEndereco requisicaoEndereco = new RequisicaoCadastroEndereco("AVENIDA", "GUIMARAES", 155, "CENTRO", "SAO PAULO", "SP");
        RequisicaoCadastroUnidade cadastroUnidade = new RequisicaoCadastroUnidade("UNIDADE TESTE SEGUNDA", "UTS", requisicaoEndereco);

        UnidadeDto unidadeDto = unidadeService.cadastrarUnidade(cadastroUnidade);

        given().contentType(ContentType.JSON)
                .body("""
                    {
                    "nome": "UNIDADE TESTE SEGUNDA",
                    "sigla": "UTS",
                    "endereco": {
                            "tipoLogradouro":"avenida",
                            "logradouro":"rio branco",
                            "numero": 120,
                            "bairro":"centro",
                            "cidade":"sao paulo",
                            "uf":"sp"
                        }
                    }
                """)
                .when()
                .post("/api/v1/unidades")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nome", equalTo("UNIDADE TESTE SEGUNDA"))
                .body("sigla", equalTo("UTS"));
    }

    @Test
    @DisplayName("atualizarUnidade: Deve retornar Status 200 quando ID da unidade a ser atualizada existir")
    void atualizarUnidade_quandoIdExistir_entaoRetornaStatus200(){
        RequisicaoCadastroEndereco requisicaoEndereco = new RequisicaoCadastroEndereco("AVENIDA", "GUIMARAES", 155, "CENTRO", "SAO PAULO", "SP");
        RequisicaoCadastroUnidade cadastroUnidade = new RequisicaoCadastroUnidade("UNIDADE TESTE TERCEIRA", "UTT", requisicaoEndereco);

        UnidadeDto unidadeDto = unidadeService.cadastrarUnidade(cadastroUnidade);

        given().contentType(ContentType.JSON)
                .body(
                        """
                        {
                        "nome": "UNIDADE TESTE TERCEIRA",
                        "sigla": "UTT"
                        }
                        """
                )
                .when()
                .put("/api/v1/unidades/{id}", unidadeDto.id())
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("excluirUnidade: Deve retornar Status 204 quando ID informado existir")
    void excluirUnidade_quandoIdExistir_entaoRetornaStatus204(){
        RequisicaoCadastroEndereco requisicaoEndereco = new RequisicaoCadastroEndereco("AVENIDA", "GUIMARAES", 155, "CENTRO", "SAO PAULO", "SP");
        RequisicaoCadastroUnidade cadastroUnidade = new RequisicaoCadastroUnidade("UNIDADE TESTE QUARTA", "UTQ", requisicaoEndereco);

        UnidadeDto unidadeDto = unidadeService.cadastrarUnidade(cadastroUnidade);

        given().contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/unidades/{id}", unidadeDto.id())
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("excluirUnidade: Deve retornar Status 404 quando ID informado nao existir")
    void excluirUnidade_quandoIdNaoExistir_entaoRetornaStatus404(){
        Integer id = 8888;

        given().contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/unidades/{id}", id)
                .then()
                .statusCode(404);
    }
}
