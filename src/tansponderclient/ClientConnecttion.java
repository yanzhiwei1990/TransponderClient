package tansponderclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import tansponderclient.Callback.SocketConnectionCallback;

public class ClientConnecttion {
	private final String TAG = ClientConnecttion.class.getSimpleName();
	
	private int fixed_server_port = 19910;
	private String fixed_host_address = "opendiylib.com";
	private int request_server_port = 19911;
	private ClientConnecttionThread mClientConnecttionThread;
	
	public ClientConnecttion (String fixedhost, int fixedport, int requestPort) {
		fixed_host_address = fixedhost;
		fixed_server_port = fixedport;
		request_server_port = requestPort;
		mClientConnecttionThread = new ClientConnecttionThread(fixed_host_address, fixed_server_port, mSocketConnectionCallback);
	}
	
	private SocketConnectionCallback mSocketConnectionCallback = new SocketConnectionCallback() {
		@Override
		public void onSocketConnectionCallbackChange(Socket socket, String flag, String status) {
			if ("erro".equals(flag) && "reconnect".equals(status)) {
				restart();
			}
		}
	};
	
	public void startRun() {
		if (mClientConnecttionThread != null) {
			mClientConnecttionThread.startRun();
			mClientConnecttionThread.start();
		}
	}
	
	public void stopRun() {
		if (mClientConnecttionThread != null) {
			mClientConnecttionThread.stopRun();
		}
	}
	
	private TimerTask timerTask;
	private Timer timer;
	
	public void restart() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
		timerTask = getTimerTask();
		timer = getTimer();
		timer.schedule(timerTask, 3000);
	}
	
	private TimerTask getTimerTask() {
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mClientConnecttionThread = null;
				mClientConnecttionThread = new ClientConnecttionThread(fixed_host_address, fixed_server_port, mSocketConnectionCallback);
				mClientConnecttionThread.startRun();
				mClientConnecttionThread.start();
			}
		};
		return timerTask;
	}
	
	private Timer getTimer() {
		Timer timer = new Timer();
		return timer;
	}
	
	public JSONObject parseSocketCommand(byte[] buffer, int length) {
		JSONObject result = null;
		String commandStr = null;
		try {
			commandStr = new String(buffer, 0, length);
			LogUtils.LOGD(TAG, "#parseSocketCommand commandStr = " + commandStr + "$");
			if (commandStr != null && commandStr.length() > 0) {
				result = new JSONObject(commandStr);
			}
		} catch (Exception e) {
			LogUtils.TRACE(e);
			LogUtils.LOGD(TAG, "parseSocketCommand Exception = " + e.getMessage());
		}
		return result;
	}
	
	private class ClientConnecttionThread extends Thread {
		private Socket mConnectSocket;
		private boolean isRunning = true;
		private String mHost;
		private int mPort;
		private SocketConnectionCallback socketConnectionCallback;
		
		public ClientConnecttionThread (String host, int port, SocketConnectionCallback callback) {
			this.mHost = host;
			this.mPort = port;
			this.socketConnectionCallback = callback;
		}
		
		public void setRun(boolean run) {
			isRunning = run;
		}
		
		public void startRun() {
			isRunning = true;
		}
		
		public void stopRun() {
			isRunning = false;
		}
		
		@Override
		public void run() {
			startConnect();
		}
		
		private void startConnect() {
			InputStream is = null;
            OutputStream os = null;
            byte[] buffer = new byte[1024*10];
            JSONObject commandObj = null;
			try {
				mConnectSocket = new Socket(mHost, mPort);
				is = mConnectSocket.getInputStream();
				os = mConnectSocket.getOutputStream();
				LogUtils.LOGD(TAG, "remote server:" + mConnectSocket.getRemoteSocketAddress());
				JSONObject responseObj = new JSONObject();
				responseObj.put("request_port", request_server_port);
				responseObj.put("response_status", "ready");
				responseObj.put("response_password", "#qwertyuiop789456123zxcvbnm,.$");
				os.write(responseObj.toString().getBytes());
				while (isRunning/* && !mConnectSocket.isClosed()*/) {
					int size = is.read(buffer); 
                    if (size > -1) {
                    	commandObj = parseSocketCommand(buffer, size);
                    	LogUtils.LOGD(TAG, "remote server response = " + commandObj != null ? commandObj.toString() : "invalid");
                    } else {
                    	break;
                    }
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LogUtils.TRACE(e);
				LogUtils.LOGE(TAG, "startConnect Exception = " + e.getMessage());
			}
			boolean stopFlag = isRunning;
			releaseSocket(mConnectSocket, is, os);
			if (stopFlag && socketConnectionCallback != null) {
				socketConnectionCallback.onSocketConnectionCallbackChange(mConnectSocket, "erro", "reconnect");
			}
		}
		
		private Socket reConnectSocket(String host, int port) {
			Socket result = null;
			try {
				result = new Socket(host, port);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				LogUtils.TRACE(e);
				LogUtils.LOGE(TAG, "reConnectSocket UnknownHostException = " + e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LogUtils.TRACE(e);
				LogUtils.LOGE(TAG, "reConnectSocket IOException = " + e.getMessage());
			}
			return result;
		}
		
		private void releaseSocket(Socket socket, InputStream is, OutputStream os) {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				LogUtils.TRACE(e);
				LogUtils.LOGE(TAG, "releaseSocket is.close() Exception = " + e.getMessage());
			}
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} catch (Exception e) {
				LogUtils.TRACE(e);
				LogUtils.LOGE(TAG, "releaseSocket os.close() Exception = " + e.getMessage());
			}
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (Exception e) {
				LogUtils.TRACE(e);
				LogUtils.LOGE(TAG, "releaseSocket socket.close() Exception = " + e.getMessage());
			}
		}
	}
}
