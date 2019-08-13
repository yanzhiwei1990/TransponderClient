package tansponderclient;

public class Main {

    private static final int MAX_CONNECTION_NUM = 50;
    private static int fixed_server_port = 19910;
	private static String fixed_host_address = "opendiylib.com";
	private static int request_server_port = 19911;

    public static void main(String[] args) {
    	ConnectionManager connectionManager = new ConnectionManager(fixed_host_address, fixed_server_port, request_server_port);
    	connectionManager.initClientConnect();
    }

}