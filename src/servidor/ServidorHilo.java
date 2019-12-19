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

	
    public ServidorHilo(Socket s,String usuario)
    {
    	this.s=s;
    	this.ruta="src/almacen/"+usuario;
    	try 
    	{
			this.dis=new DataInputStream(s.getInputStream());
		 	this.dos= new DataOutputStream(s.getOutputStream());
		} 
    	catch (IOException e) 
    	{
			
			e.printStackTrace();
		}

    }
    
	public void run() 
	{
		String espacios;
		String bienvenida;
		String ruta;
		String extension;
		String aux2;
		int tamaño;
		int opcion =1;

		try 					
		{
        	while(opcion<3)
			{
				String[] aux=this.ruta.split("/");
				bienvenida="Bienvenido a tu servidor "+aux[aux.length-1];
				bienvenida=bienvenida+"\r\n";
				dos.writeBytes(bienvenida);
				
				//Recibimos la opcion
				opcion=dis.readInt();
				
				
				//Enviamos el contenido de nuestro drive
				espacios=this.toString()+"\r\n";
				dos.writeBytes(espacios);
				dos.flush();
				
				if(opcion==1)
				{
					//recibimos la ruta
					ruta = dis.readLine();
					File f = new File(ruta);
					
					//Enviamos el nombre del archivo
					String[] rutas = ruta.split("/");
					//Enviamos el nombre del fichero con su extensión si la tiene
					ruta=rutas[rutas.length-1]+"\r\n";
					dos.writeBytes(ruta);
					
					//Enviamos el tamaño
					dos.writeLong(f.length());

					
					enviarArchivo(ruta,f.length());
				}
				else if(opcion==2)
				{
					//recibimos la ruta
					ruta=dis.readLine();
					long ftam=dis.readLong();
					//recibimos la ruta
					recibirArchivo(ruta,ftam);

					
					
					
					
				}
			  
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				this.dos.close();
				this.dis.close();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
			
			
		
	
		
		
	}
	
	//Muestra el contanido de la ruta
	public String toString( String ruta)
	{
		String resultado=""; 
		File f = new File(this.ruta+ ruta);
		if(f.isDirectory())
		{
			resultado = f.getAbsolutePath()+" ";  //
			File [] f2 = f.listFiles();
	     
			for(int i=0;i<f2.length;i++)
			{
				if(f2[i].isDirectory()==true)
				{
					resultado= resultado + f2[i].getAbsolutePath()+" ";
					resultado= resultado+this.toString("/"+f2[i].getName())+" ";
				}
				else
				{
					resultado= resultado+f.getAbsolutePath()+" ";
				}
	    	 
			}
		}
		else
		{
			resultado = resultado+f.getAbsolutePath()+" ";
		}
	     
	    
	     return resultado;
	}
	
	
	//Envia un archivo al cliente
	public void enviarArchivo(String a,long tamaño)
	{
		File f = new File(a);
	    
	    try 
	    {
			FileInputStream fis = new FileInputStream(f);
			byte [] buff = new byte[1024*32];
			int leidos=0;
			int aux=fis.read(buff);
			while((leidos= leidos+aux)==tamaño)
			{
				this.dos.write(buff, 0, aux);
				aux=fis.read(buff);
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
	public void  recibirArchivo(String ruta,long tamaño)
	{
		File f = new File(ruta);
		
		try 
		{
			FileOutputStream fos = new FileOutputStream(f);
			byte [] buff = new byte[1024*32];
			int leidos=0;
			int aux = dis.read(buff);
			while((leidos=leidos+aux)==tamaño) 
			{
				fos.write(buff, 0, aux);
				aux = dis.read(buff);
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
