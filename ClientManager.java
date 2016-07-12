package connection;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import gan.alan.androidclient.InputListener;

public class ClientManager extends ConnectionManager {
    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private InputStream inputStream = null;
    private InputListener mInputListener = null;
    private String address;
    private UUID mUUID;

    public ClientManager(String address, UUID uuid) {
        this.address = address;
        this.mUUID = uuid;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Connect to server specified by MAC Address and UUID
     */
    public void connect() {
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try {
            btSocket = device.createRfcommSocketToServiceRecord(mUUID);
        } catch (IOException e) {
        }
        btAdapter.cancelDiscovery();

        try {
            btSocket.connect();
            outStream = btSocket.getOutputStream();
            inputStream = btSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                btSocket.close();
            } catch (IOException e2) {}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerListener(InputListener listener) throws IllegalStateException {
        if(mInputListener == null || !mInputListener.isAlive()) {
            mInputListener = listener;
            mInputListener.setInputStream(inputStream, key);
            mInputListener.start();

        } else
            throw new IllegalStateException("Input listener is already active");
    }

    public boolean isConnected() {
        return btSocket.isConnected();
    }

    public void unregisterListener() {
        try {
            if(mInputListener != null)
                mInputListener.close(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flushOutStream() throws IOException {
        if(outStream != null) {
            outStream.flush();
        }
    }

    public void closeConnection() throws IOException {
        outStream.close();
        mInputListener.close(key);
        btSocket.close();
    }

    public void checkBTState(Activity activity) {
        // Check for Bluetooth support and then check to make sure it is turned on

        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }
}
