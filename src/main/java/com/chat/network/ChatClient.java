package com.chat.network;

/**
 * Cliente de chat.
 *
 * Responsabilidades:
 * - Conectar a servidor remoto (host + puerto)
 * - Orquestar protocolo ECDH
 * - Manejar loop de chat cifrado
 *
 * Flujo:
 * 1. new ChatClient(host, port)
 * 2. start() → establece conexión TCP e inicia KeyExchange
 * 3. Una vez completado handshake, inicia loop de lectura/escritura de mensajes cifrados
 */
public class ChatClient {

    private String host;
    private int port;
    private Connection connection;

    /**
     * Crea cliente que conectará a servidor remoto.
     *
     * @param host hostname o IP del servidor
     * @param port puerto TCP del servidor
     */
    public ChatClient(String host, int port) {
        // TODO: Implementar
    }

    /**
     * Inicia el cliente.
     *
     * Establece conexión TCP y bloquea hasta completar handshake ECDH.
     *
     * @throws Exception si hay error en la conexión o handshake
     */
    public void start() throws Exception {
        // TODO: Implementar
    }

    /**
     * Inicia el loop de lectura de mensajes desde servidor y escritura desde usuario.
     *
     * Bloquea indefinidamente hasta que usuario escriba /salir o se desconecte servidor.
     *
     * @throws Exception si hay error durante el chat
     */
    public void chatLoop() throws Exception {
        // TODO: Implementar
    }

    /**
     * Cierra el cliente y recursos asociados.
     *
     * @throws Exception si hay error al cerrar
     */
    public void shutdown() throws Exception {
        // TODO: Implementar
    }
}
