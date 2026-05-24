package com.chat.crypto;

import org.junit.Test;

import static org.junit.Assert.*;

public class KeyExchangeManagerTest {

    @Test
    public void testKeyPairGeneration() {
        CryptographyManager cm = new CryptographyManager();
        KeyExchangeManager kex = new KeyExchangeManager();
        kex.generateLocalKeyPair(cm);
        byte[] pub = kex.getPublicKeyEncoded();
        assertNotNull(pub);
        assertTrue(pub.length > 0);
    }

    @Test
    public void testPublicKeyEncoding() {
        CryptographyManager cm = new CryptographyManager();
        KeyExchangeManager kex = new KeyExchangeManager();
        kex.generateLocalKeyPair(cm);
        byte[] pub = kex.getPublicKeyEncoded();
        // Clave pública P-256 en formato X.509 SubjectPublicKeyInfo = 91 bytes
        assertEquals(91, pub.length);
    }

    @Test
    public void testSharedSecretComputation() {
        CryptographyManager cm = new CryptographyManager();
        KeyExchangeManager alice = new KeyExchangeManager();
        KeyExchangeManager bob = new KeyExchangeManager();
        alice.generateLocalKeyPair(cm);
        bob.generateLocalKeyPair(cm);

        boolean ok = alice.computeSharedSecret(bob.getPublicKeyEncoded());
        assertTrue(ok);

        byte[] secret = alice.getSharedSecret();
        assertNotNull(secret);
        assertEquals(32, secret.length);
    }

    @Test
    public void testSharedSecretSymmetry() {
        // Propiedad crítica de ECDH: ambos lados deben calcular el mismo secret
        CryptographyManager cm = new CryptographyManager();
        KeyExchangeManager alice = new KeyExchangeManager();
        KeyExchangeManager bob = new KeyExchangeManager();
        alice.generateLocalKeyPair(cm);
        bob.generateLocalKeyPair(cm);

        alice.computeSharedSecret(bob.getPublicKeyEncoded());
        bob.computeSharedSecret(alice.getPublicKeyEncoded());

        assertArrayEquals(alice.getSharedSecret(), bob.getSharedSecret());
    }
}
