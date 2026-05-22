package com.chat.crypto;

import org.junit.Test;

/**
 * Tests para MessageCryptor.
 */
public class MessageCryptorTest {

    @Test
    public void testEncryptDecrypt() {
        // TODO: Implementar test
    }

    @Test
    public void testEncryptedFormatSize() {
        // TODO: Implementar test
        // Verifica que el formato sea: [IV 12B] [CIPHER] [TAG 16B]
    }

    @Test
    public void testAuthenticationTagVerification() {
        // TODO: Implementar test
        // Verifica que tampering sea detectado
    }

    @Test
    public void testUniqueIVPerMessage() {
        // TODO: Implementar test
        // Verifica que IVs sean distintos por mensaje
    }
}
