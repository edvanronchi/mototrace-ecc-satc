# Documentação

## Introdução
O API Gateway é encarregado de gerenciar o fluxo de requisições externas para os serviços internos, atuando como um ponto de entrada centralizado.

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

Após a conclusão, acesse [http://localhost:8090/api/v1](http://localhost:8090/api/v1)

### Build

Para compilar o projeto e gerar o pacote JAR, execute:

```bash
mvn clean package
```
