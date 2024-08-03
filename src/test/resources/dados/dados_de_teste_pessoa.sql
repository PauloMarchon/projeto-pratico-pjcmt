TRUNCATE TABLE pessoas.pessoa CASCADE;

ALTER SEQUENCE pessoas.pessoa_pes_id_seq RESTART WITH 100;

INSERT INTO pessoas.pessoa(pes_id, pes_nome, pes_data_nascimento, pes_sexo, pes_mae, pes_pai) VALUES
(1, 'JOAO DA SILVA', '1990-08-15', 'MASCULINO' ,'MARIA OLIVEIRA', 'JOSE DA SILVA'),
(2, 'ANA SOUZA', '1985-03-20', 'FEMININO', 'LUCIA SOUZA', 'CARLOS SOUZA'),
(3, 'PEDRO ALVES', '1992-11-10', 'MASCULINO' ,'SILVIA ALVES', 'ANTONIO ALVES'),
(4, 'CAROLINA RODRIGUES', '1987-06-25', 'FEMININO', 'ANA RODRIGUES', 'MARCOS RODRIGUES'),
(5, 'LUCAS FERREIRA', '1994-02-05', 'MASCULINO' ,'SANDRA FERREIRA', 'FERNANDO FERREIRA'),
(6, 'ISABEL CRUZ', '1989-09-18', 'FEMININO', 'MARIA CRUZ', 'JOAO CRUZ'),
(7, 'GABRIELA SANTOS', '1998-04-30', 'FEMININO', 'FERNANDA SANTOS', 'RICARDO SANTOS'),
(8, 'RAFAEL OLIVEIRA', '1991-12-12', 'MASCULINO' ,'ANA OLIVEIRA', 'PAULO OLIVEIRA'),
(9, 'VITORIA ALMEIDA', '1996-10-08', 'FEMININO', 'LUIZA ALMEIDA', 'EDUARDO ALMEIDA'),
(10, 'MATEUS PEREIRA', '1986-07-02', 'MASCULINO' ,'CLAUDIA PEREIRA', 'ROBERTO PEREIRA'),
(11, 'MARINA FERREIRA', '1993-09-12', 'FEMININO', 'ANA FERREIRA', 'LUIZ FERREIRA'),
(12, 'GUSTAVO RODRIGUES', '1984-05-28', 'MASCULINO' ,'CAROLINA RODRIGUES', 'MARCOS RODRIGUES'),
(13, 'SOFIA ALMEIDA', '1997-11-03', 'FEMININO', 'LUCIA ALMEIDA', 'EDUARDO ALMEIDA'),
(14, 'FELIPE OLIVEIRA', '1988-02-17', 'MASCULINO' ,'ANA OLIVEIRA', 'PAULO OLIVEIRA'),
(15, 'LARISSA CRUZ', '1995-06-20', 'FEMININO', 'MARIA CRUZ', 'JOAO CRUZ'),
(16, 'RAFAEL SANTOS', '1999-03-08', 'MASCULINO' ,'FERNANDA SANTOS', 'RICARDO SANTOS'),
(17, 'ANA LIMA', '1986-10-22', 'FEMININO', 'CLAUDIA LIMA', 'ROBERTO LIMA'),
(18, 'PEDRO SOUZA', '1991-07-14', 'MASCULINO' ,'LUCIA SOUZA', 'CARLOS SOUZA'),
(19, 'VANESSA PEREIRA', '1994-04-05', 'FEMININO', 'CLAUDIA PEREIRA', 'ROBERTO PEREIRA'),
(20, 'MARCOS RIBEIRO', '1989-12-30', 'MASCULINO' ,'CRISTINA RIBEIRO', 'PAULO RIBEIRO');