package fr.tp.slr201.projects.robotsim.service.simulation.API;


import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import fr.tp.inf112.projects.robotsim.infrasturcture.LocalNotifier;
import fr.tp.inf112.projects.robotsim.model.Factory;

public class KafkaFactoryModelChangeNotifier extends LocalNotifier {
	
	private Factory factory;
	private KafkaTemplate<String, Factory> simulationEventTemplate;
	
	public KafkaFactoryModelChangeNotifier(Factory factory,  KafkaTemplate<String, Factory> simulationEventTemplate) {
		this.factory = factory;
		TopicBuilder.name("simulation-" + factory.getId()).build();
		this.simulationEventTemplate = simulationEventTemplate;
	}
	
		
	@Override
	public void notifyObservers() {
		final Message<Factory> factoryMessage = MessageBuilder.withPayload(factory)
				.setHeader(KafkaHeaders.TOPIC, "simulation-" + factory.getId())
				.build();
				var sendResult = simulationEventTemplate.send(factoryMessage);
				sendResult.whenComplete((result, ex) -> {
				if (ex != null) {
					throw new RuntimeException(ex);
				}
				});
	}
}
