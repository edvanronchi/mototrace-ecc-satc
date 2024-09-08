#include <WiFi.h>
#include <Wire.h>
#include <MPU6050.h>
#include <TinyGPSPlus.h>
#include <ArduinoJson.h>
#include <PubSubClient.h>
#include <HardwareSerial.h>

const String DISPOSITIVO_ID = "256b1d43-d7f4-468b-a597-74aa6f27033f";

const int LIMITE_ROTACAO_PARA_NOTIFICACAO = 10000;
const int SEGUNDOS_ENVIO_CORDENADAS = 5;
const int SEGUNDOS_ESPERA_PROXIMA_MOVIMENTACAO = 10 ;

// Definir os pinos I2C para o ESP32 MPU6050
const int MPU_PIN_SDA = 21;
const int MPU_PIN_SCL = 22;

// Configura a Serial2 no ESP32 (pinos RX e TX)
const int RXD2 = 16;
const int TXD2 = 17;
const double LIMITE_DIFERENCA_LOCALIZACAO = 0.00001;

// Definir o pino do LED
const int LED_PIN_NOTIFICACAO = 2;
const int LED_PIN_BLOQUEIO = 4;
const int LED_PIN_MOVIMENTACAO = 5;

// Configurações da rede Wi-Fi
// const char* WIFI_SSID = "RONCHI";
// const char* WIFI_PASSWORD = "umdoistres";

const char* WIFI_SSID = "iPhone";
const char* WIFI_PASSWORD = "123456789";

// Configurações do broker MQTT
const char* MQTT_SERVER = "broker.hivemq.com";
const int MQTT_PORT = 1883;
const char* MQTT_TOPIC_CORDENADA = "topic-mototrace-cordenada";
const char* MQTT_TOPIC_COMUNICACAO = "topic-mototrace-comunicacao";

//Variaveis globais
bool bloqueado = false;
bool notificacaoAtivada = false;
double globalLatitude = 0.0;
double globalLongitude = 0.0;
double globalLatitudeEnviada = 0.0;
double globalLongitudeEnviada = 0.0;

MPU6050 mpu;
TinyGPSPlus gps;
WiFiClient espClientCordenada;
WiFiClient espClientComunicacao;
PubSubClient clientCordenada(espClientCordenada);
PubSubClient clientComunicacao(espClientComunicacao);
HardwareSerial SerialGPS(2);
SemaphoreHandle_t xMutex;

void setupWifi() {
  Serial.print("Conectando-se a ");
  Serial.println(WIFI_SSID);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi conectado");
  Serial.println("Endereço IP: ");
  Serial.println(WiFi.localIP());
}

void setupMpu() {
  Serial.println("Inicializando MPU6050...");
  mpu.initialize();

  if (mpu.testConnection()) {
    Serial.println("Conexão com MPU6050 bem-sucedida");
  } else {
    Serial.println("Falha na conexão com MPU6050");
  }
}

String montarMensagemRecebida(byte* payload, unsigned int length) {
  String receivedMessage = "";
  for (int i = 0; i < length; i++) {
    receivedMessage += (char)payload[i];
  }
  return receivedMessage;
}

void listenerComunicacao(char* topic, byte* payload, unsigned int length) {
  String mensagemRecebida = montarMensagemRecebida(payload, length);

  StaticJsonDocument<200> mensagem;
  DeserializationError error = deserializeJson(mensagem, mensagemRecebida);

  if (error) {
    Serial.print(F("Falha ao fazer parse do JSON: "));
    Serial.println(error.f_str());
    return;
  }

  String codigoDispositivo = mensagem["codigoDispositivo"];
  String acao = mensagem["acao"];
  String origem = mensagem["origem"];

  if (DISPOSITIVO_ID != codigoDispositivo || origem == "DISPOSITIVO") {
    return;
  }

  Serial.println("Mensagem recebida: " + mensagemRecebida);

  gerarAcao(acao);
  enviarMensagemRetorno(mensagem);
}

