package fr.tp.inf112.projects.robotsim.app;

import fr.tp.inf112.projects.canvas.controller.CanvasViewerController;
import fr.tp.inf112.projects.canvas.controller.Observer;
import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.CanvasPersistenceManager;
import fr.tp.inf112.projects.robotsim.model.Factory;

public class SimulatorController implements CanvasViewerController {
	
	private Factory factoryModel;
	
	private final CanvasPersistenceManager persistenceManager;
	
	public SimulatorController(final CanvasPersistenceManager persistenceManager) {
		this(null, persistenceManager);
	}
	
	public SimulatorController(final Factory factoryModel,
							   final CanvasPersistenceManager persistenceManager) {
		this.factoryModel = factoryModel;
		this.persistenceManager = persistenceManager;
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
		factoryModel = (Factory) canvasModel;
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
		factoryModel.startSimulation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopAnimation() {
		factoryModel.stopSimulation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAnimationRunning() {
		return factoryModel != null && factoryModel.isSimulationStarted();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CanvasPersistenceManager getPersistenceManager() {
		return persistenceManager;
	}
}
