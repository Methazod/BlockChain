package practica7BlockChain;

/**
 * Clase que crea protocolos de conexion
 *
 * @author Jorge Manzano Anchelergues y Jaime usero Aranda
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Conexion {
	
	/**
	 * Puerto para la conexión
	 */
    protected int puertoServidor, puertoCliente;
    
    /**
     * Host para la conexión
     */
    protected String hostServidor, hostCliente;	
    
    /**
     * Socket del servidor
     */
    protected ServerSocket ss; 
    
    /**
     * Socket del cliente
     */
    protected Socket cs;        
        
    /**
     * Constructor del Servidor que crea un ServerSocket
     * 
     * @param puerto entero que especifica el puerto
     * @throws IOException
     */
    public Conexion(int puertoServidor) throws IOException {
    	this.puertoServidor = puertoServidor;    			
    	//Se crea el socket para el servidor en un puerto
        ss = new ServerSocket(this.puertoServidor);        
    }
    
    /**
     * Constructor del Cliente que crea un Socket
     * 
     * @param host string que especifica la ip
     * @param puerto entero que especifica el puerto
     * @throws IOException
     */
    public Conexion(String hostServidor, int puertoServidor) throws IOException {
    	this.hostServidor = hostServidor;
    	this.puertoServidor = puertoServidor;
    	//Socket para el cliente en un host en un puerto
        cs = new Socket(this.hostServidor, this.puertoServidor);
    }   
    
    /**
     * Constructor del Cliente-Servidor que crea un Socket y un ServerSocket
     * 
     * @param host string que especifica la ip
     * @param puerto entero que especifica el puerto
     * @throws IOException
     */
    public Conexion(String hostServidor, int puertoServidor, String hostCliente, int puertoCliente) throws IOException {
    	this.hostServidor = hostServidor;
    	this.puertoServidor = puertoServidor;
    	this.hostCliente = hostCliente;
    	this.puertoCliente = puertoCliente; 
    	//Socket para el cliente en un host en un puerto
        cs = new Socket(this.hostServidor, this.puertoServidor);
        ss = new ServerSocket(this.puertoCliente);
    }
}