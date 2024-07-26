CREATE TABLE enderecos.endereco(
    end_id SERIAL PRIMARY KEY,
    end_tipo_logradouro VARCHAR(50) NOT NULL ,
    end_logradouro VARCHAR(200) NOT NULL ,
    end_numero INTEGER NOT NULL ,
    end_bairro VARCHAR(100) NOT NULL,
    cid_id INTEGER,
    CONSTRAINT fk_cidade FOREIGN KEY (cid_id) REFERENCES enderecos.cidade (cid_id)
);