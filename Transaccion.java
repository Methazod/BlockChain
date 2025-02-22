package practica7BlockChain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Clase que crea una transaccion.
 * 
 * @author Jorge Manzano Anchelergues y Jaime Usero Aranda.
 */

public class Transaccion implements Serializable{
	
	/**
	 * Id serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Hash criptográfico único que representa la transacción.
	 */
	private String id;
	
	/**
	 * Llave publica del emisor y receptor
	 */
	private int emisor, receptor;
	
	/**
	 * Monto transferido en la transaccion
	 */
	private int valor;
	
	private String fecha;
	
	/**
	 * Construye una transaccion
	 * 
	 * @param emisor el emisor de la transaccion
	 * @param receptor el receptor de la transaccion
	 * @param valor el dinero enviado
	 * @throws Exception si al hashear falla
	 */
	public Transaccion(int emisor, int receptor, int valor) throws Exception {
		this.emisor = emisor;
		this.receptor = receptor;
		this.valor = valor;
		this.fecha = LocalDateTime.now().toString();
		this.id = Utilities.hashear(this.getBytes());
	}
	
	/**
	 * Getter del id
	 * 
	 * @return el id
	 */
	public String getId() { return id; }

	/**
	 * Getter del emisor
	 * 
	 * @return el emisor
	 */
	public int getEmisor() { return emisor; }

	/**
	 * Getter del receptor
	 * 
	 * @return el receptor
	 */
	public int getReceptor() { return receptor;	}

	/**
	 * Getter del valor
	 * 
	 * @return el valor
	 */
	public int getValor() { return valor; }
	
	/**
	 * Metodo que transforma una transaccion a bytes[]
	 * 
	 * @return la transaccion en bytes[]
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
	 * Metodo que transforma un byte[] a transaccion
	 * 
	 * @param bytes los bytes[] a transformar
	 * @return la transaccion obtenida
	 * @throws IOException si el stream falla
	 * @throws ClassNotFoundException si los bytes[] no son una transaccion
	 */
	public Transaccion toTransaccion(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bs= new ByteArrayInputStream(bytes); // bytes es el byte[]
		ObjectInputStream is = new ObjectInputStream(bs);
		Transaccion t = (Transaccion)is.readObject();
		is.close();
		return t;
	}
	
	/**
	 * Metodo que sobreescribe la manera en la que se muestra una transaccion
	 */
	@Override
	public String toString() {
	    return "{\n" 
	            + "\tid: " + id + ",\n"
	            + "\temisor: " + emisor + ",\n"
	            + "\treceptor: " + receptor + ",\n"
	            + "\tmonto: " + valor + "\n"
	            + "\tfecha: " + fecha + "\n"
	            + "}";
	}
	
	/**
	 * Metodo que compara un objeto con esta instancia
	 */
	@Override
	public boolean equals(Object obj) {		
		if(obj == null || getClass() != obj.getClass()) return false;
		Transaccion tr = (Transaccion) obj;		
		return tr.getId().equals(this.getId())  
			   && tr.getEmisor() == this.getEmisor()	
			   && tr.getReceptor() == this.getReceptor() 
			   && tr.getValor() == this.getValor()
			   && tr.fecha.equals(this.fecha);					
	}
	
	/**
	 * Metodo que devuelve un hash para una tabla hash
	 */
	@Override
	public int hashCode() {		
		return Objects.hash(getId(), getEmisor(), getReceptor(), getValor(), fecha);
	}
}