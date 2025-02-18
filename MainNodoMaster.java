package practica7BlockChain;

/**
 * Clase que crea un nodo master y lo inicia.
 * 
 * @author Jorge Manzano Ancglergues y Jaime Usero Aranda
 */

import java.util.Scanner;

public class MainNodoMaster {		
  public static void main(String[] args) throws Exception {
	  Scanner escaner = new Scanner(System.in);	
	  NodoMaster cli = new NodoMaster(2001); //Se crea el servidor	  
	  
      System.out.println("Iniciando Servidor\n");
      cli.startClient(escaner); //Se inicia el servidor
  }    
}