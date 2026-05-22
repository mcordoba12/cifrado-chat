package com.chat.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.ECGenParameterSpec;
import java.security.InvalidAlgorithmParameterException;

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
     * @throws RuntimeException si hay error en la generación de claves
     */
    public KeyPair generateECKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
            keyGen.initialize(ecSpec);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Error generando par de claves ECDH: " + e.getMessage(), e);
        }
    }

    /**
     * Calcula el hash SHA-256 de un array de bytes.
     *
     * @param data datos a hashear
     * @return hash SHA-256 (32 bytes)
     * @throws RuntimeException si hay error en el cálculo del hash
     */
    public byte[] sha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculando SHA-256: " + e.getMessage(), e);
        }
    }

    /**
     * Deriva una clave AES-256 a partir del shared secret usando KDF basado en SHA-256.
     *
     * Fórmula:
     *   AES_KEY = SHA-256(shared_secret + "CIPHER_KEY_DERIVATION")
     *
     * @param sharedSecret shared secret del protocolo ECDH (256 bits)
     * @return clave AES-256 (32 bytes)
     * @throws RuntimeException si hay error en la derivación
     */
    public byte[] deriveAESKey(byte[] sharedSecret) {
        String derivationConstant = "CIPHER_KEY_DERIVATION";
        byte[] constantBytes = derivationConstant.getBytes();

        byte[] input = new byte[sharedSecret.length + constantBytes.length];
        System.arraycopy(sharedSecret, 0, input, 0, sharedSecret.length);
        System.arraycopy(constantBytes, 0, input, sharedSecret.length, constantBytes.length);

        return sha256(input);
    }
}
