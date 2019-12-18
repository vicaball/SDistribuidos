package Cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
		String fichero;
		String extension;
		String contenido;
		String rutaServidor;
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
				comprobacion=false;
				while (comprobacion==false)
				{
					System.out.println("Introduce el nombre del usuario");
					usuario=teclado.nextLine();
					//Enviamos el usuario
					dos.writeChars(usuario);
					comprobacion=dis.readBoolean();
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
				//Introducimos menus
				System.out.println("Selecciona alguna de estas opciones: \r\n 1:Descargar Archivo \r\n  2:Enviar un archivo al servidor (introducir ruta añadiendo al inicio \\..) \r\n Otro:Terminar)");
				System.out.println("Introduce un numero para continuar");
				opcion=teclado.nextInt();
				//Enviamos la opcion
				dos.writeInt(opcion);
				//Recibimos el contenido del mennu
				contenido=dis.readLine();
				String [] menus=contenido.split(" ");
				for(int i=0;i<menus.length;i++)
				{
					System.out.println(menus[i]);
				}
				
				if(opcion==1)
				{
					System.out.println("Introduce el nombre del archivo a descargar(poner ruta del archivo empezando por //(Poner extension del archivo al final)");
					ruta=teclado.nextLine();
					//Enviamos la ruta del archivo a enviar
					dos.writeChars(ruta);
					//Recibimos el nombre del fichero
					fichero=dis.readLine();
					File f = new File(fichero);
					FileOutputStream f2 = new FileOutputStream(f);
					byte [] buff = new byte[1024*32];
					int leidos;
					//Escritura del archivo 
					while((leidos= dis.read(buff))!=-1) 
					{
						f2.write(buff, 0, leidos);
					}
				}
				else if (opcion==2)
				{
					//Ruta de nuestro cliente
					System.out.println("Introduce la ruta del archivo a enviar(Ordenador)(Introducir también la extension del fichero )");
					ruta=teclado.nextLine();
					
                	//Introducimos ruta donde queremos enviarlo
					System.out.println("Introduce la ruta en el servidor donde enviarlo(Servidor)(incluyendo extension)");
					rutaServidor=teclado.nextLine();
					dos.writeChars(rutaServidor);
					
					//envio del archivo al servidor
					File f = new File(ruta);
					FileInputStream f2 = new FileInputStream(f);
					byte[] buff = new byte[1024*32];
					int leidos;
					
					//Enviamos el archivo
					while((leidos=f2.read(buff))!=-1)
					{
						dos.write(buff,0,leidos);
					}
					dos.flush();
				}
				else
				{
					System.out.println("Terminando gracias por usar esta aplicación");
				}
			}
			
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

}
