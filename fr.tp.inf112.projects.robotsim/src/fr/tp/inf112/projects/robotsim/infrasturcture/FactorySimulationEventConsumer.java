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
	private final String SIMULATION_ID;

	public FactorySimulationEventConsumer(final RemoteSimulationController controller, final String SIMULATION_ID) {
		this.controller = controller;
		this.SIMULATION_ID = SIMULATION_ID;
		
		final Properties props = SimulationServiceUtils.getDefaultConsumerProperties();
		
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		
		this.consumer = new KafkaConsumer<>(props);
		
		final String topicName = SimulationServiceUtils.getTopicName(SIMULATION_ID);
		this.consumer.subscribe(Collections.singletonList(topicName));
		LOGGER.info(this.consumer.subscription().toString());
	}

	public void consumeMessages() {
		try {
			LOGGER.info("KAFKA 1");
			while (controller.isAnimationRunning()) {
				final ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
				
				LOGGER.info(String.format("Records founded in %d %s", records.count(),  SimulationServiceUtils.getTopicName(SIMULATION_ID)));
				
				for (final ConsumerRecord<String, String> record : records) {
					Factory factory = serialyzer.createFactoryFrom(record.value());
					LOGGER.info(String.format("KAFKA %s", factory));
					
					controller.setCanvas(factory);
				}
				
				consumer.commitAsync();
				
				Thread.sleep(200);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			LOGGER.warning(e.getMessage());
		}
		 finally {
			//consumer.close();
			LOGGER.warning("KAFKA CLOSE");
		}
	}

	class SimulationServiceUtils {
		public static final String BOOTSTRAP_SERVERS = "localhost:9092";
		private static final String GROUP_ID = "Factory-Simulation-Group";
		private static final String AUTO_OFFSET_RESET = "earliest";
		private static final String TOPIC = "simulation-";

		public static String getTopicName(String SIMULATION_ID) {
			return TOPIC + SIMULATION_ID;
		}

		public static Properties getDefaultConsumerProperties() {
			final Properties props = new Properties();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
			props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
			props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AUTO_OFFSET_RESET);
			props.put("enable.auto.commit", "true");
	        props.put("auto.commit.interval.ms", "1000");
			return props;
		}
	}

}
