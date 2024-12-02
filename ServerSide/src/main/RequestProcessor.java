package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;
import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.robotsim.infrasturcture.FactoryPersistenceManager;
import fr.tp.inf112.projects.robotsim.infrasturcture.FactorySerialyzer;

public class RequestProcessor implements Runnable {
	final private Socket socket;
	final private FactoryPersistenceManager persistManager;
	final private Logger logger;
	
	public RequestProcessor(Socket socket, FactoryPersistenceManager persistManager, Logger logger) {
		this.socket = socket;
		this.persistManager = persistManager;
		this.logger = logger;
	}
	
	@Override
	public void run() {
		
		try (socket){
			InputStream inputStream = socket.getInputStream();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			ObjectInputStream objectReader = new ObjectInputStream(bufferedInputStream);
			
			// It could be the factory or the factory Id (file name/path)
			Object object = objectReader.readObject();
			
			if (object instanceof String) {
				readCanvas(object);
			}
			else {
				writeCanvas(object);
			}
			
			// closing the socket
			inputStream.close();
			bufferedInputStream.close();
			objectReader.close();
			
			this.logger.info("REQUEST CONCLUDE");
		}
		catch (Exception e) {
			e.printStackTrace();
			this.logger.warning(e.getMessage());
		}
	}

	private void readCanvas(Object object) throws IOException {
		// reading the file name/path
		String canvasId = (String) object;
		
		logger.info(String.format("READING FILE %s", canvasId));
		
		// reading the file from the file system
		Canvas canvas = this.persistManager.read(canvasId);
		
		// if the file not exists a default factory will be create
		if (canvas == null) {
			// creating the default factory (same of the first instructions)
			canvas = FactorySerialyzer.createDefaultFactory();
			
			// adding the factory Id (the id is the file name/path)
			canvas.setId(canvasId);
			
			// adding the canvas to the file system
			persistManager.persist(canvas);
		}
		
		OutputStream out = socket.getOutputStream();
		BufferedOutputStream bufferOutStream = new BufferedOutputStream(out);
		ObjectOutputStream writer = new ObjectOutputStream(bufferOutStream);
		
		writer.writeObject(canvas);
		
		writer.close();
		out.close();
	}

	private void writeCanvas(Object object) throws IOException {
		Canvas canvas = (Canvas) object;
		
		this.logger.info(String.format("WRITING FILE %s", canvas.getId()));
		
		//save the canvas
		this.persistManager.persist(canvas);
	}
}

