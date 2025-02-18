package practica7BlockChain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
 * Clase que crea un nodo
 * 
 * @author Jorge Manzano Anchelergues y Jaime Usero Aranda
 */

public class Nodo extends Conexion {	
	
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
	 * Nodos out
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
    public Nodo(String localhost, int puertoServidor, String hostCliente, int puertoCliente) throws Exception {
    	super(localhost, puertoServidor, hostCliente, puertoCliente);    	
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
     * Metodo para iniciar el cliente, intercambiara claves con el nodo maestro
     * y nodos ya conectados a la red e iniciara la comunicacion con ellos
     * 
     * @param escaner el escaner a usar
     * @throws Exception si algun cifrado falla
     */	
	public void startClient(Scanner escaner) throws Exception {	
		ArrayList<Boolean> respuestas = new ArrayList<Boolean>();
		Semaphore semaforoRecibir = new Semaphore(1); 
		// Conectar con el master y obtener y enviar los datos	
		ArrayList<String[]> ipsPuertos = new ArrayList<String[]>();
		conectarMaster(ipsPuertos);
		this.id = ipsPuertos.size() + 2;
		new ThreadEscribir(id, escaner, clavesSimetrica, respuestas, nodosEscritura, transacciones, blockChain).start();
		new ThreadLeer(id, nodosLectura.getLast(), nodosEscritura.getLast(), clavesSimetrica.getLast(), 
				respuestas, semaforoRecibir, transacciones, blockChain).start();
		
		// Conectar con los puertos enviados por el master
		if(!ipsPuertos.isEmpty()) {
			for(String[] ipPuerto : ipsPuertos) {
				conectarCliente(new Socket(ipPuerto[0], Integer.parseInt(ipPuerto[1])));
				new ThreadLeer(id, nodosLectura.getLast(), nodosEscritura.getLast(), clavesSimetrica.getLast(), 
						respuestas, semaforoRecibir, transacciones, blockChain).start(); 
			}			
		}		
				
		// Esperar nuevos nodos
		while(true) {
			//Esperando conexión
    		System.out.println("Nodo esta esperando...");	        		
    		//Accept comienza el socket y espera una conexión desde un cliente 
    		conectarServer(this.ss.accept());    		
    		new ThreadLeer(id, nodosLectura.getLast(), nodosEscritura.getLast(), clavesSimetrica.getLast(), 
    				respuestas, semaforoRecibir, transacciones, blockChain).start();
		}
    }
	
	/**
	 * Metodo que conecta este nodo con otro nodo
	 * 
	 * @param s socket del nodo
	 * @throws Exception si el cifrado falla
	 */
	private void conectarCliente(Socket s) throws Exception {		
		nodosEscritura.add(new ObjectOutputStream(s.getOutputStream()));
		nodosLectura.add(new ObjectInputStream(s.getInputStream()));
		enviar(clavePublica);				
		nodosKey.add((PublicKey)nodosLectura.getLast().readObject());		
		// RECIBIR AES CIFRADA
		SecretKey descifrado = Utilities.descifrarClaveAES(clavePrivada, (byte[])nodosLectura.getLast().readObject());
		//RECIBIR AES FIRMADA		
		if(Utilities.verificarFirmaClaveAES(nodosKey.getLast(), descifrado.getEncoded(), (byte[])nodosLectura.getLast().readObject())) 
			clavesSimetrica.add(descifrado);
	}
	
	/**
	 * Metodo que conecta un nodo con este nodo
	 * 
	 * @param s socket del nodo
	 * @throws Exception si el cifrado falla
	 */
	private void conectarServer(Socket s) throws Exception {		
		nodosEscritura.add(new ObjectOutputStream(s.getOutputStream()));
		nodosLectura.add(new ObjectInputStream(s.getInputStream()));
		clavesSimetrica.add(Utilities.generarClaveAES());
		enviar(clavePublica);		
		nodosKey.add((PublicKey)nodosLectura.getLast().readObject());		
		enviar(Utilities.cifrarClaveAES(nodosKey.getLast(), clavesSimetrica.getLast())); //Cifrado
		enviar(Utilities.firmarClaveAES(clavePrivada, clavesSimetrica.getLast().getEncoded())); // Firma
	}
	
	/**
	 * Metodo que conecta este nodo con el nodo maestro
	 * 
	 * @param s socket del nodo
	 * @throws Exception si el cifrado falla
	 */
	private void conectarMaster(ArrayList<String[]> ipsPuertos) throws Exception {		
		conectarCliente(cs);
		ipsPuertos.addAll(recibir());
		enviar(new String[]{this.hostCliente, ""+this.puertoCliente});
		transacciones.addAll(recibir());
		blockChain.addAll(recibir());		
	}
	
	/**
	 * Metodo que envia un objeto cifrado
	 * 
	 * @param <T> el objeto a enviar
	 * @throws IOException si las tuberias fallan	 
	 */
	private <T> void enviar(T objeto) throws IOException {
		nodosEscritura.getLast().writeObject(objeto);
		nodosEscritura.getLast().flush();
	}		
	
	/**
	 * Metodo que descifra un ArrayList
	 * 
	 * @param texto ArrayList a descifrar
	 * @return el ArrayLiist descifrado
	 * @throws Exception por si el descifrado falla
	 */
	@SuppressWarnings("unchecked")
	private <T> ArrayList<T> descifrar(byte[] encriptado) throws Exception {
		ByteArrayInputStream bs= new ByteArrayInputStream(Utilities.descifrarAES(clavesSimetrica.getLast(), encriptado));
		ObjectInputStream is = new ObjectInputStream(bs);
		ArrayList<T> t = (ArrayList<T>)is.readObject();
		is.close();
		return t;		
	}		
	
	/**
	 * Metodo que recibe un mensaje cifrado lo descifra
	 * 
	 * @return el mensaje descifrado
	 * @throws ClassNotFoundException si falla al castear la clase
	 * @throws IOException si falla la tuberia
	 * @throws Exception si falla el cifrado
	 */
	private <T> ArrayList<T> recibir() throws ClassNotFoundException, IOException, Exception {		
		return descifrar((byte[])nodosLectura.getLast().readObject());
	}
}
