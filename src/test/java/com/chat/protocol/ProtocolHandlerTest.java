package com.chat.protocol;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProtocolHandlerTest {

    @Test
    public void testMessageSerialization() {
        ProtocolHandler handler = new ProtocolHandler();
        Message msg = new Message("Hola mundo");
        byte[] bytes = handler.serializeMessage(msg);
        assertNotNull(bytes);
        // 1 (tipo) + 4 (length) + 10 ("Hola mundo" UTF-8) = 15
        assertEquals(15, bytes.length);
    }

    @Test
    public void testMessageDeserialization() {
        ProtocolHandler handler = new ProtocolHandler();
        Message original = new Message("Hola mundo");
        byte[] bytes = handler.serializeMessage(original);
        Message restored = handler.deserializeMessage(bytes);
        assertNotNull(restored);
        assertEquals(original.getContent(), restored.getContent());
        assertEquals(original.getType(), restored.getType());
    }

    @Test
    public void testHandshakeSerialization() {
        ProtocolHandler handler = new ProtocolHandler();
        byte[] pubKey = new byte[91];
        for (int i = 0; i < pubKey.length; i++) pubKey[i] = (byte) i;
        HandshakeMessage original = new HandshakeMessage(pubKey);
        byte[] bytes = handler.serializeHandshake(original);
        assertNotNull(bytes);
        // 4 (length) + 91 (clave pública) = 95
        assertEquals(95, bytes.length);
    }

    @Test
    public void testHandshakeDeserialization() {
        ProtocolHandler handler = new ProtocolHandler();
        byte[] pubKey = new byte[91];
        for (int i = 0; i < pubKey.length; i++) pubKey[i] = (byte) i;
        HandshakeMessage original = new HandshakeMessage(pubKey);
        byte[] bytes = handler.serializeHandshake(original);
        HandshakeMessage restored = handler.deserializeHandshake(bytes);
        assertNotNull(restored);
        assertArrayEquals(pubKey, restored.getPublicKey());
    }
}
