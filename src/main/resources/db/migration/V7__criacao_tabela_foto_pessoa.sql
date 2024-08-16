CREATE TABLE pessoas.foto_pessoa(
    fp_id SERIAL PRIMARY KEY ,
    pes_id INTEGER NOT NULL ,
    fp_data DATE NOT NULL ,
    fp_bucket VARCHAR(50) NOT NULL ,
    fp_hash VARCHAR(50) NOT NULL ,
    CONSTRAINT fk_pessoa FOREIGN KEY (pes_id) REFERENCES pessoas.pessoa(pes_id)
);