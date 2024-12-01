package fr.tp.inf112.projects.robotsim.infrasturcture;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

import fr.tp.inf112.projects.robotsim.model.Factory;

public class SimulationClient {
	private HttpClient client = HttpClient.newHttpClient();
	private FactorySerialyzer serialyzer = new FactorySerialyzer();
	private String simulatioId;
	private Logger logger;

	public SimulationClient(String simulationId, Logger logger) {
		this.simulatioId = simulationId;
		this.logger = logger;
	}

	public Factory getFactory() {

		try {
			String url = String.format("http://localhost:8080/retrieve-simulation?simulationId=%s", simulatioId);
			URI uri = URI.create(url);
			
			HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			logger.info(response.toString());
			
			return serialyzer.createFactoryFrom(response.body());
		} 
		catch (IOException | InterruptedException e) {
			logger.warning(e.getMessage());
			
			return null;
		}
	}

	public void startSimulation() {
		try {
			String url = String.format("http://localhost:8080/start-simulation?simulationId=%s", simulatioId);
			URI uri = URI.create(url);

			HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			logger.info(response.toString());
			
		} catch (IOException | InterruptedException e) {
			logger.warning(e.getMessage());
		}
	}
	
	public void stopSimulation() {
		try {
			String url = String.format("http://localhost:8080/stop-simulation?simulationId=%s", simulatioId);
			URI uri = URI.create(url);

			HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			logger.info(response.toString());
			
		} catch (IOException | InterruptedException e) {
			logger.info(e.getMessage());
		}
	}

	public void setSimulationId(String id) {
		this.simulatioId = id;	
	}
}
