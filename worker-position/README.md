# Documentação

## Introdução
API gerencia a comunicação com os dispositivos, permitindo o envio de comandos e o recebimento de coordenadas. Ela utiliza o protocolo MQTT para facilitar a troca de informações e armazena os dados no MongoDB.

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

Após a conclusão, acesse [http://localhost:8092/api/v1](http://localhost:8092/api/v1)

### Build

Para compilar o projeto e gerar o pacote JAR, execute:

```bash
mvn clean package
```
