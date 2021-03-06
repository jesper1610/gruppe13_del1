package seq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class WeightSocket {
	
	private Socket scaleSocket;
	private PrintWriter out;
	private BufferedReader in;
	
	public void connect(String ip, int port) throws IOException
	{
		scaleSocket = new Socket(ip, port);
    	out = new PrintWriter(scaleSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(scaleSocket.getInputStream()));
	}

	public String recieveFromServer() throws IOException
	{
		return in.readLine();
	}
	
	public void sendToServer(String command)
	{
		out.println(command);
	}
	public Socket getScaleSocket() {
		return scaleSocket;
	}
}
