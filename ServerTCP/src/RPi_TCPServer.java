package rpi.nicomda.tcpserver;
import java.io.*;
import java.net.*;
import com.pi4j.*;

public class RPi_TCPServer {

	public static void main(String[] args) throws Exception 
	{
		String in_sentence;
		String in_password;
		ServerSocket socket=new ServerSocket(Integer.parseInt(args[0]));
		
		while(true){
			Socket conSocket= socket.accept();
			BufferedReader inFromClient=new BufferedReader(new InputStreamReader(conSocket.getInputStream()));
			DataOutputStream outToClient=new DataOutputStream(conSocket.getOutputStream());
			in_password=inFromClient.readLine();
			if(in_password==args[1]){
				System.out.println("IP: " + conSocket.getRemoteSocketAddress() + " autenticada.");
				in_sentence=inFromClient.readLine();	
				System.out.println("Recibido: " + in_sentence);
				outToClient.writeBytes("Conexión correcta.\n");
			}
			else{
				System.out.println("IP: " + conSocket.getRemoteSocketAddress() + " Fallo en autenticación.");
				outToClient.writeBytes("Fallo en autenticación.\n");
			} 
		}
	}

}
