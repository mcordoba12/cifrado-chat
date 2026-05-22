package com.chat;

/**
 * Punto de entrada principal del programa.
 * Parsea argumentos de línea de comandos y crea instancia de servidor o cliente.
 *
 * Argumentos:
 *   --mode server/client (requerido)
 *   --port PUERTO (requerido para server, ignorado para client)
 *   --host HOSTNAME (requerido para client, ignorado para server)
 *
 * Ejemplo servidor:
 *   java -jar cifrado-chat.jar --mode server --port 5000
 *
 * Ejemplo cliente:
 *   java -jar cifrado-chat.jar --mode client --host 0.tcp.ngrok.io --port 12345
 */
public class Main {

    /**
     * Punto de entrada.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // TODO: Implementar
    }
}
