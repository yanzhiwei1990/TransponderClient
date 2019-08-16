package tansponderclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import tansponderclient.Callback.ClientCallback;

public class TcpServerListen {
	public static final String TAG = TcpServerListen.class.getSimpleName();
	private String mHostAddress = "127.0.0.1";//"opendiylib.com";
	private int mHostPort = 19910;
	private int mConnectionNumber = 50;
	private ServerSocket mServerSocket = null;
	private List<ServerThread> mServerThreadList = new ArrayList<ServerThread>();
	private Object mServerThreadListLock = new Object();
	
	public TcpServerListen(String host, int port, int number) {
		this.mHostAddress = host;
		this.mHostPort = port;
		this.mConnectionNumber = number;
	}
	
	public void startTcpServerListen() {
		InetAddress address = null;
		InetAddress defaultAddress = null;
    	try {
    		defaultAddress = InetAddress.getByName("127.0.0.1");
    		if (mHostAddress != null) {
    			address = InetAddress.getByName(mHostAddress);
    		} else {
    			address = defaultAddress;
    		}
		} catch (Exception e) {
			address = defaultAddress;
			LogUtils.TRACE(e);
			LogUtils.LOGE(TAG, "startTcpServerListen use default address");
		}
    	if (address == null) {
    		LogUtils.LOGE(TAG, "startTcpServerListen invalid address");
    		return;
    	}
        try {
        	System.out.println("start to listen " + address + ":" + mHostPort);
            mServerSocket =new ServerSocket(mHostPort, mConnectionNumber, address);
            Socket client = null;
            ServerThread serverThread = null;
            while(true)
            {
            	client = mServerSocket.accept();
            	serverThread = new ServerThread(client, mClientCallback);
            	serverThread.start();
            	if (mClientCallback != null) {
            		mClientCallback.onClientCallbackChange(serverThread, "start", "start");
            	}
            }
        } catch (Exception e) {
        	LogUtils.TRACE(e);
            LogUtils.LOGE(TAG, "startTcpServerListen Exception = " + e.getMessage());
        }
	}
	
	private ClientCallback mClientCallback = new ClientCallback() {
		@Override
		public void onClientCallbackChange(ServerThread serverThread, String flag, String status) {
			dealServerThreadCallback(serverThread, flag, status);
		}
		
	};
	
	private void findInList(ServerThread serverThread, String flag, String status) {
		/*synchronized (mServerThreadListLock) {
			Iterator<ServerThread> iterator = mServerThreadList.iterator();
		}*/
		
	}
	
	private void dealServerThreadCallback(ServerThread serverThread, String flag, String status) {
		if (serverThread != null) {
			
		}
		if (serverThread != null && "over".equals(flag) && "exit".equals(status)) {
			try {
				if (serverThread.mClient != null) {
					serverThread.mClient.close();
				}
			} catch (Exception e) {
				LogUtils.TRACE(e);
				LogUtils.LOGE(TAG, "dealServerThreadCallback Exception = " + e.getMessage());
			}
			removeServerThread(serverThread);
		} else if (serverThread != null && "start".equals(flag) && "start".equals(status)) {
			addServerThread(serverThread);
		}
	}
	
	private void addServerThread(ServerThread thread) {
		synchronized (mServerThreadListLock) {
			if (thread != null) {
				try {
					mServerThreadList.add(thread);
				} catch (Exception e) {
					LogUtils.TRACE(e);
					LogUtils.LOGE(TAG, "addServerThread Exception = " + e.getMessage());
				}
			}
		}
	}
	
	private void removeServerThread(ServerThread thread) {
		synchronized (mServerThreadListLock) {
			if (thread != null) {
				try {
					mServerThreadList.remove(thread);
				} catch (Exception e) {
					LogUtils.TRACE(e);
					LogUtils.LOGE(TAG, "removeServerThread Exception = " + e.getMessage());
				}
			}
		}
	}
	
	public class ServerThread extends Thread {
	    private Socket mClient;
	    private ClientCallback mClientCallback;
	    
	    ServerThread(Socket sk, ClientCallback callback) {
	        this.mClient = sk;
	        this.mClientCallback = callback;
	    }

	    public void run() {
	        BufferedReader in = null;
	        PrintWriter out = null;
	        try {
	            in = new BufferedReader(new InputStreamReader(mClient.getInputStream()));
	            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mClient.getOutputStream())),true);
	            while(true)
	            {
	                String cmd=in.readLine();
	                System.out.println(cmd);
	                if (cmd==null) {
	                    break;
	                }
	                out.println(new Date().toString() + ":" + cmd);   //服务器返回时间和客户发送来的消息
	            }
	        } catch (Exception e) {
	        	LogUtils.TRACE(e);
				LogUtils.LOGE(TAG, "ServerThread run Exception = " + e.getMessage());
	        }
	        releaseSocket(mClient, in, out);
	        if (mClientCallback != null) {
	        	mClientCallback.onClientCallbackChange(ServerThread.this, "over", "exit");
	        }
	    }
	}
	
	private void releaseSocket(Socket socket, BufferedReader in, PrintWriter out) {
		try {
        	if (in != null) {
                in.close();
                in = null;
            }
		} catch (Exception e) {
			LogUtils.TRACE(e);
			LogUtils.LOGE(TAG, "releaseSocket in Exception = " + e.getMessage());
		}
		try {
        	if (out != null) {
        		out.close();
        		out = null;
            }
		} catch (Exception e) {
			LogUtils.TRACE(e);
			LogUtils.LOGE(TAG, "releaseSocket out Exception = " + e.getMessage());
		}
		/*try {
        	if (socket != null) {
        		socket.close();
        		socket = null;
            }
		} catch (Exception e) {
			LogUtils.TRACE(e);
			LogUtils.LOGE(TAG, "releaseSocket socket Exception = " + e.getMessage());
		}*/
	}
}
