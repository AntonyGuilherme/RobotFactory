package fr.tp.inf112.projects.robotsim.app;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import fr.tp.inf112.projects.canvas.controller.CanvasViewerController;
import fr.tp.inf112.projects.canvas.controller.Observer;
import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.CanvasPersistenceManager;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.RemoteFactoryPersistenceManager;

public class RemoteSimulationController extends SimulatorController {

	private final SimulationClient client;
	
	// the simulation will be updated in a background thread
	private AtomicBoolean simulationStarted = new AtomicBoolean(false);

	private Logger logger;

	public RemoteSimulationController(Factory remoteFactory, SimulationClient client,
			RemoteFactoryPersistenceManager remoteFactoryPersistenceManager, Logger logger) {
		super(remoteFactory, remoteFactoryPersistenceManager);
		this.client = client;
		this.logger = logger;
	}


	@Override
	public void setCanvas(final Canvas canvasModel) {
		
		// if this is the first factory to be simulated
		// the observers will be added in this factory
		if (factoryModel == null) {
			this.factoryModel = (Factory) canvasModel;
			return;
		}

		// Considering the case when the user change the factory that is being simulated
		if (factoryModel.getId() != null && canvasModel.getId() != null
				&& !factoryModel.getId().equalsIgnoreCase(canvasModel.getId())) {

			logger.info(String.format("Changing the simulation of %s to %s", factoryModel.getId(), canvasModel.getId()));
			
			// stopping the current simulation
			this.simulationStarted.set(false);
			client.stopSimulation();
			
			// updating the current simulation
			client.setSimulationId(canvasModel.getId());
		}
		
		// adding the observers to the updated factory
		List<Observer> observers = null;

		observers = factoryModel.getObservers();

		factoryModel = (Factory) canvasModel;

		for (final Observer observer : observers) {
			factoryModel.addObserver(observer);
		}
		
		// notifying the observers to update the simulation view
		factoryModel.notifyObservers();
	}

	@Override
	public void startAnimation() {
		logger.info(String.format("Starting the simulation %s", this.factoryModel.getId()));
		
		// updating the factory
		setCanvas(client.getFactory());
		
		// starting the simulation
		client.startSimulation();
		this.simulationStarted.set(true);
		
		// updating the view in the background
		new Thread(() -> this.updateFactory()).start();
	}

	private void updateFactory() {
		// updating the factoring while the simulation still running
		while (this.simulationStarted.get()) {
			setCanvas(client.getFactory());

			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stopAnimation() {
		//stopping the simulation in the server and the current location
		client.stopSimulation();
		setCanvas(client.getFactory());
		this.simulationStarted.set(false);
	}

	@Override
	public boolean isAnimationRunning() {
		return this.simulationStarted.get();
	}
}
