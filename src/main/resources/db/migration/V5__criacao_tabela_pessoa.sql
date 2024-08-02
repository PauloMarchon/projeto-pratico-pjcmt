CREATE SCHEMA IF NOT EXISTS pessoas;

CREATE TABLE pessoas.pessoa(
    pes_id SERIAL PRIMARY KEY ,
    pes_nome VARCHAR(200) NOT NULL ,
    pes_data_nascimento DATE NOT NULL ,
    pes_sexo VARCHAR(9) NOT NULL ,
    pes_mae VARCHAR(200) NOT NULL ,
    pes_pai VARCHAR(200) NOT NULL
);