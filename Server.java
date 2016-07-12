package connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
  
public class Server {
    
	private StreamConnection connection = null;
	private StreamConnectionNotifier streamConnNotifier = null;
	private OutputStream outStream = null;
	private InputStream inStream = null;
	
    //start server
    public void startServer(ServerManager.Key key) throws IOException{
    	key.hashCode();
    	
        //Create a UUID for SPP
        UUID uuid = new UUID("1101", true);
        //Create the service url
        String connectionString = "btspp://localhost:" + uuid +";name=Sample SPP Server";
        
        //open server url
        streamConnNotifier = (StreamConnectionNotifier)Connector.open( connectionString );
        
        //Wait for client connection
        System.out.println("\nServer Started. Waiting for clients to connect...");
        connection=streamConnNotifier.acceptAndOpen();
 
        RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
        System.out.println("Remote device address: "+dev.getBluetoothAddress());
        System.out.println("Remote device name: "+dev.getFriendlyName(true));
        
        inStream = connection.openInputStream();
        outStream = connection.openOutputStream();
    }
    
    public InputStream getInputStream(ServerManager.Key key) {
    	key.hashCode();
    	return inStream;
    }
    
    public void writeToStream(Serializable data, ServerManager.Key key) throws IOException {
    	key.hashCode();
    	if(connection != null) {
    		if(outStream == null)
    			outStream = connection.openOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outStream);
			oos.writeObject(data);
    	}
    }
    
    public void closeConnection(ServerManager.Key key) throws IOException {
    	streamConnNotifier.close();
    	outStream.close();
    }
    
}