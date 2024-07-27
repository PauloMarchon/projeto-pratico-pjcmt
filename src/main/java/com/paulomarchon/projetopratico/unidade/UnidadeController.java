package com.paulomarchon.projetopratico.unidade;

import com.paulomarchon.projetopratico.unidade.dto.RequisicaoAlteracaoUnidade;
import com.paulomarchon.projetopratico.unidade.dto.RequisicaoCadastroUnidade;
import com.paulomarchon.projetopratico.unidade.dto.UnidadeDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/unidades")
public class UnidadeController {
    private final UnidadeService unidadeService;

    public UnidadeController(UnidadeService unidadeService) {
        this.unidadeService = unidadeService;
    }

    @GetMapping
    public Page<UnidadeDto> listarUnidades(
            @RequestParam(name = "pag", defaultValue = "0", required = false) Integer pagina,
            @RequestParam(name = "results", defaultValue = "10", required = false) Integer quantResultados
    ) {
        return unidadeService.buscarTodasUnidades(pagina, quantResultados);
    }

    @GetMapping("{id}")
    public ResponseEntity<UnidadeDto> buscarUnidade(@PathVariable(name = "id") Integer id) {
        UnidadeDto unidadeDto = unidadeService.selecionarUnidadePorId(id);

        return ResponseEntity.ok(unidadeDto);
    }

    @PostMapping
    public ResponseEntity<UnidadeDto> cadastrarUnidade(@RequestBody @Validated RequisicaoCadastroUnidade unidadeRequest) {
        UnidadeDto unidadeDto = unidadeService.cadastrarUnidade(unidadeRequest);
        return new ResponseEntity<>(unidadeDto, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public void atualizarUnidade(@PathVariable(name = "id") Integer id, @RequestBody RequisicaoAlteracaoUnidade unidadeRequest) {
        unidadeService.alterarUnidade(id, unidadeRequest);
    }


    @DeleteMapping("{id}")
    public ResponseEntity<?> excluirUnidade(@PathVariable(name = "id") Integer id) {
        unidadeService.excluirUnidade(id);

        return ResponseEntity.noContent().build();
    }
}