package practica7BlockChain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Clase que crea un cliente
 * 
 * @author Jorge Manzano Anchelergues y Jaime Usero Aranda
 */

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import javax.crypto.SecretKey;

/**
 * Clase que crea un nodo maestro.
 * 
 * @author Jorge Manzano Anchelergues y Jaime Usero Aranda
 */

public class NodoMaster extends Conexion {
	 
	/**
	 * id del cliente
	 */
	int id;
	
	/**
	 * Llave privada del cliente
	 */
	private PrivateKey clavePrivada;
	
	/**
	 * LLave publica del cliente
	 */
	private PublicKey clavePublica;
	
	/**
	 * Claves simetricas
	 */
	private ArrayList<SecretKey> clavesSimetrica;
	
	/**
	 * llaves publicas de los nodos
	 */
	private ArrayList<PublicKey> nodosKey;
	
	/**
	 * Nodos out
	 */
	private ArrayList<ObjectOutputStream> nodosEscritura;
	
	/**
	 * Nodos in
	 */
	private ArrayList<ObjectInputStream> nodosLectura;
	
	/**
	 * Lista con las transacciones
	 */
	private ArrayList<Transaccion> transacciones;
	
	/**
	 * BlockChain
	 */
	private ArrayList<Bloque> blockChain;
	
	/**
	 * Construye un cliente
	 * 
	 * @param hostServidor el host del servidor a conectar
	 * @param puertoServidor puerto del servidor a conectar
	 * @throws Exception 
	 */
    public NodoMaster(int puertoServidor) throws Exception {
    	super(puertoServidor); 
    	id = 1;
		KeyPair claves = Utilities.generarClavesRSA();
		clavePrivada = claves.getPrivate();
		clavePublica = claves.getPublic();
		clavesSimetrica = new ArrayList<SecretKey>();
		nodosEscritura = new ArrayList<ObjectOutputStream>();
		nodosLectura = new ArrayList<ObjectInputStream>();
		nodosKey = new ArrayList<PublicKey>();
		transacciones = new ArrayList<Transaccion>();
		blockChain = new ArrayList<Bloque>();
    }  

    /**
     * Metodo para iniciar el cliente, intercambiara claves con los nodos
     * e iniciara la comunicacion con ellos
     * 
     * @param escaner el escaner a usar
     * @throws Exception si algun cifrado falla
     */
	public void startClient(Scanner escaner) throws Exception {	
		ArrayList<Boolean> respuestas = new ArrayList<Boolean>();
		Semaphore semaforoRecibir = new Semaphore(1); 
		new ThreadEscribir(id, escaner, clavesSimetrica, respuestas, nodosEscritura, transacciones, blockChain).start();  
		ArrayList<String[]> ipsPuertos = new ArrayList<>();
		while(true) {
			//Esperando conexión
    		System.out.println("Master esta esperando...");	        		
    		//Accept comienza el socket y espera una conexión desde un cliente
    		Socket s = this.ss.accept();    		    	
    		nodosEscritura.add(new ObjectOutputStream(s.getOutputStream()));
    		nodosLectura.add(new ObjectInputStream(s.getInputStream()));  
    		clavesSimetrica.add(Utilities.generarClaveAES());
    		nodosKey.add((PublicKey)nodosLectura.getLast().readObject());
    		enviar(clavePublica); // Enviar publica
    		enviar(Utilities.cifrarClaveAES(nodosKey.getLast(), clavesSimetrica.getLast())); //Cifrado
    		enviar(Utilities.firmarClaveAES(clavePrivada, clavesSimetrica.getLast().getEncoded())); // Firma
    		enviar(ipsPuertos);
    		ipsPuertos.add((String[])nodosLectura.getLast().readObject());   
    		enviar(transacciones);
    		enviar(blockChain);
    		new ThreadLeer(id, nodosLectura.getLast(), nodosEscritura.getLast(), clavesSimetrica.getLast(),
    				respuestas, semaforoRecibir, transacciones, blockChain).start();
		}		
    }
	
	/**
	 * Metodo que cifra un ArrayList
	 * 
	 * @param texto mensaje a cifrar
	 * @return el mensaje cifrado
	 * @throws Exception por si el cifrado falla
	 */
	private <T> byte[] cifrar(ArrayList<T> texto) throws Exception {
		ByteArrayOutputStream bs= new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream (bs);
		os.writeObject(texto);
		os.close();		
		return Utilities.cifrarAES(clavesSimetrica.getLast(), bs.toByteArray());
	}
	
	/**
	 * Metodo que envia un ArrayList cifrado
	 * 
	 * @param text el ArrayList a enviar
	 * @throws IOException si las tuberias fallan
	 * @throws Exception si el cifrado o las tuberias fallan
	 */
	private <T> void enviar(ArrayList<T> text) throws Exception {				
		nodosEscritura.getLast().writeObject(cifrar(text));
		nodosEscritura.getLast().flush();
	}
	
	/**
	 * Metodo que envia un objeto cifrado
	 * 
	 * @param <T> el objeto a enviar
	 * @throws IOException si las tuberias fallan	 
	 */
	private <T> void enviar(T object) throws IOException {
		nodosEscritura.getLast().writeObject(object);
		nodosEscritura.getLast().flush();		
	}
}
