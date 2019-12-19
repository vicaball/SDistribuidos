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
		String aux;
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
			aux=teclado.nextLine();
			opcion = Integer.parseInt(aux);
			if(opcion==1)
			{
				//Enviamos la opcion
				dos.writeInt(opcion);
				while(comprobacion==false)
				{
					//Introducción del usuario y contraseña
					System.out.println("Introduzca el usuario:");
					usuario=teclado.nextLine();
					usuario=usuario+"\r\n";
					dos.writeBytes(usuario);
					//Recibo si el usuario existe
					comprobacion=dis.readBoolean();
				}	
				comprobacion=false;
				while(comprobacion== false)
				{
					System.out.println("Introduce la contraseña: ");
					contraseña = teclado.nextLine();
					contraseña=contraseña+"\r\n";
					//Recibo la contraseña
					dos.writeBytes(contraseña);
					//Recibo si la contraseña es correcta
					comprobacion=dis.readBoolean();
				}
			}
			else if(opcion==2)
			{
				//Enviamos la opcion
				dos.writeInt(opcion);
				comprobacion=true;
				while (comprobacion==true)
				{
					System.out.println("Introduce el nombre del usuario");
					usuario=teclado.nextLine();
					usuario=usuario+"\r\n";
					//Enviamos el usuario
					dos.writeBytes(usuario);
					comprobacion=dis.readBoolean();
				}
				System.out.println("introduce la contraseña");
				contraseña=teclado.nextLine();
				contraseña=contraseña+"\r\n";
				//Enviamos contraseña
				dos.writeBytes(contraseña);
				System.out.println("Introduce el correo electrónico");
				correoelectronico=teclado.nextLine();
				correoelectronico=correoelectronico+"\r\n";
				//Enviamos correoelectronico
				dos.writeBytes(correoelectronico);
			}
			//Recibimos mensaje bienvenida
			System.out.println(dis.readLine());
			opcion=1;
			while(opcion<3)
			{
				//Introducimos menus
				System.out.println("Selecciona alguna de estas opciones: \r\n1:Descargar Archivo \r\n2:Enviar un archivo al servidor (introducir ruta añadiendo al inicio \\..) \r\nOtro:Terminar)");
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
					System.out.println("Introduce el nombre del archivo a descargar(poner ruta)(Poner extension del archivo al final)");
					ruta=teclado.nextLine();
					ruta=ruta+"\r\n";
					//Enviamos la ruta del archivo a enviar
					dos.writeBytes(ruta);
					//Recibimos el nombre del fichero
					fichero=dis.readLine();
					File f = new File(fichero);
					long ftam=f.length();
					
					FileOutputStream f2 = new FileOutputStream(f);
					byte [] buff = new byte[1024*32];
					int leidos=0;
					int aux2=dis.read(buff);
					//Escritura del archivo 
					while((leidos=leidos+aux2)==ftam) 
					{
						f2.write(buff, 0, aux2);
						aux2=dis.read(buff);
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
					dos.writeBytes(rutaServidor);
					
					//envio del archivo al servidor
					File f = new File(ruta);
					long ftam = f.length();
					dos.writeLong(ftam);
					
					FileInputStream f2 = new FileInputStream(f);
					byte[] buff = new byte[1024*32];
					int leidos=0;
					int aux2=f2.read(buff);
					
					//Enviamos el archivo
					while((leidos=leidos+aux2)==ftam)
					{
						dos.write(buff,0,leidos);
						f2.read(buff);
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
