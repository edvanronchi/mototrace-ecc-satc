# Documentação

## Introdução
A API tem como função gerenciar os dispositivos rastreadores, permitindo o cadastro de novos dispositivos e informaçõoes sobre eles. Utiliza postgreSQL para armazenamento de dados.

## Pré-requisitos
Antes de começar, certifique-se de que seu ambiente de desenvolvimento inclui o `Java 21` e o `Apache Maven`.

### Instalação

``` bash
mvn clean install
```

### Uso básico

``` bash
mvn spring-boot:run
```

Após a conclusão, acesse [http://localhost:8081/api/v1](http://localhost:8081/api/v1)

### Build

Para compilar o projeto e gerar o pacote JAR, execute:

```bash
mvn clean package
```
