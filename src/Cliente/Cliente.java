package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente 
{
	public static void main (String [] args)
	{
		String usuario;
		String contraseña;
		Scanner teclado = new Scanner(System.in);
		
		try
		(Socket s = new Socket ("192.168.56.1",82);
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				DataInputStream dis = new DataInputStream(s.getInputStream());
				)
		{
			
			
				//Introducción del usuario y contraseña
				System.out.println("Bienvenido a mi servidor \r\n Introduzca el usuario: \r\n");
				usuario=teclado.nextLine();
				dos.writeBytes(usuario);
			
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

}
