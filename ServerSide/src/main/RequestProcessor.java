package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;
import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.robotsim.model.FactoryPersistenceManager;

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
			
			Object object = objectReader.readObject();
			
			if (object instanceof String) {
				String canvasId = (String) object;
				
				logger.info(String.format("READING FILE %s", canvasId));
				
				Canvas canvas = this.persistManager.read(canvasId);
				System.out.println(canvas);
				
				OutputStream out = socket.getOutputStream();
				BufferedOutputStream bufferOutStream = new BufferedOutputStream(out);
				ObjectOutputStream writer = new ObjectOutputStream(bufferOutStream);
				
				writer.writeObject(canvas);
				
				writer.close();
				out.close();
			}
			else {
				Canvas canvas = (Canvas) object;
				
				this.logger.info(String.format("WRITING FILE %s", canvas.getId()));
				
				this.persistManager.persist(canvas);
			}
			
			
			inputStream.close();
			bufferedInputStream.close();
			objectReader.close();
			
			this.logger.info("REQUEST CONCLUDE");
		}
		catch (Exception e) {
			this.logger.warning(e.getMessage());
		}
	}
}

