package fr.tp.inf112.projects.robotsim.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.CanvasChooser;
import fr.tp.inf112.projects.canvas.model.impl.AbstractCanvasPersistenceManager;

public class RemoteFactoryPersistenceManager extends AbstractCanvasPersistenceManager {

	public RemoteFactoryPersistenceManager(CanvasChooser canvasChooser) {
		super(canvasChooser);
	}

	@Override
	public Canvas read(String path) {
		String canvasId = getCanvasIdFromPath(path);
		
		try ( Socket socket = new Socket()) {
			
			InetAddress host = InetAddress.getLocalHost();
			InetSocketAddress adress = new InetSocketAddress(host, 80);
			
			socket.connect(adress, 1000);
			
			OutputStream socketOutputStream = socket.getOutputStream();
			BufferedOutputStream bufferOutStream = new BufferedOutputStream(socketOutputStream);
			ObjectOutputStream writter = new ObjectOutputStream(bufferOutStream);

			writter.writeObject(canvasId);
			
			writter.flush();
			
			InputStream inputStream = socket.getInputStream();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			ObjectInputStream objectReader = new ObjectInputStream(bufferedInputStream);
			
			Object object = objectReader.readObject();
			
			System.out.println(object);
					
			writter.close();
			bufferOutStream.close();
			socketOutputStream.close();
			
			return (Canvas) object;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String getCanvasIdFromPath(String path) {
		int nameStartOf = path.lastIndexOf('\\') + 1;
		
		return path.substring(nameStartOf);
	}

	@Override
	public void persist(Canvas canvasModel)  {
		
		try (Socket socket = new Socket()) {
			
			InetAddress host = InetAddress.getLocalHost();
			InetSocketAddress adress = new InetSocketAddress(host, 80);
			
			socket.connect(adress, 1000);
			
			
			OutputStream socketOutputStream = socket.getOutputStream();
			BufferedOutputStream bufferOutStream = new BufferedOutputStream(socketOutputStream);
			ObjectOutputStream writter = new ObjectOutputStream(bufferOutStream);
			
			writter.writeObject(canvasModel);
			

			writter.close();
			bufferOutStream.close();
			socketOutputStream.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	// never will be called (implement as a plus)
	@Override
	public boolean delete(Canvas canvasModel) {
		return false;
	}
}
