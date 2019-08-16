package tansponderclient;

public class ConnectionManager {
	private final String TAG = ConnectionManager.class.getSimpleName();
	private ClientConnecttion mClientConnecttion;
	
	private int fixed_server_port = 19910;
	private String fixed_host_address = "opendiylib.com";
	private int request_server_port = 19911;
	private int response_server_port = 19911;
	private String fixed_response_address = "127.0.0.1";
	
	public ConnectionManager(String fixedhost, int fixedport, int requestPort, int responsePort) {
		fixed_host_address = fixedhost;
		fixed_server_port = fixedport;
		request_server_port = requestPort;
		response_server_port = responsePort;
		mClientConnecttion = new ClientConnecttion(fixed_host_address, fixed_server_port,request_server_port, fixed_response_address, response_server_port);
	}
	
	public void initClientConnect() {
		mClientConnecttion.startRun();
	}
	
	public void releaseClientConnect() {
		mClientConnecttion.stopRun();
		mClientConnecttion = null;
	}
}
