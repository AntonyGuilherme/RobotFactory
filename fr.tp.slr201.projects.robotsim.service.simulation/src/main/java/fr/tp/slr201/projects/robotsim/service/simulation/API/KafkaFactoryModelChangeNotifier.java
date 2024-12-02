package fr.tp.slr201.projects.robotsim.service.simulation.API;

import java.util.logging.Logger;

import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import fr.tp.inf112.projects.robotsim.infrasturcture.LocalNotifier;
import fr.tp.inf112.projects.robotsim.infrasturcture.SimulationServiceUtils;
import fr.tp.inf112.projects.robotsim.model.Factory;

public class KafkaFactoryModelChangeNotifier extends LocalNotifier {

	private Factory factory;
	private KafkaTemplate<String, Factory> simulationEventTemplate;
	private Logger LOGGER = Logger.getLogger("KAFKA_NOTIFIER");

	public KafkaFactoryModelChangeNotifier(Factory factory, KafkaTemplate<String, Factory> simulationEventTemplate) {
		this.factory = factory;

		// creating the topic simulation-<factoryId>
		TopicBuilder.name(SimulationServiceUtils.getTopicName(factory)).build();

		this.simulationEventTemplate = simulationEventTemplate;
	}

	@Override
	public void notifyObservers() {
		final Message<Factory> factoryMessage = MessageBuilder.withPayload(factory)
				.setHeader(KafkaHeaders.TOPIC, "simulation-" + factory.getId()).build();

		// sending the message to the topic
		var sendResult = simulationEventTemplate.send(factoryMessage);

		sendResult.whenComplete((result, ex) -> {
			if (ex != null) {
				LOGGER.warning(ex.getMessage());
			}
		});
	}
}
