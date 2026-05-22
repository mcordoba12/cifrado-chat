package com.chat.protocol;

/**
 * Mensaje del protocolo de chat.
 *
 * Responsabilidades:
 * - Representar un mensaje de usuario
 * - Serialización/deserialización
 *
 * Formato binario:
 *   [1 byte tipo] [4 bytes big-endian length] [text_utf8]
 *
 * Tipos:
 *   0x00 = mensaje normal
 *   0xFF = comando especial (/salir)
 */
public class Message {

    public static final byte TYPE_NORMAL = 0x00;
    public static final byte TYPE_SHUTDOWN = (byte) 0xFF;

    private byte type;
    private String content;

    /**
     * Crea un mensaje normal.
     *
     * @param content contenido del mensaje
     */
    public Message(String content) {
        // TODO: Implementar
    }

    /**
     * Crea un mensaje con tipo específico.
     *
     * @param type tipo de mensaje
     * @param content contenido
     */
    public Message(byte type, String content) {
        // TODO: Implementar
    }

    /**
     * Retorna el tipo de mensaje.
     */
    public byte getType() {
        // TODO: Implementar
        return 0;
    }

    /**
     * Retorna el contenido del mensaje.
     */
    public String getContent() {
        // TODO: Implementar
        return null;
    }

    /**
     * Serializa el mensaje a bytes.
     *
     * Formato: [1 byte tipo] [4 bytes big-endian length] [text_utf8]
     */
    public byte[] toBytes() {
        // TODO: Implementar
        return null;
    }

    /**
     * Deserializa un mensaje desde bytes.
     *
     * @param data bytes del mensaje
     * @return instancia de Message, null si el formato es inválido
     */
    public static Message fromBytes(byte[] data) {
        // TODO: Implementar
        return null;
    }

    /**
     * Verifica si el mensaje es un comando de shutdown.
     */
    public boolean isShutdown() {
        // TODO: Implementar
        return false;
    }
}
