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
		String ruta;
		String extension;
		int opcion =1;

		try 
		{

			while(opcion<=3)
			{
				//Recibimos la opcion
				opcion=this.dis.readInt();
				
				//Enviamos el contenido de nuestro drive
				dos.writeChars(this.toString(""));
				dos.flush();
				
				if(opcion==1)
				{
					//recibimos la ruta
					ruta = dis.readLine();
					
					//Enviamos el nombre del archivo
					String[] rutas = ruta.split("\\");
					//Enviamos el nombre del fichero con su extensión si la tiene
					dos.writeChars(rutas[rutas.length-1]);
					
					enviarArchivo(ruta);
				}
				else if(opcion==2)
				{
					//recibimos la ruta
					ruta=dis.readLine();
					//recibimos la ruta
					recibirArchivo(ruta);

					
					
					
					
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
		File f = new File(a);
	    
	    try 
	    {
			FileInputStream fis = new FileInputStream(f);
			byte [] buff = new byte[1024*32];
			int leidos;
			while((leidos= fis.read(buff))!=-1)
			{
				this.dos.write(buff, 0, leidos);
			}
			dos.flush();
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
	public void  recibirArchivo(String ruta)
	{
		File f = new File(ruta);
		
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
