package fr.tp.inf112.projects.robotsim.infrasturcture;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import fr.tp.inf112.projects.robotsim.app.RemoteSimulationController;
import fr.tp.inf112.projects.robotsim.model.Factory;

public class FactorySimulationEventConsumer {

	private final KafkaConsumer<String, String> consumer;
	private final RemoteSimulationController controller;
	private final Logger LOGGER = Logger.getLogger("KAFKA");
	private final FactorySerialyzer serialyzer = new FactorySerialyzer();

	public FactorySimulationEventConsumer(final RemoteSimulationController controller, final Factory factory) {
		this.controller = controller;
		
		final Properties props = SimulationServiceUtils.getDefaultConsumerProperties();
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		
		this.consumer = new KafkaConsumer<>(props);
		
		final String topicName = SimulationServiceUtils.getTopicName(factory);
		this.consumer.subscribe(Collections.singletonList(topicName));
		LOGGER.info("Consumer Register");
	}
	
	public void redefineTopic(Factory factory) {
		consumer.unsubscribe();
		
		final String topicName = SimulationServiceUtils.getTopicName(factory);
		this.consumer.subscribe(Collections.singletonList(topicName));
		LOGGER.info("Consumer Register");
	}

	public void consumeMessages() {
		try {
			LOGGER.info("KAFKA - Consuming messages proccess");
			
			while (controller.isAnimationRunning()) {
				
				// getting the factory updates in the topic
				final ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
				
				for (final ConsumerRecord<String, String> record : records) {
					
					// parsing the factory of JSON to string
					Factory factory = serialyzer.createFactoryFrom(record.value());
					
					// informing the new factory canvas version
					controller.setCanvas(factory);
					
					Thread.sleep(50);
				}
				
				consumer.commitAsync();
			}
		}
		catch (Exception e) {
			LOGGER.warning(e.getMessage());
		}
	}
}
