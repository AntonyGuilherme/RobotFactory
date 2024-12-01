package fr.tp.inf112.projects.robotsim.app;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.FactorySerialyzer;

public class SimulationClient {
	private HttpClient client = HttpClient.newHttpClient();
	private FactorySerialyzer serialyzer = new FactorySerialyzer();
	private String simulatioId;

	public SimulationClient(String simulationId) {
		this.simulatioId = simulationId;
	}

	public Factory getFactory() {

		try {
			String url = String.format("http://localhost:8080/retrieve-simulation?simulationId=%s", simulatioId);
			URI uri = URI.create(url);

			HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			System.out.println(response.body());
			
			return serialyzer.createFactoryFrom(response.body());
		} 
		catch (IOException | InterruptedException e) {
			System.out.println(e);
			
			return null;
		}
	}

	public void startSimulation() {
		try {
			String url = String.format("http://localhost:8080/start-simulation?simulationId=%s", simulatioId);
			URI uri = URI.create(url);

			HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			System.out.println(response);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void stopSimulation() {
		try {
			String url = String.format("http://localhost:8080/stop-simulation?simulationId=%s", simulatioId);
			URI uri = URI.create(url);

			HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
			client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setSimulationId(String id) {
		this.simulatioId = id;	
	}
}
