package tansponderclient;

public class ConnectionManager {
	private final String TAG = ConnectionManager.class.getSimpleName();
	private ClientConnecttion mClientConnecttion;
	
	private int fixed_server_port = 19910;
	private String fixed_host_address = "opendiylib.com";
	private int request_server_port = 19911;
	
	public ConnectionManager(String fixedhost, int fixedport, int requestPort) {
		fixed_host_address = fixedhost;
		fixed_server_port = fixedport;
		request_server_port = requestPort;
		mClientConnecttion = new ClientConnecttion(fixed_host_address, fixed_server_port, request_server_port);
	}
	
	public void initClientConnect() {
		mClientConnecttion.startRun();
	}
	
	public void releaseClientConnect() {
		mClientConnecttion.stopRun();
		mClientConnecttion = null;
	}
}
