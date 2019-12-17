package Cliente;

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
		String correoelectronico;
		String ruta;
		int opcion;
		Scanner teclado = new Scanner(System.in);
		
		try
		(Socket s = new Socket ("192.168.56.1",82);
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				DataInputStream dis = new DataInputStream(s.getInputStream());
				)
		{
			boolean comprobacion=false;
			System.out.println("Bienvenido a mi servidor \r\n Introduce 1 para loguearte \r\n Introduce 2 para registrarte \r\n otro para salir");
			opcion = teclado.nextInt();
			if(opcion==1)
			{
				//Enviamos la opcion
				dos.writeInt(opcion);
				while(comprobacion==false)
				{
					//Introducción del usuario y contraseña
					System.out.println("Introduzca el usuario:");
					usuario=teclado.nextLine();
					dos.writeChars(usuario);
					//Recibo si el usuario existe
					comprobacion=dis.readBoolean();
				}	
				comprobacion=false;
				while(comprobacion== false)
				{
					System.out.println("Introduce la contraseña: ");
					contraseña = teclado.nextLine();
					//Recibo la contraseña
					dos.writeChars(contraseña);
					//Recibo si la contraseña es correcta
					comprobacion=dis.readBoolean();
				}
			}
			else if(opcion==2)
			{
				//Enviamos la opcion
				dos.writeInt(opcion);
				while (comprobacion==false)
				{
					System.out.println("Introduce el nombre del usuario");
					usuario=teclado.nextLine();
					//Enviamos el usuario
					dos.writeChars(usuario);	
				}
				System.out.println("introduce la contraseña");
				contraseña=teclado.nextLine();
				//Enviamos contraseña
				dos.writeChars(contraseña);
				System.out.println("Introduce el correo electrónico");
				correoelectronico=teclado.nextLine();
				//Enviamos correoelectronico
				dos.writeChars(correoelectronico);
			}
			//Recibimos mensaje bienvenida
			System.out.println(dis.readLine());
			opcion=1;
			while(opcion<3)
			{
				//mensaje de introducir opcion 
				System.out.println(dis.readLine());
				//Menu
				for(int i=0;i<4;i++)
				{
					System.out.println(dis.readLine());
				}
				//Enviamos la opcion
				opcion = teclado.nextInt();
				dos.writeInt(opcion);
				//Contenido de nuestro drive
				System.out.println((dis.readLine()));
				if(opcion==1)
				{
					//mensaje de seleccion de fichero
					System.out.println(dis.readLine());
					ruta=teclado.nextLine();
				
				}



				
			}
			
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

}
