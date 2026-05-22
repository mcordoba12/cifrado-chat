package com.chat.crypto;

/**
 * Encriptador/desencriptador de mensajes usando AES-256-GCM.
 *
 * Responsabilidades:
 * - Encriptar mensajes con IV aleatorio por mensaje
 * - Desencriptar mensajes verificando autenticación
 * - Manejo de formato: [IV 12B] [CIPHER_TEXT] [TAG 16B]
 *
 * Notas:
 * - IV debe ser aleatorio y único por cada mensaje
 * - El tag de autenticación es de 128 bits (16 bytes)
 * - La clave AES es de 256 bits (32 bytes)
 */
public class MessageCryptor {

    private byte[] aesKey;

    /**
     * Inicializa el criptador con la clave AES-256 derivada.
     *
     * @param aesKey clave AES-256 (32 bytes)
     */
    public MessageCryptor(byte[] aesKey) {
        // TODO: Implementar
    }

    /**
     * Encripta un mensaje usando AES-256-GCM.
     *
     * Formato de salida:
     *   [IV 12 bytes] [CIPHER_TEXT variable] [TAG 16 bytes]
     *
     * @param plaintext mensaje a encriptar
     * @return mensaje encriptado con IV y tag
     */
    public byte[] encrypt(String plaintext) {
        // TODO: Implementar
        return null;
    }

    /**
     * Desencripta un mensaje cifrado con AES-256-GCM.
     *
     * Espera formato:
     *   [IV 12 bytes] [CIPHER_TEXT variable] [TAG 16 bytes]
     *
     * @param encryptedData mensaje encriptado con IV y tag
     * @return texto plano, null si autenticación falla
     */
    public String decrypt(byte[] encryptedData) {
        // TODO: Implementar
        return null;
    }

    /**
     * Valida que los datos encriptados tengan el formato correcto.
     *
     * @param encryptedData datos a validar
     * @return true si el tamaño es válido (mínimo 12 + 16 bytes)
     */
    private boolean validateEncryptedDataFormat(byte[] encryptedData) {
        // TODO: Implementar
        return false;
    }
}
