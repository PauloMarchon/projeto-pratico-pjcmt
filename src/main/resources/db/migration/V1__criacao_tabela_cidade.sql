CREATE SCHEMA IF NOT EXISTS enderecos;

CREATE TABLE enderecos.cidade(
    cid_id SERIAL PRIMARY KEY ,
    cid_nome VARCHAR(200) NOT NULL ,
    cid_uf CHAR(2) NOT NULL
);