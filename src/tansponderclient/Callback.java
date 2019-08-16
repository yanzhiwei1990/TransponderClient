package tansponderclient;

import java.net.Socket;

import org.json.JSONObject;

import tansponderclient.TcpServerListen.ServerThread;

public class Callback {
	
	public interface ReadWriteConnectionStatusCallback {
    	void onReadWriteConnectionStatusChange(Socket request, Socket response, String flag, String status);
    }
	
	public interface ReadWriteConnectionCallback {
		void onReadWriteConnectionCallbackChange(Socket request, Socket response, String flag, String status);
	}
	
	public interface SocketConnectionCallback {
		void onSocketConnectionCallbackChange(Socket socket, String flag, String status);
	}
	
	public interface ClientCallback {
		void onClientCallbackChange(ServerThread serverThread, String flag, String status);
	}
	
	public interface PairCallback {
		void onPairCallbackChange(JSONObject obj);
	}
}
