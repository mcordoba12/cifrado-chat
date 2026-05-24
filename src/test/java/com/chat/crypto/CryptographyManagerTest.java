package com.chat.crypto;

import org.junit.Test;
import java.security.KeyPair;
import java.util.Arrays;

import static org.junit.Assert.*;

public class CryptographyManagerTest {

    @Test
    public void testGenerateECKeyPair() {
        CryptographyManager cm = new CryptographyManager();
        KeyPair kp = cm.generateECKeyPair();
        assertNotNull(kp);
        assertNotNull(kp.getPublic());
        assertNotNull(kp.getPrivate());
        assertEquals("EC", kp.getPublic().getAlgorithm());
        assertEquals("EC", kp.getPrivate().getAlgorithm());
    }

    @Test
    public void testSHA256() {
        CryptographyManager cm = new CryptographyManager();
        byte[] hash = cm.sha256("test".getBytes());

        assertNotNull(hash);
        assertEquals(32, hash.length);

        // Determinista: misma entrada → mismo hash
        assertArrayEquals(hash, cm.sha256("test".getBytes()));

        // Entradas distintas → hashes distintos
        assertFalse(Arrays.equals(hash, cm.sha256("diferente".getBytes())));
    }

    @Test
    public void testDeriveAESKey() {
        CryptographyManager cm = new CryptographyManager();
        byte[] secret = new byte[32];

        byte[] key = cm.deriveAESKey(secret);
        assertNotNull(key);
        assertEquals(32, key.length);

        // Determinista
        assertArrayEquals(key, cm.deriveAESKey(secret));

        // Secrets distintos → claves distintas
        byte[] secret2 = new byte[32];
        secret2[0] = 1;
        assertFalse(Arrays.equals(key, cm.deriveAESKey(secret2)));
    }
}
