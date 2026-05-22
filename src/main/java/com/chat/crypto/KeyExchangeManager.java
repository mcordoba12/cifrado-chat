package com.chat.crypto;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.KeyAgreement;

/**
 * Gestor del protocolo de intercambio de claves ECDH P-256.
 *
 * Responsabilidades:
 * - Generar claves locales
 * - Calcular shared secret a partir de clave pública remota
 * - Serializar/deserializar claves públicas en formato X.509
 *
 * Protocolo:
 * 1. Servidor genera par y envía pública (X.509)
 * 2. Cliente genera par, recibe pública del servidor y envía la propia
 * 3. Ambos calculan shared_secret = ECDH(privada_local, pública_remota)
 */
public class KeyExchangeManager {

    private KeyPair localKeyPair;
    private byte[] sharedSecret;

    /**
     * Genera el par de claves ECDH local.
     *
     * @param cryptoManager gestor criptográfico
     */
    public void generateLocalKeyPair(CryptographyManager cryptoManager) {
        this.localKeyPair = cryptoManager.generateECKeyPair();
    }

    /**
     * Retorna la clave pública local en formato X.509 (bytes).
     *
     * @return clave pública X.509 encoded
     */
    public byte[] getPublicKeyEncoded() {
        if (localKeyPair == null) {
            throw new IllegalStateException("Par de claves no ha sido generado");
        }
        return localKeyPair.getPublic().getEncoded();
    }

    /**
     * Calcula el shared secret a partir de la clave pública remota.
     *
     * @param remotePublicKeyBytes clave pública remota en formato X.509
     * @return true si el cálculo fue exitoso, false si hubo error
     */
    public boolean computeSharedSecret(byte[] remotePublicKeyBytes) {
        if (localKeyPair == null) {
            System.err.println("Error: Par de claves no ha sido generado");
            return false;
        }

        try {
            // Reconstruir clave pública remota desde bytes X.509
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(remotePublicKeyBytes);
            PublicKey remotePublicKey = keyFactory.generatePublic(keySpec);

            // Calcular shared secret usando ECDH
            KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
            keyAgreement.init(localKeyPair.getPrivate());
            keyAgreement.doPhase(remotePublicKey, true);
            this.sharedSecret = keyAgreement.generateSecret();

            return true;
        } catch (Exception e) {
            System.err.println("Error calculando shared secret: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retorna el shared secret calculado (256 bits).
     *
     * Nota: Debe llamarse después de {@link #computeSharedSecret(byte[])}.
     *
     * @return shared secret (32 bytes)
     */
    public byte[] getSharedSecret() {
        if (sharedSecret == null) {
            throw new IllegalStateException("Shared secret no ha sido calculado");
        }
        return sharedSecret;
    }

    /**
     * Log detallado de información de la clave derivada para verificación.
     *
     * @param derivedKey clave derivada (típicamente AES-256)
     */
    public static void logKeyDerivationDebug(byte[] derivedKey) {
        if (derivedKey == null || derivedKey.length == 0) {
            System.err.println("Error: Clave derivada es nula o vacía");
            return;
        }

        StringBuilder hexString = new StringBuilder();
        int bytesToShow = Math.min(8, derivedKey.length);
        for (int i = 0; i < bytesToShow; i++) {
            hexString.append(String.format("%02X ", derivedKey[i] & 0xFF));
        }

        System.out.println("[KEY EXCHANGE DEBUG]");
        System.out.println("  Clave derivada (primeros " + bytesToShow + " bytes): " + hexString.toString().trim());
        System.out.println("  Longitud total: " + derivedKey.length + " bytes");
    }
}
