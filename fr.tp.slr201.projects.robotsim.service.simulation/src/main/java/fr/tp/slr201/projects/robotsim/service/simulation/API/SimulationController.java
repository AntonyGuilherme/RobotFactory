package fr.tp.slr201.projects.robotsim.service.simulation.API;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.FactorySerialyzer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimulationController {
	
	private static final Hashtable<String, Factory> simulations = new Hashtable<String, Factory>();
	private final FactoryRepository persistenceManager = new FactoryRepository();
	private static final Logger LOGGER = Logger.getLogger(SimulationController.class.getName());
	private final FactorySerialyzer serialyzer = new FactorySerialyzer();
	
	
	@GetMapping("/start-simulation")
	public boolean startSimulation(@RequestParam String simulationId) {
		try {
			LOGGER.info(String.format("STARTING SIMULATION %s", simulationId));
			final Factory factory = simulations.get(simulationId);
			
			new Thread(() -> factory.startSimulation()).start();
			
			simulations.put(simulationId, factory); 
			
			return true;
		}
		catch (Exception e) {
			LOGGER.info(e.getMessage());
			return false;
		}
	}
	
	@GetMapping("/retrieve-simulation")
	public String retrieveSimulation(@RequestParam String simulationId) {
		LOGGER.info(String.format("GETTING SIMULATION %s", simulationId));
		
		if (simulations.containsKey(simulationId)) {
			return serialyzer.toJSON(simulations.get(simulationId));
		} 
		else {
			Factory factoryOrDefault = persistenceManager.read(simulationId);
			simulations.put(simulationId, factoryOrDefault);
			
			return serialyzer.toJSON(factoryOrDefault);
		}
	}
	
	@GetMapping("/stop-simulation")
	public void stopSimulation(@RequestParam String simulationId) {
		LOGGER.info(String.format("STOPPING SIMULATION %s", simulationId));
		
		if (simulations.containsKey(simulationId)) {
			Factory factory = simulations.get(simulationId);
			factory.stopSimulation();	
		}
	}
	
	class FactoryRepository {
		
		public Factory read(String canvasId) {
			
			try (Socket socket = new Socket()) {
				
				InetAddress host = InetAddress.getLocalHost();
				InetSocketAddress adress = new InetSocketAddress(host, 80);
				
				socket.connect(adress, 1000);
				
				OutputStream socketOutputStream = socket.getOutputStream();
				BufferedOutputStream bufferOutStream = new BufferedOutputStream(socketOutputStream);
				ObjectOutputStream writter = new ObjectOutputStream(bufferOutStream);

				writter.writeObject(canvasId);
				
				writter.flush();
				
				InputStream inputStream = socket.getInputStream();
				BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
				ObjectInputStream objectReader = new ObjectInputStream(bufferedInputStream);
				
				Object object = objectReader.readObject();
				
				System.out.println(object);
						
				writter.close();
				bufferOutStream.close();
				socketOutputStream.close();
				
				return (Factory) object;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		public void persist(Factory factory)  {
			
			try (Socket socket = new Socket()) {
				
				InetAddress host = InetAddress.getLocalHost();
				InetSocketAddress adress = new InetSocketAddress(host, 80);
				
				socket.connect(adress, 1000);
				
				
				OutputStream socketOutputStream = socket.getOutputStream();
				BufferedOutputStream bufferOutStream = new BufferedOutputStream(socketOutputStream);
				ObjectOutputStream writter = new ObjectOutputStream(bufferOutStream);
				
				writter.writeObject(factory);

				
				writter.close();
				bufferOutStream.close();
				socketOutputStream.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
