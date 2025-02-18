package practica7BlockChain;

/**
 * Clase que crea un hilo para escritura.
 * 
 * @author Jorge Manzano Anchelergues y Jaime Usero Aranda.
 */

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import javax.crypto.SecretKey;

public class ThreadEscribir extends Thread{			
		
	/**
	 * id del nodo
	 */
	int idCliente;
	
	/**
	 * Escaner por el que escribira el cliente
	 */
	private Scanner escaner;
	
	/**
	 * llaves publicas de los nodos
	 */
	private ArrayList<SecretKey> nodosKey;
	
	/**
	 * Nodos out
	 */
	private ArrayList<ObjectOutputStream> nodosOut;	
	
	/**
	 * Lista con las transacciones
	 */
	private ArrayList<Transaccion> transacciones;
	
	/**
	 * BlockChain
	 */
	private ArrayList<Bloque> blockChain;	
	
	/**
	 * Lista con las respuestas del bloque
	 */
	private ArrayList<Boolean> respuestas;
	
	/**
	 * Construye un hilo cliente escritor
	 * 
	 * @param idCliente el id del nodo	 
	 * @param escaner el escaner por el que escribira el nodo
	 * @param nodosKey llaves simetricas de los nodos
	 * @param respuestas lista de boolean de los bloques
	 * @param nodosE out de los nodos
	 * @param transacciones lista con todas las transacciones
	 * @param blockChain la lista de bloques	 
	 * @throws IOException si el stream falla
	 */
	public ThreadEscribir(int idCliente, Scanner escaner, ArrayList<SecretKey> nodosKey, ArrayList<Boolean> respuestas, 
			ArrayList<ObjectOutputStream> nodosE, ArrayList<Transaccion> transacciones, ArrayList<Bloque> blockChain) throws IOException {
		super();		
		this.idCliente = idCliente;
		this.escaner = escaner;
		this.nodosKey = nodosKey;
		this.transacciones = transacciones;
		this.blockChain = blockChain;
		this.nodosOut = nodosE;		
		this.respuestas = respuestas;
	}
	
	/**
	 * Metodo que recibe un mensaje por la tuberia,
	 * dependiendo del mensaje enviara una transaccion o
	 * un bloque
	 */
	public void run() {				
		try {				
			while(true) {				
				String mensaje = escaner.nextLine();
				enviar(mensaje);
				if(mensaje.equals("/minar")) {
					enviar(""+idCliente);
					// Paso 1 - Crear Bloque
					Bloque b;
					int j= 0;					
					while(true) {						
						if(blockChain.isEmpty()) b = new Bloque(null, LocalDateTime.now().toString(), j++, transacciones);
						else b = new Bloque(Utilities.hashear(blockChain.getLast().getBytes()), LocalDateTime.now().toString(), j++, transacciones);
						String bloqueHasheado = Utilities.hashear(b.getBytes());						
						if(bloqueHasheado.substring(0, 3).equals("000")) break;												
					}
					// Paso 2 - Enviar bloque					
					enviar(b);					
					// Paso 3 - Recibir respuestas					
					while(respuestas.size() != nodosOut.size()) ThreadEscribir.sleep(1000);
					// Paso 4 - Mandar confirmacion depende de las respuestas					
					if(!respuestas.contains(false)) {
						blockChain.add(b);
						transacciones.removeAll(b.getTransacciones());
						enviar("Añadir");
						System.out.println("Añadido");
					}
					else {
						enviar("No añadir");
						System.out.println("No añadido");
					}
					respuestas.clear();
				}
				if(mensaje.equals("/enviar")) {
					System.out.println("Tu id es el " + idCliente);
					System.out.println("Introduce el id del receptor: ");
					int index = escaner.nextInt();
					while(index == idCliente) {
						System.out.println("No puedes hacerte una transaccion a ti mismo!!");
						System.out.print("Reindtroduce el id del receptor: ");
						index = escaner.nextInt(); 
					}
					System.out.println("Introduce el monto: ");
					Transaccion t = new Transaccion(idCliente, index, escaner.nextInt());
					transacciones.add(t);					
					
					enviar(t);
				}							
			}		
		} catch (EOFException e) {
			System.out.println("Conexion terminada");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {			
			e.printStackTrace();
		}	
	}
	  
	/**
	 * Metodo que cifra una transaccion
	 * 
	 * @param tr transaccion a cifrar
	 * @param key llave simetrica para cifrar
	 * @return la transaccion cifrada
	 * @throws Exception por si el cifrado falla
	 */
	private byte[] cifrar(Transaccion tr, SecretKey key) throws Exception {
		return Utilities.cifrarAES(key, tr.getBytes());
	}
	
	/**
	 * Metodo que cifra un bloque
	 * 
	 * @param bl bloque a cifrar
	 * @param key llave simetrica para cifrar
	 * @return el bloque cifrada
	 * @throws Exception por si el cifrado falla
	 */
	private byte[] cifrar(Bloque bl, SecretKey key) throws Exception {
		return Utilities.cifrarAES(key, bl.getBytes());
	}
	
	/**
	 * Metodo que cifra un mensaje
	 * 
	 * @param texto mensaje a cifrar
	 * @param key llave simetrica para cifrar
	 * @return el mensaje cifrado
	 * @throws Exception por si el cifrado falla
	 */
	private byte[] cifrar(String texto, SecretKey key) throws Exception {
		return Utilities.cifrarAES(key, texto.getBytes(StandardCharsets.UTF_8));
	}
	 
	/**
	 * Metodo que envia una transaccion cifrada
	 * 
	 * @param t la transaccion a enviar
	 * @throws IOException si las tuberias fallan
	 * @throws Exception si el cifrado falla
	 */
	private void enviar(Transaccion t) throws IOException, Exception {
		for(int i = 0; i < nodosOut.size(); i++) { 			
			nodosOut.get(i).writeObject(cifrar(t, nodosKey.get(i)));
			nodosOut.get(i).flush();
		}
	}
	
	/**
	 * Metodo que envia un bloque cifrado
	 * 
	 * @param b el bloque a enviar
	 * @throws IOException si las tuberias fallan
	 * @throws Exception si el cifrado falla
	 */
	private void enviar(Bloque b) throws IOException, Exception {
		for(int i = 0; i < nodosOut.size(); i++) { 			
			nodosOut.get(i).writeObject(cifrar(b, nodosKey.get(i)));
			nodosOut.get(i).flush();
		}
	}
	
	/**
	 * Metodo que envia un mensaje cifrado
	 * 
	 * @param text el mensaje a enviar
	 * @throws IOException si las tuberias fallan
	 * @throws Exception si el cifrado falla
	 */
	private void enviar(String text) throws IOException, Exception {
		for(int i = 0; i < nodosOut.size(); i++) { 			
			nodosOut.get(i).writeObject(cifrar(text, nodosKey.get(i)));
			nodosOut.get(i).flush();
		}
	}
}