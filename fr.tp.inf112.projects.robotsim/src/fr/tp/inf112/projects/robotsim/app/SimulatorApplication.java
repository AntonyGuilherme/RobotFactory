package fr.tp.inf112.projects.robotsim.app;

import java.awt.Component;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import fr.tp.inf112.projects.canvas.view.CanvasViewer;
import fr.tp.inf112.projects.canvas.view.FileCanvasChooser;
import fr.tp.inf112.projects.robotsim.infrasturcture.RemoteFactoryPersistenceManager;
import fr.tp.inf112.projects.robotsim.infrasturcture.SimulationClient;
import fr.tp.inf112.projects.robotsim.model.Factory;

public class SimulatorApplication {
	
	private static final Logger LOGGER = Logger.getLogger(SimulatorApplication.class.getName());
	private static String SIMULATION_ID = "cruzeiro.factory";
			
	public static void main(String[] args) {
		LOGGER.info("Starting the robot simulator...");
		
		LOGGER.info("With parameters " + Arrays.toString(args) + ".");
		
		SimulationClient client = new SimulationClient(SIMULATION_ID, LOGGER);
		Factory remoteFactory = client.getFactory();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
	        public void run() {
				final FileCanvasChooser canvasChooser = new FileCanvasChooser("factory", "Puck Factory");
				final RemoteFactoryPersistenceManager persistence = new RemoteFactoryPersistenceManager(canvasChooser, LOGGER);
				final RemoteSimulationController controller = new RemoteSimulationController(remoteFactory, client, persistence, LOGGER);
				final Component factoryViewer = new CanvasViewer(controller);
				
				canvasChooser.setViewer(factoryViewer);
			}
		});
	}
}
