package fr.tp.inf112.projects.robotsim.infrasturcture;

import java.util.logging.Logger;

import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.impl.AbstractCanvasPersistenceManager;
import fr.tp.inf112.projects.canvas.view.FileCanvasChooser;
import fr.tp.inf112.projects.robotsim.model.Factory;

public class RemoteFactoryPersistenceManager extends AbstractCanvasPersistenceManager {

	private FactoryRepository repository;
	
	public RemoteFactoryPersistenceManager(FileCanvasChooser canvasChooser, Logger logger) {
		super(canvasChooser);
		this.repository = new FactoryRepository(logger);
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
