package practica7BlockChain;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.HexFormat;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/**
 * Clase con métodos estáticos para cifrar, descifrar y hashear textos.
 * 
 * Implementa cifrado simétrico (AES) para datos grandes
 * y asimétrico (RSA) para intercambio de claves.
 * 
 * @author Jorge Manzano Anchelergues y Jaime Usero Aranda
 */

public class Utilities {
	
	/**
	 * Algoritmo para las llaves asimetricas
	 */
	private static final String ALGORITMO_CIFRADO_RSA = "RSA";
	
	/**
	 * Algoritmo para la llave simetrica
	 */
	private static final String ALGORITMO_CIFRADO_AES = "AES";
	
	/**
	 * Algoritmo para hashear
	 */
	private static final String ALGORITMO_HASH = "SHA-256";
	
	/**
	 * Tamaño de las llaves asimetricas
	 */
	private static final int RSA_KEY_SIZE = 4096;
	
	/**
	 * Tamaño de la llave simetrica
	 */
	private static final int AES_KEY_SIZE = 256;

	// ===========================
	//  GENERACIÓN DE CLAVES
	// ===========================

	/**
	 * Método que genera un par de claves RSA.
	 * 
	 * @return el par de claves
	 * @throws Exception
	 */
	public static KeyPair generarClavesRSA() throws Exception {        		
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITMO_CIFRADO_RSA);
        keyPairGenerator.initialize(RSA_KEY_SIZE);
        return keyPairGenerator.generateKeyPair();        
	}

	/**
	 * Genera una clave secreta AES de 256 bits.
	 * 
	 * @return clave AES
	 * @throws Exception
	 */
	public static SecretKey generarClaveAES() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITMO_CIFRADO_AES);
        keyGenerator.init(AES_KEY_SIZE);
        return keyGenerator.generateKey();
    }

	// ===========================
	//  CIFRADO Y DESCIFRADO AES
	// ===========================

	/**
	 * Cifra un mensaje con AES.
	 * 
	 * @param claveAES Clave AES de cifrado
	 * @param texto Texto a cifrar
	 * @return Texto cifrado
	 * @throws Exception
	 */
	public static byte[] cifrarAES(SecretKey claveAES, byte[] texto) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITMO_CIFRADO_AES);
        cipher.init(Cipher.ENCRYPT_MODE, claveAES);
        byte[] cifrado = cipher.doFinal(texto);
        return cifrado;
    }

	/**
	 * Descifra un mensaje con AES.
	 * 
	 * @param claveAES Clave AES de descifrado
	 * @param textoCifrado Texto cifrado
	 * @return Texto descifrado
	 * @throws Exception
	 */
	public static byte[] descifrarAES(SecretKey claveAES, byte[] textoCifrado) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITMO_CIFRADO_AES);
        cipher.init(Cipher.DECRYPT_MODE, claveAES);
        byte[] descifrado = cipher.doFinal(textoCifrado);
        return descifrado;
    }

	// ===========================
	//  CIFRADO Y DESCIFRADO RSA
	// ===========================

	/**
	 * Cifra la clave AES con la clave pública RSA.
	 * 
	 * @param publicKey Clave pública RSA
	 * @param claveAES Clave AES a cifrar
	 * @return Clave AES cifrada
	 * @throws Exception
	 */
	public static byte[] cifrarClaveAES(PublicKey publicKey, SecretKey claveAES) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITMO_CIFRADO_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] claveCifrada = cipher.doFinal(claveAES.getEncoded());
        return Base64.getEncoder().encode(claveCifrada);
    }		

	/**
	 * Descifra la clave AES con la clave privada RSA.
	 * 
	 * @param privateKey Clave privada RSA
	 * @param claveCifrada Clave AES
	 * @return Clave AES descifrada
	 * @throws Exception
	 */
	public static SecretKey descifrarClaveAES(PrivateKey privateKey, byte[] claveCifrada) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITMO_CIFRADO_RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] claveDescifrada = cipher.doFinal(Base64.getDecoder().decode(claveCifrada));
        return new SecretKeySpec(claveDescifrada, ALGORITMO_CIFRADO_AES);
    }
	
	/**
	 * Firma la llave AES
	 * 
	 * @param privateKey Clave privada RSA
	 * @param claveAES Clave AES en bytes[]
	 * @return la firma en bytes[]
	 * @throws Exception
	 */
	public static byte[] firmarClaveAES(PrivateKey privateKey, byte[] claveAES) throws Exception {
	    Signature signature = Signature.getInstance("SHA256withRSA");
	    signature.initSign(privateKey);
	    signature.update(claveAES);
	    return signature.sign();
	}

	/**
	 * Verifica la llave AES
	 * 
	 * @param publicKey Clave publica RSA
	 * @param claveAES clave AES en bytes[]
	 * @param firma la firma en bytes[]
	 * @return [true] si es la misma llave,
	 * [false] si no lo son
	 * @throws Exception
	 */
	public static boolean verificarFirmaClaveAES(PublicKey publicKey, byte[] claveAES, byte[] firma) throws Exception {
	    Signature signature = Signature.getInstance("SHA256withRSA");
	    signature.initVerify(publicKey);
	    signature.update(claveAES);
	    return signature.verify(firma); // Devuelve true si la firma es válida
	}
	
	// ===========================
	//  HASHING (SHA-256)
	// ===========================

	/**
	 * Método que hashea un contenido.
	 * 
	 * @param contenido Texto a hashear
	 * @return Hash en formato hexadecimal
	 * @throws Exception
	 */
	public static String hashear(String contenido) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance(ALGORITMO_HASH);
        byte[] hash = messageDigest.digest(contenido.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }

	/**
	 * Método que hashea un array de bytes.
	 * 
	 * @param contenido Bytes a hashear
	 * @return Hash en formato hexadecimal
	 * @throws Exception
	 */
	public static String hashear(byte[] contenido) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance(ALGORITMO_HASH);
        byte[] hash = messageDigest.digest(contenido);
        return HexFormat.of().formatHex(hash);
    }
}
