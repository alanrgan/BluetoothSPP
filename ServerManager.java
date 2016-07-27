package connection;

import java.io.IOException;
import java.io.InputStream;

public class ServerManager extends ConnectionManager {
	private Server server;
	private InputListener mInputListener = null;
	private InputStream inputStream = null;
	private Thread serverThread;
	private Thread listenerThread;
	private OnServerStartedHandler startHandler = null;
	
	public ServerManager(Server server) {
		this.server = server;
	}
	
	public static abstract class OnServerStartedHandler {
		public abstract void handle();
	}
	
	public void startServer() {
		serverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					server.startServer(key);
					inputStream = server.getInputStream(key);
					outStream = server.getOutputStream(key);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if(startHandler != null) {
						startHandler.handle();
					}
				}
			}
		});
		
		serverThread.start();
	}
	
	public void closeConnection() throws IOException {
		if(mInputListener != null) {
			server.closeConnection(key);
			mInputListener.close(key);
		}
	}

	@Override
	public void registerListener(InputListener listener) throws IllegalStateException {
		if(mInputListener == null || !mInputListener.isAlive()) {
			listenerThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						serverThread.join();
						mInputListener = listener;
						mInputListener.setInputStream(inputStream, key);
						mInputListener.start();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			
			listenerThread.start();
		} else {
			throw new IllegalStateException("Input listener already active");
		}
	}

	@Override
	public void unregisterListener() throws IOException {
		try {
			if(mInputListener != null)
				mInputListener.close(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setOnServerStartedHandler(OnServerStartedHandler handler) {
		startHandler = handler;
	}
}
