package connection;

import java.io.IOException;
import java.io.InputStream;

public class ServerManager extends ConnectionManager {
	private Server server;
	private InputListener mInputListener = null;
	private InputStream inputStream = null;
	private Thread serverThread;
	private Thread listenerThread;
	
	public ServerManager(Server server) {
		this.server = server;
	}
	
	public void startServer() {
		serverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					server.startServer(key);
					inputStream = server.getInputStream(key);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		serverThread.start();
	}
	
	public void sendRequest(String text) {
		try {
			server.writeToStream(text, key);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean sendRequest(CollectionRequest request) {
		try {
			server.writeToStream(request, key);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void closeConnection() {
		try {
			server.closeConnection(key);
			mInputListener.close(key);
		} catch (IOException e) {
			e.printStackTrace();
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
}
