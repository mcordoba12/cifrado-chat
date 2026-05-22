package com.chat.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;

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

    private static final int IV_LENGTH = 12;          // bytes
    private static final int TAG_LENGTH = 128;        // bits
    private static final int TAG_LENGTH_BYTES = 16;   // bytes
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    private byte[] aesKey;
    private SecureRandom random;

    /**
     * Inicializa el criptador con la clave AES-256 derivada.
     *
     * @param aesKey clave AES-256 (32 bytes)
     */
    public MessageCryptor(byte[] aesKey) {
        if (aesKey == null || aesKey.length != 32) {
            throw new IllegalArgumentException("Clave AES debe ser de 32 bytes (256 bits)");
        }
        this.aesKey = aesKey;
        this.random = new SecureRandom();
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
        try {
            // Generar IV aleatorio
            byte[] iv = new byte[IV_LENGTH];
            random.nextBytes(iv);

            // Crear clave secreto para AES
            SecretKeySpec secretKey = new SecretKeySpec(aesKey, 0, aesKey.length, "AES");

            // Inicializar cipher con GCM
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            // Encriptar plaintext
            byte[] plaintextBytes = plaintext.getBytes("UTF-8");
            byte[] ciphertext = cipher.doFinal(plaintextBytes);

            // Concatenar: [IV] [ciphertext con tag integrado]
            byte[] result = new byte[IV_LENGTH + ciphertext.length];
            System.arraycopy(iv, 0, result, 0, IV_LENGTH);
            System.arraycopy(ciphertext, 0, result, IV_LENGTH, ciphertext.length);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error encriptando mensaje: " + e.getMessage(), e);
        }
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
        if (!validateEncryptedDataFormat(encryptedData)) {
            System.err.println("Error: Datos encriptados tienen formato inválido");
            return null;
        }

        try {
            // Extraer IV (primeros 12 bytes)
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(encryptedData, 0, iv, 0, IV_LENGTH);

            // Extraer ciphertext + tag (resto de datos)
            byte[] ciphertext = new byte[encryptedData.length - IV_LENGTH];
            System.arraycopy(encryptedData, IV_LENGTH, ciphertext, 0, ciphertext.length);

            // Crear clave secreto para AES
            SecretKeySpec secretKey = new SecretKeySpec(aesKey, 0, aesKey.length, "AES");

            // Inicializar cipher con GCM
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

            // Desencriptar (verifica tag automáticamente)
            byte[] plaintextBytes = cipher.doFinal(ciphertext);

            return new String(plaintextBytes, "UTF-8");
        } catch (javax.crypto.AEADBadTagException e) {
            System.err.println("Error: Tag de autenticación inválido - posible tampering");
            return null;
        } catch (Exception e) {
            System.err.println("Error desencriptando mensaje: " + e.getMessage());
            return null;
        }
    }

    /**
     * Valida que los datos encriptados tengan el formato correcto.
     *
     * @param encryptedData datos a validar
     * @return true si el tamaño es válido (mínimo 12 + 16 bytes)
     */
    private boolean validateEncryptedDataFormat(byte[] encryptedData) {
        // Mínimo: IV (12) + ciphertext (mínimo 0) + tag (16) = 28 bytes
        // Sin embargo, si hay ciphertext mínimo de 1 byte:
        // IV (12) + ciphertext (1) + tag (16) = 29 bytes
        // Pero en GCM, el tag está integrado en la salida de doFinal()
        // Así que mínimo es IV (12) + tag (16) = 28 bytes
        return encryptedData != null && encryptedData.length >= (IV_LENGTH + TAG_LENGTH_BYTES);
    }
}
