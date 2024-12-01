package fr.tp.inf112.projects.robotsim.model;

import java.util.logging.Logger;

import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.CanvasChooser;
import fr.tp.inf112.projects.canvas.model.impl.AbstractCanvasPersistenceManager;
import fr.tp.inf112.projects.robotsim.infrasturcture.FactoryRepository;

public class RemoteFactoryPersistenceManager extends AbstractCanvasPersistenceManager {

	private FactoryRepository repository;

	public RemoteFactoryPersistenceManager(CanvasChooser canvasChooser) {
		super(canvasChooser);
		this.repository = new FactoryRepository(Logger.getLogger("Repository"));
	}
	
	public RemoteFactoryPersistenceManager() {
		super(null);
	}

	@Override
	public Canvas read(String path) {
		String canvasId = getCanvasIdFromPath(path);
		
		return this.repository.read(canvasId);
	}
	
	private String getCanvasIdFromPath(String path) {
		int nameStartOf = path.lastIndexOf('\\') + 1;
		
		return path.substring(nameStartOf);
	}

	@Override
	public void persist(Canvas canvasModel)  {
		this.repository.persist((Factory) canvasModel);
	}

	@Override
	public boolean delete(Canvas canvasModel) {
		return false;
	}
}
