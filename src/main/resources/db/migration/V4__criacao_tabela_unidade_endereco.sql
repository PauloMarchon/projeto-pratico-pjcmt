CREATE TABLE unidades.unidade_endereco(
    unid_id INTEGER ,
    end_id INTEGER ,
    CONSTRAINT fk_unidade FOREIGN KEY (unid_id) REFERENCES unidades.unidade (unid_id) ,
    CONSTRAINT fk_endereco FOREIGN KEY (end_id) REFERENCES enderecos.endereco (end_id) ,
    PRIMARY KEY (unid_id, end_id)
);