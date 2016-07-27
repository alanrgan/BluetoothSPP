package connection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public abstract class ConnectionManager {
	public final class Key { protected Key() {} }
	public final Key key = new Key();
	private final Object LOCK = new Object();
	
	protected OutputStream outStream = null;
	
	public abstract void closeConnection() throws IOException;
	public abstract void registerListener(InputListener listener) throws IllegalStateException;
	public abstract void unregisterListener() throws IOException;
	
	public boolean sendRequest(CollectionRequest request) throws IOException {
		if(outStream != null) {
			synchronized(LOCK) {
				synchronized(outStream) {
					ObjectOutputStream oos = new ObjectOutputStream(outStream);
					oos.writeObject(request);
					return true;
				}
			}
		}
		return false;
	}
}
