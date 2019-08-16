package tansponderclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import tansponderclient.Callback.ReadWriteConnectionCallback;
import tansponderclient.Callback.SocketConnectionCallback;

public class ClientConnecttion {
	private final String TAG = ClientConnecttion.class.getSimpleName();
	
	private int fixed_server_port = 19910;
	private String fixed_host_address = "opendiylib.com";
	private String fixed_client_address = "127.0.0.1";
	private int request_server_port = 19911;
	private int response_server_port = 19911;
	private ClientConnecttionThread mClientConnecttionThread;
	private Socket requestSocket = null;
	private Socket responseSocket = null;
	private ReadWriteConnection mReadWriteConnection = null;
	
	public ClientConnecttion (String fixedhost, int fixedport, int requestPort, String localhost, int responsePort) {
		fixed_host_address = fixedhost;
		fixed_server_port = fixedport;
		request_server_port = requestPort;
		fixed_client_address = localhost;
		response_server_port = responsePort;
		mClientConnecttionThread = new ClientConnecttionThread(fixed_host_address, fixed_server_port, mSocketConnectionCallback);
	}
	
	private SocketConnectionCallback mSocketConnectionCallback = new SocketConnectionCallback() {
		@Override
		public void onSocketConnectionCallbackChange(Socket socket, String flag, String status) {
			LogUtils.LOGD(TAG, "onSocketConnectionCallbackChange flag = " + flag + ", status = " + status);
			if ("erro".equals(flag) && "reconnect".equals(status)) {
				restart();
			}
		}
	};
	
