package com.paulomarchon.projetopratico.unidade;

import com.paulomarchon.projetopratico.AbstractIntegrationTest;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoCadastroEndereco;
import com.paulomarchon.projetopratico.unidade.dto.RequisicaoCadastroUnidade;
import com.paulomarchon.projetopratico.unidade.dto.UnidadeDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
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
    @Sql("/dados/dados_de_teste_unidade.sql")
    void deveRetornarUnidadesPaginadas(){
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
    void deveRetornarUmaUnidadePorId(){
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
    void deveRetornarError404UnidadeComIdInexistente(){
        Integer id = 9999;

        given().contentType(ContentType.JSON)
                .when()
                .get("/api/v1/unidades/{id}", id)
                .then()
                .statusCode(404);
    }

    @Test
    void deveCadastrarNovaUnidadeComSucesso(){
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
    void deveAlterarUnidadeComSucesso(){
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
    void deveDeletarUnidadeComSucesso(){
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
    void deveRetornarError404AoTentarExcluirUnidadeComIdInexistente(){
        Integer id = 8888;

        given().contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/unidades/{id}", id)
                .then()
                .statusCode(404);
    }
}
