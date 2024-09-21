# Documentação

## Introdução
Este dispositivo de rastreamento utiliza o microcontrolador ESP32, integrando sensores como MPU6050 e módulo GPS. Ele se comunica via MQTT com um servidor e LoRaWAN, garantindo envio de dados mesmo sem internet. Com recursos de bloqueio remoto, notificações de movimentação e envio periódico de coordenadas.

## Pré-requisitos
Antes de começar, certifique-se de que seu ambiente de desenvolvimento inclui o seguinte:

### Hardware
- ESP32: Microcontrolador que suporta Wi-Fi e Bluetooth.
- MPU6050: Sensor de movimento para detecção de rotação.
- Módulo GPS: Para localização em tempo real.
- Módulo LoRa: Para comunicação em longas distâncias.
- LEDs: Para notificações visuais.

### Software
- Arduino IDE: Para programar o ESP32.
- Bibliotecas necessárias: As seguintes bibliotecas devem ser instaladas no Arduino IDE:
  - MPU6050
  - TinyGPSPlus
  - ArduinoJson
  - PubSubClient

### Instalação
Abra o Arduino IDE e carregue o código do dispositivo. Configure as credenciais do Wi-Fi e as informações do broker MQTT no código:

```
const char* WIFI_SSID = "WIFI_SSID";
const char* WIFI_PASSWORD = "WIFI_PASSWORD";
const char* MQTT_SERVER = "broker.hivemq.com";
const int MQTT_PORT = 1883;
```
Após isso, compile o código e verifique a saída no monitor serial para confirmar a conexão bem-sucedida.

## Comunicação Dispositivo/Servidor
O dispositivo se comunica com o servidor utilizando o protocolo MQTT, permitindo o envio e recebimento de comandos de forma eficiente.

### Comandos via MQTT

Os comandos são enviados ao tópico `topic-mototrace-comunicacao`, e o dispositivo utiliza o mesmo tópico para retornar a confirmação de execução dos comandos. Os comandos disponíveis incluem:
- BLOQUEAR: Bloqueia o dispositivo.
- DESBLOQUEAR: Desbloqueia o dispositivo.
- ATIVAR_NOTIFICACAO: Ativa as notificações de movimentação.
- DESATIVAR_NOTIFICACAO: Desativa as notificações de movimentação.

### Envio de Coordenadas
O dispositivo envia suas coordenadas GPS a cada 10 segundos para o tópico `topic-mototrace-coordenada`, garantindo que a localização esteja sempre atualizada.

## Comunicação entre Dispositivos
A comunicação entre dispositivos é realizada via LoRaWAN, permitindo o envio de dados mesmo em áreas sem cobertura de internet.