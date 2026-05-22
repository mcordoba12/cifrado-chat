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
        // TODO: Implementar
    }

    /**
     * Retorna la clave pública del mensaje.
     */
    public byte[] getPublicKey() {
        // TODO: Implementar
        return null;
    }

    /**
     * Serializa el handshake message a bytes.
     *
     * Formato: [4 bytes big-endian length] [clave_pública]
     */
    public byte[] toBytes() {
        // TODO: Implementar
        return null;
    }

    /**
     * Deserializa un handshake message desde bytes.
     *
     * @param data bytes del mensaje
     * @return instancia de HandshakeMessage, null si el formato es inválido
     */
    public static HandshakeMessage fromBytes(byte[] data) {
        // TODO: Implementar
        return null;
    }
}
