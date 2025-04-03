package com.paulomarchon.projetopratico.pessoa;

import com.paulomarchon.projetopratico.endereco.dto.RequisicaoAlteracaoEndereco;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoCadastroEndereco;
import com.paulomarchon.projetopratico.pessoa.dto.PessoaDto;
import com.paulomarchon.projetopratico.pessoa.dto.RequisicaoAlteracaoPessoa;
import com.paulomarchon.projetopratico.pessoa.dto.RequisicaoCadastroPessoa;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/pessoas")
public class PessoaController {
    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @GetMapping
    public ResponseEntity<Page<PessoaDto>> buscarTodasPessoas(@RequestParam(defaultValue = "0") Integer pagina,
                                                              @RequestParam(defaultValue = "10") Integer tamanho) {
        Page<PessoaDto> paginaPessoa = pessoaService.buscarTodasPessoas(pagina, tamanho);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Page-Number", String.valueOf(paginaPessoa.getNumber()));
        headers.add("X-Page-Size", String.valueOf(paginaPessoa.getSize()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(paginaPessoa);
    }

    @GetMapping("{nome}")
    public ResponseEntity<Page<PessoaDto>> buscarPessoaPorNome(@PathVariable String nome,
                                                               @RequestParam(defaultValue = "0") Integer pagina,
                                                               @RequestParam(defaultValue = "10") Integer tamanho) {
        Page<PessoaDto> paginaPessoa = pessoaService.buscarPessoaPorNome(nome, pagina, tamanho);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Page-Number", String.valueOf(paginaPessoa.getNumber()));
        headers.add("X-Page-Size", String.valueOf(paginaPessoa.getSize()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(paginaPessoa);
    }

    @PostMapping
    public ResponseEntity<PessoaDto> cadastrarPessoa(@RequestBody @Validated RequisicaoCadastroPessoa cadastroPessoa) {
        PessoaDto pessoaDto = pessoaService.cadastrarPessoa(cadastroPessoa);

        return new ResponseEntity<>(pessoaDto, HttpStatus.CREATED);
    }

    @PutMapping("{pessoaId}")
    public ResponseEntity<?> alterarPessoa(@PathVariable Integer pessoaId,
                                           @RequestBody RequisicaoAlteracaoPessoa alteracaoPessoa) {
        pessoaService.alterarPessoa(pessoaId, alteracaoPessoa);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/cadastro-endereco/{pessoaId}")
    public ResponseEntity<?> cadastrarEnderecoPessoa(@PathVariable Integer pessoaId,
                                                     @RequestBody @Validated RequisicaoCadastroEndereco cadastroEndereco) {
        pessoaService.cadastrarEnderecoDePessoa(pessoaId, cadastroEndereco);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/alteracao-endereco/{pessoaId}")
    public ResponseEntity<?> alterarEnderecoPessoa(@PathVariable Integer pessoaId,
                                                   @RequestBody RequisicaoAlteracaoEndereco alteracaoEndereco) {
        pessoaService.alterarEnderecoDePessoa(pessoaId, alteracaoEndereco);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("{pessoaId}")
    public ResponseEntity<?> excluirPessoa(@PathVariable Integer pessoaId) {
        pessoaService.excluirPessoa(pessoaId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/{pessoaId}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> salvarFotosDePessoa(@PathVariable Integer pessoaId, @RequestParam("fotos") MultipartFile[] fotos) {
        pessoaService.salvarFotos(pessoaId, fotos);

        return new ResponseEntity<>("Foto armazenada com sucesso!", HttpStatus.CREATED);
    }

    @GetMapping("/{pessoaId}/foto")
    public ResponseEntity<List<String>> recuperarFotosDePessoa(@PathVariable Integer pessoaId) {
        List<String> linksFotos = pessoaService.recuperarFotos(pessoaId);

        return new ResponseEntity<>(linksFotos, HttpStatus.OK);
    }

    @DeleteMapping("/foto")
    public ResponseEntity<?> excluirFotosDePessoa(@RequestBody List<String> hashesFotos) {
        pessoaService.excluirFotos(hashesFotos);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
