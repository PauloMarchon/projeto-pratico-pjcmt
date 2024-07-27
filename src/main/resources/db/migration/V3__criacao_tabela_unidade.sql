CREATE SCHEMA IF NOT EXISTS unidades;

CREATE TABLE unidades.unidade(
    unid_id SERIAL PRIMARY KEY ,
    unid_nome VARCHAR(200) NOT NULL ,
    unid_sigla VARCHAR(20) NOT NULL
);