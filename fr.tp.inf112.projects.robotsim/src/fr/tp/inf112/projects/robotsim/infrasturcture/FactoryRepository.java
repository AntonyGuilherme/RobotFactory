package fr.tp.inf112.projects.robotsim.infrasturcture;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;

import fr.tp.inf112.projects.robotsim.model.Factory;

public class FactoryRepository {
	private Logger logger;

	public FactoryRepository(Logger logger) {
		this.logger = logger;
	}
	
	public Factory read(String canvasId) {
		try (Socket socket = new Socket()) {
			this.logger.info(String.format("READING THE FILE %s", canvasId));
			
			// Socket address to the File System Server
			InetAddress host = InetAddress.getLocalHost();
			InetSocketAddress adress = new InetSocketAddress(host, 80);
			
			socket.connect(adress, 1000);
			
			OutputStream socketOutputStream = socket.getOutputStream();
			BufferedOutputStream bufferOutStream = new BufferedOutputStream(socketOutputStream);
			ObjectOutputStream writter = new ObjectOutputStream(bufferOutStream);
			
			// Sending the file path/name
			writter.writeObject(canvasId);
			writter.flush();
			
			InputStream inputStream = socket.getInputStream();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			ObjectInputStream objectReader = new ObjectInputStream(bufferedInputStream);
			
			// reading the canvas/factory
			Object object = objectReader.readObject();
					
			writter.close();
			bufferOutStream.close();
			socketOutputStream.close();
			
			return (Factory) object;
			
		} catch (Exception e) {
			this.logger.info(String.format("ERROR READING THE FILE %s : %s", canvasId, e.getMessage()));
		}
		
		return null;
	}
	
	public void persist(Factory factory)  {
		try (Socket socket = new Socket()) {
			
			this.logger.info(String.format("WRITING THE FILE %s", factory.getId()));
			
			// Socket address to the File System Server
			InetAddress host = InetAddress.getLocalHost();
			InetSocketAddress adress = new InetSocketAddress(host, 80);
			
			// Connecting to the File System service
			socket.connect(adress, 1000);
			
			OutputStream socketOutputStream = socket.getOutputStream();
			BufferedOutputStream bufferOutStream = new BufferedOutputStream(socketOutputStream);
			ObjectOutputStream writter = new ObjectOutputStream(bufferOutStream);
			
			// saving the factory
			writter.writeObject(factory);

			writter.close();
			bufferOutStream.close();
			socketOutputStream.close();
			
		} catch (Exception e) {
			this.logger.info(String.format("ERROR WRITING THE FILE %s : %s", factory.getId(), e.getMessage()));
		}
	}
}