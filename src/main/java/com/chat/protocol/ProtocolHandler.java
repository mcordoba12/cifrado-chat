package com.chat.protocol;

/**
 * Manejador del protocolo de chat.
 *
 * Responsabilidades:
 * - Serialización/deserialización de mensajes de aplicación
 * - Serialización/deserialización de mensajes de handshake
 *
 * Nota: Esta clase es principalmente un wrapper sobre Message y HandshakeMessage.
 *       Podría extenderse para soportar otros tipos de mensajes.
 */
public class ProtocolHandler {

    /**
     * Serializa un Message de chat para envío.
     *
     * @param message mensaje a serializar
     * @return bytes del mensaje
     */
    public byte[] serializeMessage(Message message) {
        // TODO: Implementar
        return null;
    }

    /**
     * Deserializa un Message de chat recibido.
     *
     * @param data bytes recibidos
     * @return Message deserializado, null si hay error
     */
    public Message deserializeMessage(byte[] data) {
        // TODO: Implementar
        return null;
    }

    /**
     * Serializa un HandshakeMessage para envío.
     *
     * @param handshake mensaje de handshake a serializar
     * @return bytes del mensaje
     */
    public byte[] serializeHandshake(HandshakeMessage handshake) {
        // TODO: Implementar
        return null;
    }

    /**
     * Deserializa un HandshakeMessage recibido.
     *
     * @param data bytes recibidos
     * @return HandshakeMessage deserializado, null si hay error
     */
    public HandshakeMessage deserializeHandshake(byte[] data) {
        // TODO: Implementar
        return null;
    }
}
