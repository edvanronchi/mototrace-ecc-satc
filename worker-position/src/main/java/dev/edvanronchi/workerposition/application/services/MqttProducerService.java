package dev.edvanronchi.workerposition.application.services;

import dev.edvanronchi.workerposition.infra.gateway.MqttGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqttProducerService {

    private final MqttGateway mqttGateway;

    public MqttProducerService(MqttGateway mqttGateway) {
        this.mqttGateway = mqttGateway;
    }

    public void enviarMensagem(String topic, String mensagem) {
        mqttGateway.sendToMqtt(topic, mensagem);
    }
}
