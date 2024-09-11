#include <WiFi.h>
#include <Wire.h>
#include <MPU6050.h>
#include <TinyGPSPlus.h>
#include <ArduinoJson.h>
#include <PubSubClient.h>
#include <HardwareSerial.h>

const String DISPOSITIVO_ID = "256b1d43-d7f4-468b-a597-74aa6f27033f";

const int SEGUNDOS_ENVIO_COORDENADAS = 5;
const int SEGUNDOS_ESPERA_PROXIMA_MOVIMENTACAO = 10 ;

// Configurações do MPU6050
const int MPU_PIN_SDA = 21;
const int MPU_PIN_SCL = 22;
const int LIMITE_ROTACAO_PARA_NOTIFICACAO = 3000;

// Configurações do LoRa
const int M0 = 4;
const int M1 = 5;
const int AUX = 18;
const int RXD1 = 19;
const int TXD1 = 23;

// Configurações do GPS
const int RXD2 = 16;
const int TXD2 = 17;
const double LIMITE_DIFERENCA_LOCALIZACAO = 0.00001;

// Pinos dos LEDs
const int LED_PIN_NOTIFICACAO = 2;
const int LED_PIN_BLOQUEIO = 15;

// Configurações da rede Wi-Fi
const char* WIFI_SSID = "WIFI_SSID";
const char* WIFI_PASSWORD = "WIFI_PASSWORD";

// Configurações do broker MQTT
const char* MQTT_SERVER = "broker.hivemq.com";
const int MQTT_PORT = 1883;
const char* MQTT_TOPIC_COORDENADA = "topic-mototrace-coordenada";
const char* MQTT_TOPIC_COMUNICACAO = "topic-mototrace-comunicacao";

// Variaveis globais
bool bloqueado = false;
bool notificacaoAtivada = false;
double globalLatitude = 0.0;
double globalLongitude = 0.0;
double globalLatitudeEnviada = 0.0;
double globalLongitudeEnviada = 0.0;

MPU6050 mpu;
TinyGPSPlus gps;
WiFiClient espClientCoordenada;
WiFiClient espClientComunicacao;
PubSubClient clientCoordenada(espClientCoordenada);
PubSubClient clientComunicacao(espClientComunicacao);

HardwareSerial SerialLORA(1);
HardwareSerial SerialGPS(2);

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

void reconnectCoordenada() {
  while (!clientCoordenada.connected()) {
    Serial.println("#coordenada: Conectando ao broker MQTT...");
    String clientId = String(DISPOSITIVO_ID) + "-coordenada";

    if (clientCoordenada.connect(clientId.c_str())) {
      Serial.println("#coordenada: Conectado");
      return;
    }

    Serial.print("#coordenada: falhou, rc=");
    Serial.print(clientCoordenada.state());
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

String montarMensagemCoordenada() {
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
  
  for (int i = 0; i < 5; i++) {
    digitalWrite(LED_PIN_NOTIFICACAO, LOW);
    vTaskDelay(250 / portTICK_PERIOD_MS);

    digitalWrite(LED_PIN_NOTIFICACAO, HIGH);
    vTaskDelay(250 / portTICK_PERIOD_MS);
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

    vTaskDelay(500 / portTICK_PERIOD_MS);
  }
}

void ColetarCoordenadas(void* parameter) {
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

void EnviarCoordenadas(void* parameter) {
  for (;;) {
    if (!clientCoordenada.connected()) {
      reconnectCoordenada();
    }

    clientCoordenada.loop();

    if (globalLatitude == 0 || globalLongitude == 0) {
      Serial.println("Não foi encontrado a localização");
      vTaskDelay((SEGUNDOS_ENVIO_COORDENADAS * 2 * 1000) / portTICK_PERIOD_MS);
      continue;
    }

    double deltaLat = abs(globalLatitude - globalLatitudeEnviada);
    double deltaLng = abs(globalLongitude - globalLongitudeEnviada);

    if (deltaLat <= LIMITE_DIFERENCA_LOCALIZACAO && deltaLng <= LIMITE_DIFERENCA_LOCALIZACAO) {
      Serial.println("Localização está no mesmo lugar");
      vTaskDelay((SEGUNDOS_ENVIO_COORDENADAS * 2 * 1000) / portTICK_PERIOD_MS);
      continue;
    }

    Serial.println("Publicando mensagem de coordenada: ");
    String coordenada = montarMensagemCoordenada();

    clientCoordenada.publish(MQTT_TOPIC_COORDENADA, coordenada.c_str());

    globalLatitudeEnviada = globalLatitude;
    globalLongitudeEnviada = globalLongitude;

    vTaskDelay((SEGUNDOS_ENVIO_COORDENADAS * 1000) / portTICK_PERIOD_MS);
  }
}

void ReceberCoordenadasLoRaWAN(void* parameter) {
  for (;;) {
    vTaskDelay(1000 / portTICK_PERIOD_MS);
  }
}

void EnviarCoordenadasLoRaWAN(void* parameter) {
  for (;;) {
    if (WiFi.status() == WL_CONNECTED) {
      vTaskDelay(3000 / portTICK_PERIOD_MS);
      continue;
    }

    vTaskDelay((SEGUNDOS_ENVIO_COORDENADAS * 1000) / portTICK_PERIOD_MS);
  }
}

void setup() {
  Serial.begin(115200);
  SerialLORA.begin(9600, SERIAL_8N1, RXD1, TXD1);
  SerialGPS.begin(9600, SERIAL_8N1, RXD2, TXD2);

  Wire.begin(MPU_PIN_SDA, MPU_PIN_SCL);

  pinMode(M0, OUTPUT);
  pinMode(M1, OUTPUT);
  pinMode(AUX, INPUT);
  pinMode(LED_PIN_BLOQUEIO, OUTPUT);
  pinMode(LED_PIN_NOTIFICACAO, OUTPUT);

  digitalWrite(M0, LOW);
  digitalWrite(M1, LOW);

  delay(2000);

  setupWifi();
  setupMpu();

  clientCoordenada.setServer(MQTT_SERVER, MQTT_PORT);
  clientComunicacao.setServer(MQTT_SERVER, MQTT_PORT);
  clientComunicacao.setCallback(listenerComunicacao);

  xTaskCreatePinnedToCore(
    EnviarCoordenadas,
    "EnviarCoordenadas",
    8192,
    NULL,
    1,
    NULL,
    1
  );

  xTaskCreatePinnedToCore(
    ColetarCoordenadas,
    "ColetarCoordenadas",
    8192,
    NULL,
    1,
    NULL,
    1
  );

  xTaskCreatePinnedToCore(
    ComunicacaoServidor,
    "ComunicacaoServidor",
    8192,
    NULL,
    1,
    NULL,
    1
  );

  xTaskCreatePinnedToCore(
    VerificarMovimentacao,
    "VerificarMovimentacao",
    8192,
    NULL,
    1,
    NULL,
    1
  );

  // xTaskCreatePinnedToCore(
  //   EnviarCoordenadasLoRaWAN,
  //   "EnviarCoordenadasLoRaWAN",
  //   8192,
  //   NULL,
  //   1,
  //   NULL,
  //   1
  // );

  // xTaskCreatePinnedToCore(
  //   ReceberCoordenadasLoRaWAN,
  //   "ReceberCoordenadasLoRaWAN",
  //   8192,
  //   NULL,
  //   1,
  //   NULL,
  //   1
  // );
}

void loop() {}
