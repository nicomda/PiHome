package rpi.nicomda.tcpserver;
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class RPi_TCPServer {

	public static void main(String[] args) throws Exception 
	{
		String in_sentence;
		String in_password;
		String in_lightenable;
		final AtomicBoolean light_enable=new AtomicBoolean(false);
		ServerSocket socket=new ServerSocket(Integer.parseInt(args[0]));
		
		while(true){
			try {
				Socket conSocket= socket.accept();
				BufferedReader inFromClient=new BufferedReader(new InputStreamReader(conSocket.getInputStream()));
				DataOutputStream outToClient=new DataOutputStream(conSocket.getOutputStream());
				in_password=inFromClient.readLine();
				if(in_password==args[1]){
					System.out.println("IP: " + conSocket.getRemoteSocketAddress() + " autenticada.");
					in_sentence=inFromClient.readLine();
					in_lightenable=inFromClient.readLine();
					System.out.println("Recibido: " + in_sentence);
					outToClient.writeBytes("Conexión correcta.\n");
					if (in_lightenable=="true") {
					light_enable.set(true);
						
					}
					//Activating GPIO PINS
					Thread t=new Thread(new Runnable() {
						public void run() {
							ActivarGPIO(1000,120000,light_enable);
							}
						}
					);
					t.start();
				}
				else{
					System.out.println("IP: " + conSocket.getRemoteSocketAddress() + " Fallo en autenticación.");
					outToClient.writeBytes("Fallo en autenticación.\n");
				}

			} catch (Exception e) {
				System.out.println("Error de conexión");
			}
		}
	}
	
	private static void ActivarGPIO(int garaje_time, int light_time, AtomicBoolean l_enable ) {
		GpioController gpio=GpioFactory.getInstance();
		//Modify here the GPIO pins you're gonna use to trigger the relays.
		GpioPinDigitalOutput gpio_garaje=gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07,"Garage Relay",PinState.LOW);
		GpioPinDigitalOutput gpio_luces=gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08, "Light Relay",PinState.LOW);
		gpio_garaje.pulse(garaje_time, PinState.HIGH);
		if (l_enable.get()) {
			gpio_luces.pulse(light_time, PinState.HIGH);
		}
	}
}

