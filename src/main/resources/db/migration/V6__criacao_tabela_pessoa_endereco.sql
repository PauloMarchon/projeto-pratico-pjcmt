CREATE TABLE pessoas.pessoa_endereco(
    pes_id INTEGER ,
    end_id INTEGER ,
    CONSTRAINT fk_pessoa FOREIGN KEY (pes_id) REFERENCES pessoas.pessoa (pes_id) ,
    CONSTRAINT fk_endereco FOREIGN KEY (end_id) REFERENCES enderecos.endereco (end_id)
);