void gerarAcao(String acao) {
  if (acao.equals("BLOQUEAR")) {
    digitalWrite(LED_PIN_BLOQUEIO, HIGH);
    bloqueado = true;

  } else if (acao.equals("DESBLOQUEAR")) {
    digitalWrite(LED_PIN_BLOQUEIO, LOW);
    bloqueado = false;

  } else if (acao.equals("ATIVAR_NOTIFICACAO")) {
    digitalWrite(LED_PIN_NOTIFICACAO, HIGH);
    notificacaoAtivada = true;

  } else if (acao.equals("DESATIVAR_NOTIFICACAO")) {
    digitalWrite(LED_PIN_NOTIFICACAO, LOW);
    notificacaoAtivada = false;

  } else {
    Serial.println("Ação desconhecida!");
  }
}

void enviarMensagemRetorno(StaticJsonDocument<200> mensagem) {
  mensagem["origem"] = "DISPOSITIVO";
  mensagem["tipo"] = "BLOQUEIO";

  String mensagemEnvio;
  serializeJson(mensagem, mensagemEnvio);

  clientComunicacao.publish(MQTT_TOPIC_COMUNICACAO, mensagemEnvio.c_str());
}

void reconnectCordenada() {
  while (!clientCordenada.connected()) {
    Serial.println("#cordenada: Conectando ao broker MQTT...");
    String clientId = String(DISPOSITIVO_ID) + "-cordenada";

    if (clientCordenada.connect(clientId.c_str())) {
      Serial.println("#cordenada: Conectado");
      return;
    }

    Serial.print("#cordenada: falhou, rc=");
    Serial.print(clientCordenada.state());
  }
}

void reconnectComunicacao() {
  while (!clientComunicacao.connected()) {
    Serial.println("#comunicacao: Conectando ao broker MQTT...");
    String clientId = String(DISPOSITIVO_ID) + "-comunicacao";

    if (clientComunicacao.connect(clientId.c_str())) {
      Serial.println("#comunicacao: Conectado");
      clientComunicacao.subscribe(MQTT_TOPIC_COMUNICACAO);
      return;
    }

    Serial.print("#comunicacao: falha, rc=");
    Serial.print(clientComunicacao.state());
  }
}

String montarMensagemCordenada() {
  StaticJsonDocument<200> doc;

  doc["codigoDispositivo"] = DISPOSITIVO_ID;
  doc["latitude"] = globalLatitude;
  doc["longitude"] = globalLongitude;

  String mensagem;
  serializeJson(doc, mensagem);

  return mensagem;
}

String montarMensagemNotificacaoMovimentacao() {
  StaticJsonDocument<200> doc;

  doc["codigoDispositivo"] = DISPOSITIVO_ID;
  doc["origem"] = "DISPOSITIVO";
  doc["tipo"] = "NOTIFICACAO";

  String mensagem;
  serializeJson(doc, mensagem);

  return mensagem;
}

void enviarNotificacao() {
  Serial.println("Publicando mensagem de notificação de movimentação: ");
  String mensagemEnvio = montarMensagemNotificacaoMovimentacao();
  clientComunicacao.publish(MQTT_TOPIC_COMUNICACAO, mensagemEnvio.c_str());
  digitalWrite(LED_PIN_MOVIMENTACAO, HIGH);
}

