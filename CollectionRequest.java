package connection;

import java.io.Serializable;
import java.util.HashMap;

public class CollectionRequest implements Serializable {
	private static final long serialVersionUID = 4887284704831950688L;
	
	public enum RequestType {
		STOP, CLOSE, CONNECTION_STATE, DATA, OTHER
	}
	private RequestType type;
	
	HashMap<String, Serializable> parameters = new HashMap<String, Serializable>();
	
	public CollectionRequest() {
		this.type = RequestType.OTHER;
	}
	
	public CollectionRequest(RequestType type) {
		this.type = type;
	}
	
	public RequestType getType() {
		return type;
	}
	
	public CollectionRequest addParameter(String key, Serializable param) {
		parameters.put(key, param);
		return this;
	}
	
	public HashMap<String, Serializable> getParameters() {
		return parameters;
	}
	
	public Serializable getParameter(String key) {
		return parameters.get(key);
	}
}
