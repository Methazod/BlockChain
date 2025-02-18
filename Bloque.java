package practica7BlockChain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Clase que crea un bloque
 * 
 * @author Jorge Manzano Anchelergues y Jaime Usero Aranda
 */

public class Bloque implements Serializable{
	
	/**
	 * ID serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Guarda el hash del anterior bloque
	 */
	private String hashAnteriorBloque;
	
	/**
	 * Guarda cuando se genero el bloque
	 */
	private String fechaCreacion;
	
	/**
	 * Valor numerico utilizado para hallar un hash valido
	 */
	private int nonce;
	
	/**
	 * Lista que guarda las transacciones
	 */
	private ArrayList<Transaccion> transacciones;
		
	/**
	 * Construye un bloque
	 * 
	 * @param hash el hash del bloque anterior
	 * @param fecha la fecha de creacion
	 * @param nonce el nonce utilizado
	 * @param tr lista de transacciones
	 */
	public Bloque(String hash, String fecha, int nonce, ArrayList<Transaccion> tr) {
		this.hashAnteriorBloque = hash;
		this.fechaCreacion = fecha;
		this.nonce = nonce;
		transacciones = new ArrayList<Transaccion>();
		transacciones.addAll(tr);
	}
	
	/**
	 * Construye un bloque
	 * 
	 * @param hash el hash del bloque anterior
	 * @param fecha la fecha de creacion
	 * @param nonce el nonce utilizado
	 */
	public Bloque(String hash, String fecha, int nonce) {
		this.hashAnteriorBloque = hash;
		this.fechaCreacion = fecha;
		this.nonce = nonce;
		transacciones = new ArrayList<Transaccion>();
	}	
	
	/**
	 * Getter del hash
	 * 
	 * @return el hash
	 */
	public String getHashAnteriorBloque() { return hashAnteriorBloque; }

	/**
	 * Getter de la fecha de creacion
	 * 
	 * @return la fecha de creacion
	 */
	public String getFechaCreacion() { return fechaCreacion; }

	/**
	 * Getter del nonce
	 * 
	 * @return el nonce
	 */
	public int getNonce() { return nonce; }
	
	/**
	 * Getter de las transacciones
	 * 
	 * @return las transacciones
	 */
	public ArrayList<Transaccion> getTransacciones() { return transacciones; }

	/**
	 * Añade una transaccion
	 * 
	 * @param tr
	 */
	public void addTransaccion(Transaccion tr) { transacciones.add(tr); }
	
	/**
	 * Devuelve el tamaño de la lista
	 * 
	 * @return
	 */
	public int transaccionesSize() { return this.transacciones.size(); }
	
	/**
	 * Devuelve una transaccion
	 * 
	 * @param index la posicion de la transaccion a obtener
	 */
	public void getTransaccion(int index) { this.transacciones.get(index); }
	
	/**
	 * Elimina una transaccion
	 * 
	 * @param index la posicion de la transaccion a eliminar
	 */
	public void removeTransaccion(int index) { this.transacciones.remove(index); }
	
	/**
	 * Elimina una transaccion
	 * 
	 * @param tr la transaccion a eliminar
	 */
	public void removeTransaccion(Transaccion tr) { this.transacciones.remove(tr); }
	
	/**
	 * Metodo que transforma un bloque a bytes[]
	 * 
	 * @return el bloque en bytes[]
	 * @throws IOException por si el stream falla
	 */
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream bs= new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream (bs);
		os.writeObject(this);  // this es de tipo DatoUdp
		os.close();
		return bs.toByteArray(); // devuelve byte[]
	}
	
	/**
	 * Metodo que transforma un byte[] a bloque
	 * 
	 * @param bytes los bytes[] a transformar
	 * @return el bloque obtenido
	 * @throws IOException si el stream falla
	 * @throws ClassNotFoundException si los bytes[] no son un bloque
	 */
	public Bloque toBloque(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bs= new ByteArrayInputStream(bytes); // bytes es el byte[]
		ObjectInputStream is = new ObjectInputStream(bs);
		Bloque b = (Bloque)is.readObject();
		is.close();
		return b;
	}
	
	/**
	 * Metodo que sobreescribe la manera en la que se muestra un bloque
	 */
	@Override
	public String toString() {		
		StringBuilder transaccion = new StringBuilder();
	    
	    for (int i = 0; i < transacciones.size(); i++) {
	        transaccion.append("\t\t") // Se encarga de añadir la tabulación extra
	                   .append(transacciones.get(i).toString().replace("\n", "\n\t\t")); // Indentación correcta
	        if (i < transacciones.size() - 1) {
	            transaccion.append(",\n");
	        }
	    }
	    
	    return "{\n" 
	            + "\tHash Anterior: " + (hashAnteriorBloque.isBlank() ? "No hay" : hashAnteriorBloque) + ",\n"
	            + "\tFecha de creacion: " + fechaCreacion + ",\n"
	            + "\tNonce: " + nonce + ",\n"
	            + "\tTransacciones: " + (transacciones.isEmpty() ? "No hay" : "{\n" + transaccion + "\n\t}") + "\n"
	            + "}";
	}
	
	/**
	 * Metodo que compara un objeto con esta instancia
	 */
	@Override
	public boolean equals(Object obj) {		
		if(obj == null || getClass() != obj.getClass()) return false;
		Bloque bl = (Bloque) obj;		
		return bl.getHashAnteriorBloque().equals(this.getHashAnteriorBloque())  
			   && bl.getTransacciones().equals(this.getTransacciones())	
			   && bl.getFechaCreacion().equals(this.getFechaCreacion()) 			   			   
			   && bl.getNonce() == this.getNonce();
	}
	
	/**
	 * Metodo que devuelve un hash para una tabla hash
	 */
	@Override
	public int hashCode() {		
		return Objects.hash(getHashAnteriorBloque(), getTransacciones(), getFechaCreacion(), getNonce());
	}
}