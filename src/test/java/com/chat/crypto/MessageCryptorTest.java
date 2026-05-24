package com.chat.crypto;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessageCryptorTest {

    private static final byte[] TEST_KEY = new byte[32]; // clave de prueba: 32 bytes ceros

    @Test
    public void testEncryptDecrypt() {
        MessageCryptor cryptor = new MessageCryptor(TEST_KEY);
        String original = "Hola mundo cifrado";
        byte[] encrypted = cryptor.encrypt(original);
        String decrypted = cryptor.decrypt(encrypted);
        assertEquals(original, decrypted);
    }

    @Test
    public void testEncryptedFormatSize() {
        // Formato esperado: [IV 12B] [ciphertext] [tag 16B]
        MessageCryptor cryptor = new MessageCryptor(TEST_KEY);
        String msg = "test"; // 4 bytes UTF-8
        byte[] encrypted = cryptor.encrypt(msg);
        // 12 (IV) + 4 (ciphertext) + 16 (tag GCM) = 32
        assertEquals(32, encrypted.length);
    }

    @Test
    public void testAuthenticationTagVerification() {
        MessageCryptor cryptor = new MessageCryptor(TEST_KEY);
        byte[] encrypted = cryptor.encrypt("mensaje secreto");
        // Manipular el último byte del tag de autenticación
        encrypted[encrypted.length - 1] ^= (byte) 0xFF;
        String result = cryptor.decrypt(encrypted);
        assertNull("El tampering debe ser detectado y retornar null", result);
    }

    @Test
    public void testUniqueIVPerMessage() {
        MessageCryptor cryptor = new MessageCryptor(TEST_KEY);
        byte[] enc1 = cryptor.encrypt("mismo contenido");
        byte[] enc2 = cryptor.encrypt("mismo contenido");
        // Los primeros 12 bytes son el IV; deben ser distintos (aleatorios)
        boolean ivsDiffer = false;
        for (int i = 0; i < 12; i++) {
            if (enc1[i] != enc2[i]) {
                ivsDiffer = true;
                break;
            }
        }
        assertTrue("Cada mensaje debe tener un IV único", ivsDiffer);
    }
}