	private ReadWriteConnectionCallback mReadWriteConnectionCallback = new ReadWriteConnectionCallback() {
		@Override
		public void onReadWriteConnectionCallbackChange(Socket request, Socket response, String flag, String status) {
			LogUtils.LOGD(TAG, "onReadWriteConnectionCallbackChange flag = " + flag + ", status = " + status);
			if (request != null && response != null) {
				if ("request".equals(flag)) {
					if ("exception".equals(status)) {
						restart();
					} else if ("exit".equals(status)) {
						restart();
					}
				} else if ("response".equals(flag)) {
					if ("exception".equals(status)) {
						
					} else if ("exit".equals(status)) {
						
					}
				}
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
	
	private boolean isPairCommand(JSONObject commandobj) {
		LogUtils.LOGD(TAG, "isPairCommand commandobj = " + (commandobj != null ? commandobj.toString() : "null"));
		boolean result = false;
		if (commandobj != null && commandobj.length() > 0) {
			try {
				String requesthost = commandobj.getString("request_host");
				int requestport= commandobj.getInt("request_port");
				String responsehost = commandobj.getString("response_host");
				int responseport = commandobj.getInt("response_port");
				String requestStatus = commandobj.getString("request_status");
				String password = commandobj.getString("request_password");
				if (fixed_host_address.equals(requesthost) &&
						fixed_client_address.equals(responsehost) &&
						request_server_port == requestport &&
						response_server_port == responseport &&
						"allowed".equals(requestStatus) &&
						"#qwertyuiop789456123zxcvbnm,.$".equals(password)) {
					result = true;
				}
			} catch (Exception e) {
				LogUtils.TRACE(e);
				LogUtils.LOGE(TAG, "isPairCommand Exception = " + e.getMessage());
			}
		}
		return result;
	}
	
	private void initPairConnection(JSONObject commandobj) {
		LogUtils.LOGD(TAG, "initPairConnection commandobj = " + (commandobj != null ? commandobj.toString() : "null"));
		if (commandobj != null && commandobj.length() > 0) {
			if (mReadWriteConnection != null) {
				mReadWriteConnection.stopRun();
			} else {
				startPairConnection(commandobj);
			}
		}
	}
	
	public void reStartPair() {
		if (mClientConnecttionThread != null && mClientConnecttionThread.mConnectSocket != null &&
				!mClientConnecttionThread.mConnectSocket.isClosed() &&
				mClientConnecttionThread.mConnectSocket.isConnected()) {
			if (pairTimer != null) {
				pairTimer.cancel();
				pairTimer = null;
			}
			if (pairtimerTask != null) {
				pairtimerTask.cancel();
				pairtimerTask = null;
			}
			pairtimerTask = getPairTimerTask();
			pairTimer = getTimer();
			pairTimer.schedule(pairtimerTask, 3000);
		}
	}
	
	private TimerTask pairtimerTask;
	private Timer pairTimer;
	
	private TimerTask getPairTimerTask() {
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if (mClientConnecttionThread != null && mClientConnecttionThread.mConnectSocket != null &&
						!mClientConnecttionThread.mConnectSocket.isClosed() &&
						mClientConnecttionThread.mConnectSocket.isConnected()) {
					//
				}
			}
		};
		return timerTask;
	}
	
	private void startPairConnection(JSONObject commandobj) {
		LogUtils.LOGD(TAG, "startPairConnection commandobj = " + (commandobj != null ? commandobj.toString() : "null"));
		String remote = null;
		int remoteport = -1;
		String local = null;
		int localport = -1;
		if (commandobj != null && commandobj.length() > 0) {
			try {
				remote = commandobj.getString("request_host");
				remoteport = commandobj.getInt("request_port");
				local = commandobj.getString("response_host");
				localport = commandobj.getInt("response_port");
			} catch (Exception e) {
				LogUtils.TRACE(e);
				LogUtils.LOGE(TAG, "startPairConnection parse command Exception = " + e.getMessage());
			}
		}
		if (remote != null && remote.length() > 0 && remoteport > 0 &&
				local != null && local.length() > 0 && localport > 0) {
			try {
				if (requestSocket != null) {
					requestSocket.close();
					requestSocket = null;
				}
			} catch (Exception e) {
				LogUtils.TRACE(e);
				LogUtils.LOGE(TAG, "startPairConnection requestSocket close Exception = " + e.getMessage());
			}
			try {
				if (responseSocket != null) {
					responseSocket.close();
					responseSocket = null;
				}
			} catch (Exception e) {
				LogUtils.TRACE(e);
				LogUtils.LOGE(TAG, "startPairConnection responseSocket close Exception = " + e.getMessage());
			}
			try {
				requestSocket = new Socket(remote, remoteport);
			} catch (Exception e) {
				LogUtils.TRACE(e);
				LogUtils.LOGE(TAG, "startPairConnection new requestSocket Exception = " + e.getMessage());
			}
			try {
				responseSocket = new Socket(local, localport);
			} catch (Exception e) {
				LogUtils.TRACE(e);
				LogUtils.LOGE(TAG, "startPairConnection new responseSocket Exception = " + e.getMessage());
			}
			if (requestSocket != null && responseSocket != null) {
				mReadWriteConnection = new ReadWriteConnection(requestSocket, responseSocket, mReadWriteConnectionCallback);
				mReadWriteConnection.startRun();
			} else {
				try {
					if (requestSocket != null) {
						requestSocket.close();
						requestSocket = null;
					}
				} catch (Exception e) {
					LogUtils.TRACE(e);
					LogUtils.LOGE(TAG, "startPairConnection at least one fail requestSocket close Exception = " + e.getMessage());
				}
				try {
					if (responseSocket != null) {
						responseSocket.close();
						responseSocket = null;
					}
				} catch (Exception e) {
					LogUtils.TRACE(e);
					LogUtils.LOGE(TAG, "startPairConnection at least one fail responseSocket close Exception = " + e.getMessage());
				}
				//reStartPair();
				restart();
			}
		}
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
            byte[] buffer = new byte[1024];
            JSONObject commandObj = null;
			try {
				mConnectSocket = new Socket(mHost, mPort);
				is = mConnectSocket.getInputStream();
				os = mConnectSocket.getOutputStream();
				LogUtils.LOGD(TAG, "remote server:" + mConnectSocket.getRemoteSocketAddress());
				JSONObject responseObj = new JSONObject();
				responseObj.put("request_host", fixed_host_address);
				responseObj.put("request_port", request_server_port);
				responseObj.put("response_host", fixed_client_address);
				responseObj.put("response_port", response_server_port);
				responseObj.put("request_status", "ready");
				responseObj.put("request_password", "#qwertyuiop789456123zxcvbnm,.$");
				os.write(responseObj.toString().getBytes());
				os.flush();
				while (isRunning/* && !mConnectSocket.isClosed()*/) {
					int size = is.read(buffer); 
                    if (size > -1) {
                    	commandObj = parseSocketCommand(buffer, size);
                    	if (isPairCommand(commandObj)) {
                    		initPairConnection(commandObj);
                    	}
                    	LogUtils.LOGD(TAG, "remote server response = " + (commandObj != null ? commandObj.toString() : "invalid"));
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
