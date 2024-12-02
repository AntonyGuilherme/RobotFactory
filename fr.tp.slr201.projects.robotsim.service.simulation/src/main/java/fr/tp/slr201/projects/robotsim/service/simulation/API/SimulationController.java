package fr.tp.slr201.projects.robotsim.service.simulation.API;

import fr.tp.inf112.projects.robotsim.infrasturcture.FactoryRepository;
import fr.tp.inf112.projects.robotsim.infrasturcture.FactorySerialyzer;
import fr.tp.inf112.projects.robotsim.model.Factory;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimulationController {

	private static final Hashtable<String, Factory> simulations = new Hashtable<String, Factory>();
	private static final Logger LOGGER = Logger.getLogger(SimulationController.class.getName());
	private final FactoryRepository persistenceManager = new FactoryRepository(LOGGER);
	private final FactorySerialyzer serialyzer = new FactorySerialyzer();
	
	@Autowired
	private KafkaTemplate<String, Factory> simulationEventTemplate;

	@GetMapping("/start-simulation")
	public boolean startSimulation(@RequestParam String simulationId) {
		
				
		try {
			LOGGER.info(String.format("STARTING SIMULATION %s", simulationId));

			// reading the factory
			if (simulations.containsKey(simulationId)) {
				final Factory factory = simulations.get(simulationId);
				
				// setting the notifier to inform about the simulation
				final KafkaFactoryModelChangeNotifier notifier = new KafkaFactoryModelChangeNotifier(factory, simulationEventTemplate);
				factory.setNotifier(notifier);
				
				// initiating the simulation of the factory in a second processor
				// to not block the main thread
				new Thread(() -> factory.startSimulation()).start();
				
				return true;
			}

			LOGGER.info(String.format("FACTORY %s NOT FOUNDED", simulationId));
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
		}

		return false;
	}

	@GetMapping("/retrieve-simulation")
	public String retrieveSimulation(@RequestParam String simulationId) {
		LOGGER.info(String.format("GETTING SIMULATION %s", simulationId));

		if (!simulations.containsKey(simulationId)) {

			// if the factory no exists create a default factory
			Factory factoryOrDefault = persistenceManager.read(simulationId);

			// adding the factory to be simulated
			simulations.put(simulationId, factoryOrDefault);
		}

		// serializing the factory and sending to the requester
		return serialyzer.toJSON(simulations.get(simulationId));
	}

	@GetMapping("/stop-simulation")
	public void stopSimulation(@RequestParam String simulationId) {
		LOGGER.info(String.format("STOPPING SIMULATION %s", simulationId));
		
		// stopping the simulation only if it was read before
		if (simulations.containsKey(simulationId)) {
			Factory factory = simulations.get(simulationId);
			factory.stopSimulation();
		}
	}

}
