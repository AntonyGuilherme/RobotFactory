package fr.tp.inf112.projects.robotsim.app;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.tp.inf112.projects.canvas.controller.CanvasViewerController;
import fr.tp.inf112.projects.canvas.controller.Observer;
import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.CanvasPersistenceManager;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.RemoteFactoryPersistenceManager;

public class RemoteSimulationController implements CanvasViewerController {

	private Factory factoryModel;

	private final CanvasPersistenceManager persistenceManager;

	private final SimulationClient client;
	
	private AtomicBoolean simulationStarted = new AtomicBoolean(false);

	public RemoteSimulationController(Factory remoteFactory, 
			SimulationClient client,
			RemoteFactoryPersistenceManager remoteFactoryPersistenceManager) {
				this.persistenceManager = remoteFactoryPersistenceManager;
				this.client = client;
				this.setCanvas(remoteFactory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addObserver(final Observer observer) {
		if (factoryModel != null) {
			return factoryModel.addObserver(observer);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeObserver(final Observer observer) {
		if (factoryModel != null) {
			return factoryModel.removeObserver(observer);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCanvas(final Canvas canvasModel) {
		
		if (factoryModel == null) { 
			this.factoryModel = (Factory) canvasModel;
			return;
		}
		
		// Considering the case when the user change the factory that is being simulated
		if (factoryModel.getId()  != null && canvasModel.getId() != null &&
			!factoryModel.getId().equalsIgnoreCase(canvasModel.getId())) {
			
			System.out.println(String.format("%s %s", factoryModel.getId(), canvasModel.getId()));
			this.simulationStarted.set(false);
			client.stopSimulation();
			client.setSimulationId(canvasModel.getId());
		}
		
		List<Observer> observers = null;
		
		observers = factoryModel.getObservers();

		factoryModel = (Factory) canvasModel;
	
		for (final Observer observer : observers) {
			factoryModel.addObserver(observer);
		}
		
		factoryModel.notifyObservers();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Canvas getCanvas() {
		return factoryModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startAnimation() {

		setCanvas(client.getFactory());
		
		client.startSimulation();
		
		this.simulationStarted.set(true);
		
		new Thread(() -> this.updateFactory()).start();
	}
	
	private void updateFactory() {
		while(this.simulationStarted.get()) {
			setCanvas(client.getFactory());
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopAnimation() {
		client.stopSimulation();
		
		setCanvas(client.getFactory());
		
		this.simulationStarted.set(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAnimationRunning() {
		return this.simulationStarted.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CanvasPersistenceManager getPersistenceManager() {
		return persistenceManager;
	}
}
