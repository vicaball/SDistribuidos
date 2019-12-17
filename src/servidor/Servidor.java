package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class Servidor 
{
	public static void main (String[] args)
	{
		int opcion;
		String usuario=null;
		String contraseņa=null;
		String correoelectronico=null;;
		try
		(ServerSocket ss = new ServerSocket(82);
				Socket s = ss.accept();
				DataOutputStream dos= new DataOutputStream(s.getOutputStream());
				DataInputStream dis = new DataInputStream(s.getInputStream())
				)
		{
			while(true)
			{
				//Pedimos que seleccione una opcion entre registrarse y logearse 
				opcion = dis.read();
				
				//Opcion logearse
				if(opcion==1)
				{
					boolean comprobacion = false;
					
					while(comprobacion==false)
					{
						//recibimos el usuario
						usuario=dis.readLine();
						//comprobamos que el usuario se encuentra en nuestro xml
						comprobacion = comprobacionUsuarioExistente(usuario);
						dos.writeBoolean(comprobacion);
						if(comprobacion==true)
						{
							comprobacion=false;
							while(comprobacion == false)
							{
								contraseņa = dis.readLine();
								comprobacion=comprobacionUsuarioContraseņa(usuario,contraseņa);
								dos.writeBoolean(comprobacion);
							}
							//logeo correcto
						}
					}
				}
				else
				{
					boolean comprobacion= false;
					while(comprobacion==false)
					{
						//Recibimos el usuario
						usuario=dis.readLine();
						//Comprobamos que el usuario no esta en nustro xml
						comprobacion=comprobacionUsuarioExistente(usuario);
						dos.writeBoolean(comprobacion);
						
					}
					//Recibimos contraseņa
					contraseņa=dis.readLine();
					//Recibimos correoelectronico
					correoelectronico=dis.readLine();
					aniadirusuarioNuevo(usuario, contraseņa, correoelectronico);
				}
				
				Executor pool = Executors.newCachedThreadPool();
				pool.execute(new ServidorHilo(s,usuario,dos,dis));
				

				
			}
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//Este metodo comprueba que la contraseņa recibida es correcta
	public static boolean comprobacionUsuarioExistente(String a)
	{
		boolean esta = false;
		try 
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			Document doc =db.parse(new File("src\\dtds\\Usuarios.xml"));
			
			//Obtengo el elemento root
			Element root = doc.getDocumentElement();
			NodeList aux = root.getElementsByTagName("cliente");
			//Compruebo que a esta en el documento xml
			for(int i=0; i<aux.getLength();i++)
			{
				Element aux2 = (Element) aux.item(i);
				String usuario = (String) aux2.getAttribute("usuario");
				
				if(usuario==a)
				{
					esta=true;
				}
			}
		} 
		catch (ParserConfigurationException e) 
		{
			e.printStackTrace();
		} 
		catch (SAXException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
		return esta;
	}
	
	public static boolean aniadirusuarioNuevo(String usuario, String contrasena, String correo)
	{
		boolean b = false;
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			Document doc =db.parse(new File("src\\dtds\\Usuarios.xml"));
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("src\\dtds\\Usuarios.xml"));
			
			//creamos los elementos para aņadir
			Element cliente = doc.createElement("cliente");
			cliente.setAttribute("usuario", usuario);
			Element contraseņa = doc.createElement("contrasena");
			Element correoelectronico = doc.createElement("correoelectronico");
			Element root = doc.createElement("root");
			
			//realizamos las uniones de los elementos d manera correcta
			Text txt2 = doc.createTextNode(contrasena);
			Text txt3 = doc.createTextNode(correo);
			contraseņa.appendChild(txt2);
			correoelectronico.appendChild(txt3);
			cliente.appendChild(contraseņa);
			cliente.appendChild(correoelectronico);
			root.appendChild(cliente);
			//Realizamos la transformacion
			transformer.transform(source, result);
			b=true;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		} 
		catch (ParserConfigurationException e) 
		{
			e.printStackTrace();
		} 
		catch (SAXException e)
		{
			e.printStackTrace();
		} 
		catch (TransformerConfigurationException e) 
		{
			e.printStackTrace();
		} catch (TransformerException e) 
		{
			e.printStackTrace();
		}
		return b;
	}
	
	//Este metodo comprueba que ņla contraseņa recibida es correcta
	public static boolean comprobacionUsuarioContraseņa(String usuario, String contraseņa)
	{
		boolean esta = false;
		try 
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			Document doc =db.parse(new File("src\\dtds\\Usuarios.xml"));
			
			//Obtengo el elemento root
			Element root = doc.getDocumentElement();
			NodeList aux = root.getElementsByTagName("cliente");
			for(int i=0; i<aux.getLength();i++)
			{
				Element aux2 = (Element) aux.item(i);
				String us1 = (String) aux2.getAttribute("usuario");
				String contrasena = (String) aux2.getElementsByTagName("contrasena").item(0).getTextContent();
				if(us1 == usuario && contraseņa== contrasena)
				{
					esta=true;
				}
			}

		} 
		catch (ParserConfigurationException e) 
		{
			e.printStackTrace();
		} 
		catch (SAXException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
		return esta;
	}

}
