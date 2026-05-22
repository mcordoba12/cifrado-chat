package com.chat.crypto;

import java.security.KeyPair;

/**
 * Gestor centralizado de operaciones criptográficas.
 *
 * Responsabilidades:
 * - Generar pares de claves EC (P-256)
 * - Calcular SHA-256
 * - Inicializar Cipher para AES-GCM
 * - Manejar derivación de claves
 *
 * Nota: Esta clase es thread-safe en la medida que javax.crypto lo permite.
 */
public class CryptographyManager {

    /**
     * Genera un par de claves ECDH usando la curva P-256 (secp256r1).
     *
     * @return par de claves (pública + privada)
     */
    public KeyPair generateECKeyPair() {
        // TODO: Implementar
        return null;
    }

    /**
     * Calcula el hash SHA-256 de un array de bytes.
     *
     * @param data datos a hashear
     * @return hash SHA-256 (32 bytes)
     */
    public byte[] sha256(byte[] data) {
        // TODO: Implementar
        return null;
    }

    /**
     * Deriva una clave AES-256 a partir del shared secret usando KDF basado en SHA-256.
     *
     * Fórmula:
     *   AES_KEY = SHA-256(shared_secret + "CIPHER_KEY_DERIVATION")
     *
     * @param sharedSecret shared secret del protocolo ECDH (256 bits)
     * @return clave AES-256 (32 bytes)
     */
    public byte[] deriveAESKey(byte[] sharedSecret) {
        // TODO: Implementar
        return null;
    }
}
