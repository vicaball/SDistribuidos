package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ServidorHilo implements Runnable 
{
    private Socket s;
    private String ruta;
    private DataOutputStream dos;
    private DataInputStream dis;
	
    public ServidorHilo(Socket s,String usuario, DataOutputStream dos, DataInputStream dis)
    {
    	this.s=s;
    	this.ruta="src\\almacen\\"+"usuario";
    	this.dis=dis;
    	this.dos=dos;
    }
    
	public void run() 
	{
		
		String introduccion;
		String menu;
		String ruta;
		String extension;
		int opcion;
		String fichero;
		boolean seguir = true;
		introduccion="Bienvenido a tu servidor: "+this.ruta;
		menu = "Selecciona alguna de estas opciones: \r\n 1:Descargar Archivo \r\n  2:Enviar un archivo al servidor (introducir ruta añadiendo al inicio \\..) \r\n 3:Terminar)";
		try 
		{
			//Enviamos mensaje bienvenida
			dos.writeChars(introduccion);
			dos.flush();
			while(seguir==true)
			{
			   introduccion="Introduce una opcion de estas para continuar \r\n";
			   dos.writeBytes(introduccion);
			   dos.flush();
			   dos.writeBytes(menu);
			   dos.flush();
			   opcion = dis.readInt();
			   dos.writeBytes(toString(""));
			   dos.flush();
			   if(opcion==1)
			   {
				  introduccion="Selecciona el nombre del fichero a descargar dentro de esa carpeta (acompañado de la ruta empezando por \\)";
				  dos.writeBytes(introduccion);
				  dos.flush();
				  //leemos el fichero a recibir
				  fichero=dis.readLine();
				  enviarArchivo(fichero);
				  
			   }
			   else if(opcion == 2)
			   {
				   introduccion="Seleccione la nueva ruta de acorde a las instrucciones dadas anteriormente \r\n Introduce la ruta \r\n";
				   dos.writeBytes(introduccion);
				   dos.flush();
				   //Recibimos la ruta
				   ruta = dis.readLine();
				   introduccion="Introduce la extension \r\n";
				   dos.writeBytes(introduccion);
				   dos.flush();
				   extension = dis.readLine();
				   recibirArchivo(ruta,extension);
			   }
			   else
			   {
				   //finaliza 
				   seguir=false;
			   }
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
			
			
		
	
		
		
	}
	
	//Muestra el contanido de la ruta
	public String toString( String ruta)
	{
		String resultado; 
		File f = new File(this.ruta+ ruta);
		if(f.isDirectory())
		{
			resultado = f.getAbsolutePath()+"\r\n";  //Preguntar 
			File [] f2 = f.listFiles();
	     
			for(int i=0;i<f2.length;i++)
			{
				if(f2[i].isDirectory()==true)
				{
					resultado= resultado+this.toString("\\"+f2[i].getName())+"\r\n";
				}
				else
				{
					resultado= resultado+f.getAbsolutePath()+"\r\n";
				}
	    	 
			}
		}
		else
		{
			resultado=f.getAbsolutePath();
		}
	     
	    
	     return resultado;
	}
	
	
	//Envia un archivo al cliente
	public void enviarArchivo(String a)
	{
		File f = new File(this.ruta+a);
	    
	    try 
	    {
			FileInputStream fis = new FileInputStream(f);
			byte [] buff = new byte[1024*32];
			int leidos;
			while((leidos= fis.read(buff))!=-1)
			{
				this.dos.write(buff, 0, leidos);
				dos.flush();
			}
			//preguntar extension
		} 
	    catch (FileNotFoundException e)
	    {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    catch (IOException e)
	    {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	//Recibe un archivo
	public void  recibirArchivo(String ruta, String extension)
	{
		File f = new File(this.ruta+ruta+extension);
		//Extension
		//f.getName().substring(f.getName().lastIndexOf('.'));
		
		try 
		{
			FileOutputStream fos = new FileOutputStream(f);
			byte [] buff = new byte[1024*32];
			int leidos;
			while((leidos= dis.read(buff))!=-1) 
			{
				fos.write(buff, 0, leidos);
			}
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

			
	}
		
	
    
}
