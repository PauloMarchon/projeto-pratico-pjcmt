package com.paulomarchon.projetopratico.endereco.dto;

public record EnderecoDto (
        String tipoLogradouro,
        String logradouro,
        Integer numero,
        String bairro,
        String cidade
){
    @Override
    public String toString() {
        return "Endereco: " + tipoLogradouro+ " " + logradouro + ", " + numero + ", " + bairro + " | " + cidade;
    }
}
