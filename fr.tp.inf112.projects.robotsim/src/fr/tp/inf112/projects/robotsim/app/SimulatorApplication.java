package fr.tp.inf112.projects.robotsim.app;

import java.awt.Component;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import fr.tp.inf112.projects.canvas.view.CanvasViewer;
import fr.tp.inf112.projects.canvas.view.FileCanvasChooser;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.RemoteFactoryPersistenceManager;

public class SimulatorApplication {
	
	private static final Logger LOGGER = Logger.getLogger(SimulatorApplication.class.getName());
	private static String SIMULATION_ID = "vasco.factory";
			
	public static void main(String[] args) {
		LOGGER.info("Starting the robot simulator...");
		LOGGER.config("With parameters " + Arrays.toString(args) + ".");
		
		SimulationClient client = new SimulationClient(SIMULATION_ID);
		Factory remoteFactory = client.getFactory();
		
		//Factory remoteFactory = serialyzer.createFactoryFrom(json);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
	        public void run() {
				final FileCanvasChooser canvasChooser = new FileCanvasChooser("factory", "Puck Factory");
				RemoteSimulationController rcontroller = new RemoteSimulationController(remoteFactory, client, new RemoteFactoryPersistenceManager(canvasChooser));
				//SimulatorController controller = new SimulatorController(factory, new RemoteFactoryPersistenceManager(canvasChooser));
				final Component factoryViewer = new CanvasViewer(rcontroller);
				
				canvasChooser.setViewer(factoryViewer);
			}
		});
	}
}
