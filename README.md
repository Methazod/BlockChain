# Proyecto de Blockchain en Java

Este proyecto implementa una cadena de bloques (blockchain) simple utilizando el lenguaje de programación Java. Es una herramienta educativa diseñada para demostrar los conceptos básicos de la tecnología blockchain.

## Características

- **Bloques (`Bloque.java`)**: Representa un bloque en la cadena, que contiene transacciones y un hash que asegura la integridad del bloque.
- **Transacciones (`Transaccion.java`)**: Define las transacciones que se almacenan en los bloques.
- **Nodos (`Nodo.java` y `NodoMaster.java`)**: Implementa los nodos de la red blockchain, incluyendo el nodo maestro que coordina la red.
- **Conexiones (`Conexion.java`)**: Maneja la comunicación entre los nodos de la red.
- **Hilos (`ThreadEscribir.java` y `ThreadLeer.java`)**: Gestiona las operaciones de lectura y escritura en la red de nodos.
- **Utilidades (`Utilities.java`)**: Proporciona funciones auxiliares utilizadas en todo el proyecto.

## Requisitos

- Java Development Kit (JDK) 8 o superior.

## Instalación

1. Clona este repositorio en tu máquina local:
   git clone https://github.com/Methazod/BlockChain.git

## Uso
1. Iniciar el nodo maestro.
2. Iniciar uno o más nodos secundarios.
3. Los nodos comenzarán a comunicarse entre sí, permitiendo la creación y propagación de bloques en la cadena.
4. Crearas una transaccion con /enviar y crearas un bloque /minar.

## Contribuciones
Las contribuciones son bienvenidas. Si deseas mejorar este proyecto, por favor sigue estos pasos:

1. Haz un fork del repositorio.
2. Crea una nueva rama para tu función o corrección:
  git checkout -b nombre-de-tu-rama
3. Realiza tus cambios y haz commits descriptivos.
4. Envía un pull request detallando tus modificaciones.
