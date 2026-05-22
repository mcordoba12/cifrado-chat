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
        this(TYPE_NORMAL, content);
    }

    /**
     * Crea un mensaje con tipo específico.
     *
     * @param type tipo de mensaje
     * @param content contenido
     */
    public Message(byte type, String content) {
        this.type = type;
        this.content = content;
    }

    /**
     * Retorna el tipo de mensaje.
     */
    public byte getType() {
        return type;
    }

    /**
     * Retorna el contenido del mensaje.
     */
    public String getContent() {
        return content;
    }

    /**
     * Serializa el mensaje a bytes.
     *
     * Formato: [1 byte tipo] [4 bytes big-endian length] [text_utf8]
     */
    public byte[] toBytes() {
        try {
            byte[] contentBytes = content.getBytes("UTF-8");
            byte[] result = new byte[1 + 4 + contentBytes.length];

            // Byte 0: tipo
            result[0] = type;

            // Bytes 1-4: length big-endian
            int length = contentBytes.length;
            result[1] = (byte) ((length >> 24) & 0xFF);
            result[2] = (byte) ((length >> 16) & 0xFF);
            result[3] = (byte) ((length >> 8) & 0xFF);
            result[4] = (byte) (length & 0xFF);

            // Bytes 5+: contenido UTF-8
            System.arraycopy(contentBytes, 0, result, 5, contentBytes.length);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error serializando mensaje: " + e.getMessage(), e);
        }
    }

    /**
     * Deserializa un mensaje desde bytes.
     *
     * @param data bytes del mensaje
     * @return instancia de Message, null si el formato es inválido
     */
    public static Message fromBytes(byte[] data) {
        if (data == null || data.length < 5) {
            System.err.println("Error: Datos de mensaje inválidos (tamaño < 5)");
            return null;
        }

        try {
            // Byte 0: tipo
            byte type = data[0];

            // Bytes 1-4: length big-endian
            int length = ((data[1] & 0xFF) << 24)
                       | ((data[2] & 0xFF) << 16)
                       | ((data[3] & 0xFF) << 8)
                       | (data[4] & 0xFF);

            // Validar que hay suficientes bytes
            if (data.length < 5 + length) {
                System.err.println("Error: Mensaje truncado (esperado " + (5 + length) + " bytes, recibido " + data.length + ")");
                return null;
            }

            // Bytes 5+: contenido UTF-8
            String content = new String(data, 5, length, "UTF-8");

            return new Message(type, content);
        } catch (Exception e) {
            System.err.println("Error deserializando mensaje: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verifica si el mensaje es un comando de shutdown.
     */
    public boolean isShutdown() {
        return type == TYPE_SHUTDOWN;
    }
}
