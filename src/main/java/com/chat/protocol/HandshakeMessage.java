package com.chat.protocol;

/**
 * Mensaje del protocolo de handshake ECDH.
 *
 * Responsabilidades:
 * - Encapsular la clave pública ECDH
 * - Serialización/deserialización
 *
 * Formato binario:
 *   [4 bytes big-endian length] [clave_pública_x509]
 *
 * Nota: La clave pública está en formato X.509 SubjectPublicKeyInfo.
 */
public class HandshakeMessage {

    private byte[] publicKey;

    /**
     * Crea un mensaje de handshake con clave pública.
     *
     * @param publicKey clave pública ECDH en formato X.509
     */
    public HandshakeMessage(byte[] publicKey) {
        if (publicKey == null) {
            throw new IllegalArgumentException("Clave pública no puede ser nula");
        }
        this.publicKey = publicKey;
    }

    /**
     * Retorna la clave pública del mensaje.
     */
    public byte[] getPublicKey() {
        return publicKey;
    }

    /**
     * Serializa el handshake message a bytes.
     *
     * Formato: [4 bytes big-endian length] [clave_pública]
     */
    public byte[] toBytes() {
        byte[] result = new byte[4 + publicKey.length];

        // Bytes 0-3: length big-endian
        int length = publicKey.length;
        result[0] = (byte) ((length >> 24) & 0xFF);
        result[1] = (byte) ((length >> 16) & 0xFF);
        result[2] = (byte) ((length >> 8) & 0xFF);
        result[3] = (byte) (length & 0xFF);

        // Bytes 4+: clave pública
        System.arraycopy(publicKey, 0, result, 4, publicKey.length);

        return result;
    }

    /**
     * Deserializa un handshake message desde bytes.
     *
     * @param data bytes del mensaje
     * @return instancia de HandshakeMessage, null si el formato es inválido
     */
    public static HandshakeMessage fromBytes(byte[] data) {
        if (data == null || data.length < 4) {
            System.err.println("Error: Datos de handshake inválidos (tamaño < 4)");
            return null;
        }

        try {
            // Bytes 0-3: length big-endian
            int length = ((data[0] & 0xFF) << 24)
                       | ((data[1] & 0xFF) << 16)
                       | ((data[2] & 0xFF) << 8)
                       | (data[3] & 0xFF);

            // Validar que hay suficientes bytes
            if (data.length < 4 + length) {
                System.err.println("Error: Handshake truncado (esperado " + (4 + length) + " bytes, recibido " + data.length + ")");
                return null;
            }

            // Bytes 4+: clave pública
            byte[] publicKey = new byte[length];
            System.arraycopy(data, 4, publicKey, 0, length);

            return new HandshakeMessage(publicKey);
        } catch (Exception e) {
            System.err.println("Error deserializando handshake: " + e.getMessage());
            return null;
        }
    }
}
