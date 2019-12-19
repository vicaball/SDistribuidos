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
		String contrase�a=null;
		String correoelectronico=null;
		Executor pool = Executors.newCachedThreadPool();
		
		try(ServerSocket ss = new ServerSocket(82)){
			while(true)
			{	
				try
				{
					   Socket s = ss.accept();
					   DataOutputStream dos= new DataOutputStream(s.getOutputStream());
					   DataInputStream dis = new DataInputStream(s.getInputStream());
					
				
						//Pedimos que seleccione una opcion entre registrarse y logearse 
						opcion = dis.readInt();
						
						//Opcion logearse
						if(opcion==1)
						{
							System.out.println("prueba");
							boolean comprobacion1 = false;
							
							while(comprobacion1==false)
							{
								//recibimos el usuario
								usuario=dis.readLine();
//								System.out.println("usuario: "+usuario);
//								usuario=dis.readLine();
								System.out.println("usuario: "+usuario);
								//comprobamos que el usuario se encuentra en nuestro xml
								comprobacion1 = comprobacionUsuarioExistente(usuario);
								System.out.println(comprobacion1);
								dos.writeBoolean(comprobacion1);
								if(comprobacion1==true)
								{
									boolean comprobacion2=false;
									while(comprobacion2 == false)
									{
										contrase�a = dis.readLine();
										comprobacion2=comprobacionUsuarioContrase�a(usuario,contrase�a);
										dos.writeBoolean(comprobacion2);
									}
									//logeo correcto
								}
							}
						}
						else 
						{
							boolean comprobacion= true;
							while(comprobacion==true)
							{
								//Recibimos el usuario
								usuario=dis.readLine();
								//Comprobamos que el usuario no esta en nustro xml
								comprobacion=comprobacionUsuarioExistente(usuario);
								System.out.println(comprobacion);
								dos.writeBoolean(comprobacion);
								
							}
							System.out.println("Introduce la contrase�a");
							//Recibimos contrase�a
							contrase�a=dis.readLine();
							//Recibimos correoelectronico
							System.out.println("Introduce el correo electronico");
							correoelectronico=dis.readLine();
							aniadirusuarioNuevo(usuario, contrase�a, correoelectronico);
							
							String aux="src/almacen/"+usuario+"/Bienvenido.txt";
							usuario="src/almacen/"+usuario;
							File f = new File(usuario);
							
							
						}
						
						
						pool.execute(new ServidorHilo(s,usuario));
						
		
						
				}
				
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	//Este metodo comprueba que la contrase�a recibida es correcta
	public static boolean comprobacionUsuarioExistente(String a)
	{
		boolean esta = false;
		try 
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			Document doc =db.parse(new File("src/dtds/Usuarios.xml"));
			
			//Obtengo el elemento root
			Element root = doc.getDocumentElement();
			NodeList aux = root.getElementsByTagName("cliente");
			//Compruebo que a esta en el documento xml
			for(int i=0; i<aux.getLength();i++)
			{
				Element aux2 = (Element) aux.item(i);
				String usuario = (String) aux2.getAttribute("usuario");
				System.out.println(usuario);
				if(usuario.equals(a))
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
			
			Document doc =db.parse(new File("src/dtds/Usuarios.xml"));
			Element root = doc.getDocumentElement();
			
			
			//creamos los elementos para a�adir
			Element cliente = doc.createElement("cliente");
			cliente.setAttribute("usuario", usuario);
			Element contrase�a = doc.createElement("contrasena");
			Element correoelectronico = doc.createElement("correoelectronico");
			
			//realizamos las uniones de los elementos d manera correcta
			Text txt2 = doc.createTextNode(contrasena);
			Text txt3 = doc.createTextNode(correo);
			contrase�a.appendChild(txt2);
			correoelectronico.appendChild(txt3);
			cliente.appendChild(contrase�a);
			cliente.appendChild(correoelectronico);
			root.appendChild(cliente);
			//Realizamos la transformacion
			
			DOMSource  source = new DOMSource(doc);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			StreamResult result = new StreamResult(new File("src/dtds/Usuarios.xml"));
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
	
	//Este metodo comprueba que �la contrase�a recibida es correcta
	public static boolean comprobacionUsuarioContrase�a(String usuario, String contrase�a)
	{
		boolean esta = false;
		try 
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			Document doc =db.parse(new File("src/dtds/Usuarios.xml"));
			
			//Obtengo el elemento root
			Element root = doc.getDocumentElement();
			NodeList aux = root.getElementsByTagName("cliente");
			for(int i=0; i<aux.getLength();i++)
			{
				Element aux2 = (Element) aux.item(i);
				String us1 = (String) aux2.getAttribute("usuario");
				String contrasena = (String) aux2.getElementsByTagName("contrasena").item(0).getTextContent();
				if(us1.equals(usuario) && contrase�a.equals(contrasena))
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
