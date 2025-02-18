package practica7BlockChain;

/**
 * Clase que crea un nodo y lo inicia.
 * 
 * @author Jorge Manzano Anchelergues y Jaime Usero Aranda.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Clase que crea un cliente y lo inicia.
 * 
 * @author Jorge Manzano Ancglergues y Jaime Usero Aranda
 */

import java.util.Scanner;

public class MainNodo {
	public static void main(String[] args) {
		try {
			Scanner escaner = new Scanner(System.in);		  		  
			int puertoServidor =  cargarPuerto();	  		  		  		 
			String hostServidor = cargarHost();	  									
			System.out.print("Introduce el puerto del cliente: ");						
			Nodo cliente = new Nodo(hostServidor, puertoServidor, InetAddress.getLocalHost().getHostAddress(), escaner.nextInt());	
			System.out.println("");
			cliente.startClient(escaner);		  	 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}  
  
	/**
	 * Metodo que lee el fichero .env
	 * y carga las variables con su informacion.
	 *
	 * @throws FileNotFoundException si el fichero no existe
	 * @throws IOException si el stream falla.
	 */
	public static String cargarHost() throws FileNotFoundException, IOException {		
		
	  BufferedReader b = new BufferedReader(new FileReader(new File("src/practica7BlockChain/.env").getAbsolutePath()));
	  String linea = b.readLine();
	  while(linea != null) {
		  String[] lineaPartida = linea.split("=");
		  if(lineaPartida[0].equalsIgnoreCase("master.host")) {
			  b.close();
			  return lineaPartida[1].replaceAll("\"", "");		  
		  }
		  linea = b.readLine();
	  }                             
	  b.close(); 
	  return null;
	}  
	
	/**
	 * Metodo que lee el fichero .env
	 * y carga las variables con su informacion.
	 *
	 * @throws FileNotFoundException si el fichero no existe
	 * @throws IOException si el stream falla.
	 */
	public static int cargarPuerto() throws FileNotFoundException, IOException {		
	  BufferedReader b = new BufferedReader(new FileReader(new File("src/practica7BlockChain/.env").getAbsolutePath()));
	  String linea = b.readLine();
	  while(linea != null) {
		  String[] lineaPartida = linea.split("=");				  
		  if(lineaPartida[0].equalsIgnoreCase("master.port")) {
			  b.close();
			  return Integer.parseInt(lineaPartida[1].replaceAll("\"", ""));
		  }
		  linea = b.readLine();
	  }                             
	  b.close(); 
	  return 0;
	}  
}