void EnviarCordenadas(void* parameter) {
  for (;;) {
    if (!clientCordenada.connected()) {
      reconnectCordenada();
    }

    clientCordenada.loop();

    if (globalLatitude == 0 || globalLongitude == 0) {
      Serial.println("Não foi encontrado a localização");
      vTaskDelay((SEGUNDOS_ENVIO_CORDENADAS * 2 * 1000) / portTICK_PERIOD_MS);
      continue;
    }

    double deltaLat = abs(globalLatitude - globalLatitudeEnviada);
    double deltaLng = abs(globalLongitude - globalLongitudeEnviada);

    if (deltaLat <= LIMITE_DIFERENCA_LOCALIZACAO && deltaLng <= LIMITE_DIFERENCA_LOCALIZACAO) {
      Serial.println("Localização está no mesmo lugar");
      vTaskDelay((SEGUNDOS_ENVIO_CORDENADAS * 2 * 1000) / portTICK_PERIOD_MS);
      continue;
    }

    Serial.println("Publicando mensagem de cordenada: ");
    String cordenada = montarMensagemCordenada();

    clientCordenada.publish(MQTT_TOPIC_CORDENADA, cordenada.c_str());

    globalLatitudeEnviada = globalLatitude;
    globalLongitudeEnviada = globalLongitude;

    vTaskDelay((SEGUNDOS_ENVIO_CORDENADAS * 1000) / portTICK_PERIOD_MS);
  }
}

void ComunicacaoServidor(void* parameter) {
  for (;;) {
    if (!clientComunicacao.connected()) {
      reconnectComunicacao();
    }
    clientComunicacao.loop();
    vTaskDelay(250 / portTICK_PERIOD_MS);
  }
}

void VerificarMovimentacao(void* parameter) {
  for (;;) {
    int16_t gx, gy, gz;
    mpu.getRotation(&gx, &gy, &gz);

    long rotacaoTotal = abs(gx) + abs(gy) + abs(gz);

    if (rotacaoTotal > LIMITE_ROTACAO_PARA_NOTIFICACAO && notificacaoAtivada) {
      enviarNotificacao();
      vTaskDelay((SEGUNDOS_ESPERA_PROXIMA_MOVIMENTACAO * 1000) / portTICK_PERIOD_MS);
    }

    digitalWrite(LED_PIN_MOVIMENTACAO, LOW);
    vTaskDelay(500 / portTICK_PERIOD_MS);
  }
}

void ColetarCordenadas(void* parameter) {
  for (;;) {
    if (SerialGPS.available() <= 0) {
      vTaskDelay(1000 / portTICK_PERIOD_MS);
      continue;
    }

    gps.encode(SerialGPS.read());

    if (gps.location.isValid()) {
      globalLatitude = gps.location.lat();
      globalLongitude = gps.location.lng();

      Serial.println(globalLatitude, 6);
      Serial.println(globalLongitude, 6);

      vTaskDelay(2000 / portTICK_PERIOD_MS);
    } else {
      Serial.print(".");
      vTaskDelay(1 / portTICK_PERIOD_MS);
    }
  }
}

void setup() {
  Serial.begin(115200);
  SerialGPS.begin(9600, SERIAL_8N1, RXD2, TXD2);

  Wire.begin(MPU_PIN_SDA, MPU_PIN_SCL);

  pinMode(LED_PIN_BLOQUEIO, OUTPUT);
  pinMode(LED_PIN_NOTIFICACAO, OUTPUT);
  pinMode(LED_PIN_MOVIMENTACAO, OUTPUT);

  delay(2000);

  setupWifi();
  setupMpu();

  clientCordenada.setServer(MQTT_SERVER, MQTT_PORT);
  clientComunicacao.setServer(MQTT_SERVER, MQTT_PORT);
  clientComunicacao.setCallback(listenerComunicacao);

  xTaskCreatePinnedToCore(
    EnviarCordenadas,    // Função de tarefa
    "EnviarCordenadas",  // Nome da tarefa
    8192,                // Tamanho da pilha (em bytes)
    NULL,                // Parâmetro para passar para a tarefa
    1,                   // Prioridade da tarefa
    NULL,
    1);

  xTaskCreatePinnedToCore(
    ColetarCordenadas,
    "ColetarCordenadas",
    8192,
    NULL,
    1,
    NULL,
    1);

  xTaskCreatePinnedToCore(
    ComunicacaoServidor,
    "ComunicacaoServidor",
    8192,
    NULL,
    1,
    NULL,
    1);

  xTaskCreatePinnedToCore(
    VerificarMovimentacao,
    "VerificarMovimentacao",
    8192,
    NULL,
    1,
    NULL,
    1);
}

void loop() {}
