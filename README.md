# Sistema Integrado de Segurança e Monitoramento de Motocicletas

## 1. Introdução

### 1.1 Visão Geral
A plataforma proporciona monitoramento em tempo real para motocicletas, permitindo que os usuários registrem seus dispositivos, bloqueiem remotamente suas motos, acompanhem a localização ao vivo e recebam notificações sobre eventos importantes. A interface é intuitiva e totalmente responsiva, garantindo uma experiência de uso facilitada. Para realizar o monitoramento, a plataforma utiliza um dispositivo rastreador que se comunica constantemente, assegurando uma aplicação mais segura e eficiente.

![](images/plataforma.gif)

### 1.2 Objetivos
- Desenvolver interfaces responsivas
- Aprimorar o conhecimento em `Angular`
- Implementar o protocolo `MQT` a comunicação dispositivo/servidor
- Integrar o `Google Maps` com o uso de seu framework
- Gerencia múltiplas tarefas com threads no `ESP32` utilizando `RTOS`
- Utilizar o protocolo `LoRaWAN` para estabelecer comunicação em rede mesh entre dispositivos, garantindo conectividade em áreas sem cobertura de rede

## 2. Arquitetura

### 2.1 Tecnologias e Ferramentas Utilizadas
A seguir, algumas das principais tecnologias e ferramentas empregadas no projeto:

#### Front-end
- Angular 18
- Template Core UI
- Google Maps

#### Back-end
- Java 21
- Spring Boot 3.3.2
- Spring Integration MQTT
- Spring Cloud Gateway
- Spring Cloud OpenFeign

#### Dispositivo Rastreador (ESP32)
- TinyGPSPlus: Biblioteca para processar dados recebidos de módulos GPS
- PubSubClient: Biblioteca para comunicação via protocolo MQTT
- FreeRTOS: Biblioteca que gerencia múltiplas tarefas com threads
- LoRa (Ebyte E32): Módulo para comunicação de longo alcance

#### Banco de dados
- PostgreSQL
- MongoDB

#### Infra
- Docker
- Docker Compose

### 2.2 Microserviços
Um resumo das funções atendidas por cada projeto
- [gateway-api](gateway-api/README.md)
- [mototrace-app](mototrace-app/README.md)
- [mototrace-api](mototrace-api/README.md)
- [worker-position](worker-position/README.md)
- [mototrace-device](mototrace-device/README.md)

### 2.3 Diagrama de Arquitetura
![](images/diagrama.gif)

## 3. Dispositivo Rastreador
O dispositivo foi montado em uma protoboard, utilizando um ESP32, um módulo GPS, um módulo LoRa e um sensor acelerômetro. Ele conta com dois LEDs indicadores: um para sinalizar o bloqueio físico e outro para o bloqueio lógico da motocicleta.

![](images/dispositivo.jpeg)

## 4. Instalação 

### 4.1 Pré-requisitos
Certifique-se de que o Docker está instalado e funcionando corretamente em seu sistema. Verifique se as portas 8080, 8090, 8091, e 8092 não estão atualmente em uso.

### 4.2 Instruções de Instalação
Execute o seguinte comando para criar e iniciar os contêineres necessários:
```sh
docker compose up --build -d
```

### 4.3 Acesso à Aplicação
```
http://localhost:8080
```

### 4.4 Compilação do Código para o Dispositivo
A programação do ESP32 foi realizada utilizando a ferramenta Arduino IDE. O [código fonte](mototrace-device/mototrace-device.ino), pode ser compilado e transferido para o dispositivo através dessa IDE.

## 5. Roadmap

### 5.1 Fases Concluídas
- [x] Definição dos requisitos do projeto
- [x] Seleção das tecnologias a serem utilizadas
- [x] Desenvolvimento da interface do usuário
- [x] Desenvolvimento do back-end
- [x] Programação do dispositivo de rastreamento
- [x] Implementação da comunicação LoRaWAN entre dispositivos em áreas sem cobertura de rede
- [x] Elaboração da documentação do projeto

### 5.2 Próximas Etapas
- [ ] Integração do módulo GSM para utilizar a rede de telefonia móvel
- [ ] Implementação de autenticação e autorização de usuários
- [ ] Criptografia da comunicação entre dispositivos

