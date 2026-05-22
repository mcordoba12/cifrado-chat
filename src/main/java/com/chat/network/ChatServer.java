package com.chat.network;

/**
 * Servidor de chat.
 *
 * Responsabilidades:
 * - Escuchar en puerto especificado
 * - Aceptar conexión única de cliente
 * - Orquestar protocolo ECDH
 * - Manejar loop de chat cifrado
 *
 * Flujo:
 * 1. new ChatServer(port)
 * 2. start() → bloquea en ServerSocket.accept()
 * 3. Una vez conectado, inicia KeyExchange
 * 4. Inicia loop de lectura/escritura de mensajes cifrados
 */
public class ChatServer {

    private int port;
    private Connection connection;

    /**
     * Crea servidor que escucha en el puerto especificado.
     *
     * @param port puerto TCP a escuchar
     */
    public ChatServer(int port) {
        // TODO: Implementar
    }

    /**
     * Inicia el servidor.
     *
     * Bloquea hasta que se establezca conexión y se complete el handshake.
     *
     * @throws Exception si hay error en la configuración o handshake
     */
    public void start() throws Exception {
        // TODO: Implementar
    }

    /**
     * Inicia el loop de lectura de mensajes desde cliente y escritura desde usuario.
     *
     * Bloquea indefinidamente hasta que usuario escriba /salir o se desconecte cliente.
     *
     * @throws Exception si hay error durante el chat
     */
    public void chatLoop() throws Exception {
        // TODO: Implementar
    }

    /**
     * Cierra el servidor y recursos asociados.
     *
     * @throws Exception si hay error al cerrar
     */
    public void shutdown() throws Exception {
        // TODO: Implementar
    }
}
