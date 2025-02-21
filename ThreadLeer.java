package practica7BlockChain;

/**
 * Clase que crea un hilo para lectura.
 * 
 * @author Jorge Manzano Anchelergues y Jaime Usero Aranda.
 */

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.crypto.SecretKey;

public class ThreadLeer extends Thread{		
	
	/**
	 * id del nodo
	 */
	int idCliente;
	
	/**
	 * in del nodo
	 */
	ObjectInputStream in;
	
	/**
	 * out del nodo
	 */
	ObjectOutputStream out;
	
	/**
	 * LLave simetrica del nodo
	 */
	private SecretKey key;
	
	/**
	 * Lista con las transacciones
	 */
	private ArrayList<Transaccion> transacciones;
	
	/**
	 * BlockChain
	 */
	private ArrayList<Bloque> blockChain;
	
	/**
	 * Lista con als respuestas
	 */
	private ArrayList<Boolean> respuestas;
	
	/**
	 * Semaforo que no dejara que los hilos se interrumpan
	 */
	private Semaphore semaforoRecibir;
		
	/**
	 * Construye un hilo cliente lector
	 * 
	 * @param idCliente id del nodo
	 * @param in tuberia de lectura del nodo
	 * @param out tuberia de escritura del nodo
	 * @param key llave privada del nodo
	 * @param respuestas lista de respuestas
	 * @param semaforoRecibir semaforo para aue los hilos no chafen las respuestas
	 * @param transacciones las transacciones
	 * @param blockChain la lista de bloques
	 * @throws IOException si el stream falla
	 */
	public ThreadLeer(int idCliente, ObjectInputStream in, ObjectOutputStream out, SecretKey key, ArrayList<Boolean> respuestas,
			Semaphore semaforoRecibir, ArrayList<Transaccion> transacciones, ArrayList<Bloque> blockChain) throws IOException {
		super();		
		this.idCliente = idCliente;
		this.in = in;
		this.out = out;
		this.key = key;				
		this.transacciones = transacciones;
		this.blockChain = blockChain;	
		this.respuestas = respuestas;
		this.semaforoRecibir = semaforoRecibir;
	}
	
	/**
	 * Metodo que recibe un mensaje, y si es una transaccion, la guardara,
	 * si e sun bloque lo comprobara y devolvera true o false y esperara
	 * la respuesta del nodo para guardarlo o no
	 */
	public void run() {				
		try {								
			while(true) {				
				String respuesta = recibir();
				if(respuesta.equals("/minar")) {					
					int idRecibido = Integer.parseInt(recibir());
					if(idRecibido != idCliente) {
						enviar("/minar");
						enviar(""+idRecibido);
						// Paso 1 - Recibir bloque
						Bloque respuestaBloque = recibirBloque();
						// Paso 2 - Sacar hash del bloque
						String bloqueHasheado = Utilities.hashear(respuestaBloque.getBytes());						
						// Paso 3 - enviar respuesta
						enviar((bloqueHasheado.substring(0, 3).equals("000"))?"True":"false");						
						//Paso 4 - Esperar confirmacion y actuar
						if(recibir().equalsIgnoreCase("Añadir")) {
							blockChain.add(respuestaBloque);
							transacciones.removeAll(blockChain.getLast().getTransacciones());
							System.out.println("Nuevo bloque añadido " + blockChain.getLast());
						} else System.out.println("Bloque no añadido");
					}
					else {						
						semaforoRecibir.acquire();
						respuestas.add(Boolean.parseBoolean(recibir()));
						semaforoRecibir.release();						
					}														
				}
				if(respuesta.equals("/enviar")) {
					transacciones.add(recibirTransaccion());					
					System.out.println("Nueva transaccion añadida: " + transacciones.getLast());
				}				
			}		
		} catch (EOFException | SocketException e) {
			System.out.println("Conexion terminada");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {			
			e.printStackTrace();
		}       
	}		
	  
	/**
	 * Metodo que descifra un mensaje
	 * 
	 * @param encriptado mensaje a descifrar
	 * @return el mensaje descifrado
	 * @throws Exception por si el descifrado falla
	 */
	private String descifrar(byte[] encriptado) throws Exception {
		return new String(Utilities.descifrarAES(key, encriptado), StandardCharsets.UTF_8);
	}
	
	/**
	 * Metodo que descifra un bloque
	 * 
	 * @param encriptado bloque a descifrar
	 * @return el bloque descifrado
	 * @throws Exception por si el descifrado falla
	 */
	private Bloque descifrarBloque(byte[] encriptado) throws Exception {
		Bloque b = new Bloque("", 0);
		return b.toBloque(Utilities.descifrarAES(key, encriptado));
	}
	 
	/**
	 * Metodo que descifra una transaccion
	 * 
	 * @param encriptado mensaje a descifrar
	 * @return la transaccion descifrada
	 * @throws Exception por si el descifrado falla
	 */
	private Transaccion descifrarTransaccion(byte[] encriptado) throws Exception {
		Transaccion t = new Transaccion(0, 0, 0);
		return t.toTransaccion(Utilities.descifrarAES(key, encriptado));
	}
	
	/**
	 * Metodo que recibe un mensaje cifrado y 
	 * lo descifra
	 * 
	 * @return el mensaje descifrado
	 * @throws ClassNotFoundException si falla al castear la clase
	 * @throws IOException si falla la tuberia
	 * @throws Exception si falla el cifrado
	 */
	private String recibir() throws ClassNotFoundException, IOException, Exception {
		return descifrar((byte[])in.readObject());
	}
	
	/**
	 * Metodo que recibe un bloque cifrado y
	 * lo descifra
	 * 
	 * @return el bloque descifrado
	 * @throws ClassNotFoundException si falla al castear la clase
	 * @throws IOException si falla la tuberia
	 * @throws Exception si falla el cifrado
	 */
	private Bloque recibirBloque() throws ClassNotFoundException, IOException, Exception {				
		return descifrarBloque((byte[])in.readObject());
	}
	
	/**
	 * Metodo que recibe una transaccion cifrada y
	 * la descifra
	 * 
	 * @return la transaccion descifrada
	 * @throws ClassNotFoundException si falla al castear la clase
	 * @throws IOException si falla la tuberia
	 * @throws Exception si falla el cifrado
	 */
	private Transaccion recibirTransaccion() throws ClassNotFoundException, IOException, Exception {		
		return descifrarTransaccion((byte[])in.readObject());
	}
	
	/**
	 * Metodo que envia un mensaje cifrado
	 * 
	 * @param t el mensaje a enviar
	 * @throws IOException si las tuberias fallan
	 * @throws Exception si el cifrado falla
	 */
	private void enviar(String t) throws IOException, Exception {		 			
		out.writeObject(cifrar(t, key));
		out.flush();
	}
	
	/**
	 * Metodo que cifra un mensaje
	 * 
	 * @param texto mensaje a cifrar
	 * @param key llave simetrica a usar
	 * @return el mensaje cifrado
	 * @throws Exception por si el cifrado falla
	 */
	private byte[] cifrar(String texto, SecretKey key) throws Exception {
		return Utilities.cifrarAES(key, texto.getBytes(StandardCharsets.UTF_8));
	}
}