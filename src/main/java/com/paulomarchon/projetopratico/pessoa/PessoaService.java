package com.paulomarchon.projetopratico.pessoa;

import com.paulomarchon.projetopratico.endereco.EnderecoService;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoAlteracaoEndereco;
import com.paulomarchon.projetopratico.endereco.dto.RequisicaoCadastroEndereco;
import com.paulomarchon.projetopratico.exception.RecursoNaoEncontradoException;
import com.paulomarchon.projetopratico.pessoa.dto.PessoaDto;
import com.paulomarchon.projetopratico.pessoa.dto.RequisicaoAlteracaoPessoa;
import com.paulomarchon.projetopratico.pessoa.dto.RequisicaoCadastroPessoa;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PessoaService {
    private final PessoaDao pessoaDao;
    private final PessoaDtoMapper pessoaDtoMapper;
    private final EnderecoService enderecoService;

    public PessoaService(@Qualifier("pessoa-jpa") PessoaDao pessoaDao, PessoaDtoMapper pessoaDtoMapper, EnderecoService enderecoService) {
        this.pessoaDao = pessoaDao;
        this.pessoaDtoMapper = pessoaDtoMapper;
        this.enderecoService = enderecoService;
    }

    public Page<PessoaDto> buscarTodasPessoas(Integer pagina, Integer tamanho) {
        Pageable pageRequest = PageRequest.of(pagina, tamanho, Sort.by("nome"));

        Page<Pessoa> paginaPessoas = pessoaDao.buscarTodasPessoas(pageRequest);

        return paginaPessoas.map(pessoaDtoMapper);
    }

    public Page<PessoaDto> buscarPessoaPorNome(String nome, Integer pagina, Integer tamanho) {
        Pageable pageRequest = PageRequest.of(pagina, tamanho, Sort.by("nome"));

        Page<Pessoa> paginaPessoas = pessoaDao.buscarPessoaPorNome(nome, pageRequest);

        return paginaPessoas.map(pessoaDtoMapper);
    }

    @Transactional
    public PessoaDto cadastrarPessoa(RequisicaoCadastroPessoa cadastroPessoa) {
        Pessoa pessoa = new Pessoa(
                cadastroPessoa.nome(),
                cadastroPessoa.dataNascimento(),
                cadastroPessoa.sexo(),
                cadastroPessoa.nomeMae(),
                cadastroPessoa.nomePai()
        );

        return pessoaDtoMapper.apply(
                pessoaDao.cadastrarPessoa(pessoa)
        );
    }

    @Transactional
    public void alterarPessoa(Integer pessoaId, RequisicaoAlteracaoPessoa alteracaoPessoa) {
        Pessoa pessoa = pessoaDao.buscarPessoaPorReferenciaDeId(pessoaId);

        boolean alteracaoEfetivada = false;

        if (alteracaoPessoa.nome() != null && !alteracaoPessoa.nome().equals(pessoa.getNome())) {
            pessoa.setNome(alteracaoPessoa.nome());
            alteracaoEfetivada = true;
        }

        if (alteracaoPessoa.dataNascimento() != null && !alteracaoPessoa.dataNascimento().equals(pessoa.getDataNascimento())) {
            pessoa.setDataNascimento(alteracaoPessoa.dataNascimento());
            alteracaoEfetivada = true;
        }

        if (alteracaoPessoa.sexo() != null && !alteracaoPessoa.sexo().equals(pessoa.getSexo())) {
            pessoa.setSexo(alteracaoPessoa.sexo());
            alteracaoEfetivada = true;
        }

        if (alteracaoPessoa.nomeMae() != null && !alteracaoPessoa.nomeMae().equals(pessoa.getNomeMae())) {
            pessoa.setNomeMae(alteracaoPessoa.nomeMae());
            alteracaoEfetivada = true;
        }

        if (alteracaoPessoa.nomePai() != null && !alteracaoPessoa.nomePai().equals(pessoa.getNomePai())) {
            pessoa.setNomePai(alteracaoPessoa.nomePai());
            alteracaoEfetivada = true;
        }

        if (!alteracaoEfetivada)
            throw new IllegalArgumentException("Erro ao alterar dados de pessoa, nenhuma informacao alterada");

        pessoaDao.alterarPessoa(pessoa);
    }

    public void cadastrarEnderecoDePessoa(Integer pessoaId, RequisicaoCadastroEndereco cadastroEndereco) {
        Pessoa pessoa = pessoaDao.buscarPessoaPorReferenciaDeId(pessoaId);

        pessoa.setEndereco(
                enderecoService.salvarEndereco(cadastroEndereco)
        );
    }

    public void alterarEnderecoDePessoa(Integer pessoaId, RequisicaoAlteracaoEndereco alteracaoEndereco) {
        Pessoa pessoa = pessoaDao.buscarPessoaPorReferenciaDeId(pessoaId);

        enderecoService.alterarEndereco(
                pessoa.getEndereco().getId(),
                alteracaoEndereco
        );
    }

    @Transactional
    public void excluirPessoa(Integer pessoaId) {
        if (!pessoaDao.existePessoa(pessoaId))
            throw new RecursoNaoEncontradoException("Pessoa com id [%s] nao encontrada".formatted(pessoaId));
        pessoaDao.excluirPessoa(pessoaId);
    }
}
