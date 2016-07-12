package connection;

import java.io.Serializable;
import java.util.HashMap;

public class CollectionRequest implements Serializable {
	private static final long serialVersionUID = 4887284704831950688L;
	
	public enum RequestType {
		IMU_START, IMU_END, CONNECTION_STATE, OTHER
	}
	private RequestType type;
	
	HashMap<String, Serializable> parameters = new HashMap<String, Serializable>();
	
	public CollectionRequest(RequestType type) {
		this.type = type;
	}
	
	public void addParameter(String key, Serializable param) {
		parameters.put(key, param);
	}
	
	public HashMap<String, Serializable> getParameters() {
		return parameters;
	}
	
	public Serializable getParameter(String key) {
		return parameters.get(key);
	}
}
