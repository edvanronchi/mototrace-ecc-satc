package dev.edvanronchi.workerposition.infra.config;

import dev.edvanronchi.workerposition.application.services.MqttListenerService;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.Objects;

@Configuration
public class MqttListenerConfig {

    @Value("${variaveis.mqtt.broker-url}")
    private String MQTT_BROKER_URL;

    @Value("${variaveis.mqtt.client-id-listener}")
    private String MQTT_CLIENT_ID;

    private final MqttListenerService mqttListenerService;

    public MqttListenerConfig(MqttListenerService mqttListenerService) {
        this.mqttListenerService = mqttListenerService;
    }

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{MQTT_BROKER_URL});
        options.setKeepAliveInterval(60);
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        return options;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactoryListener() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions());
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(MQTT_CLIENT_ID, mqttClientFactoryListener(), MqttTopics.COMUNICACAO, MqttTopics.COORDENADA);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> {
            String topic = Objects.requireNonNull(message.getHeaders().get("mqtt_receivedTopic")).toString();
            String payload = message.getPayload().toString();
            mqttListenerService.processar(topic, payload);
        };
    }
}
