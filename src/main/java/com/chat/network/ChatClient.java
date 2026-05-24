package com.chat.network;

import com.chat.crypto.CryptographyManager;
import com.chat.crypto.KeyExchangeManager;
import com.chat.crypto.MessageCryptor;
import com.chat.protocol.Message;
import com.chat.ui.ChatUI;

import java.io.IOException;
import java.net.Socket;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatClient {

    private String host;
    private int port;
    private Connection connection;
    private MessageCryptor cryptor;
    private ChatUI ui;
    private AtomicBoolean active;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.ui = new ChatUI();
        this.active = new AtomicBoolean(false);
    }

    public void start() throws Exception {
        CryptographyManager cryptoManager = new CryptographyManager();
        KeyExchangeManager keyEx = new KeyExchangeManager();

        System.out.println("[HANDSHAKE] Generando par de claves locales...");
        keyEx.generateLocalKeyPair(cryptoManager);

        System.out.println("[HANDSHAKE] Conectando a " + host + ":" + port + "...");
        Socket socket = new Socket(host, port);
        System.out.println("[HANDSHAKE] Conexión establecida");
        this.connection = new Connection(socket);

        // El cliente recibe primero la clave pública del servidor
        System.out.println("[HANDSHAKE] Esperando clave pública del servidor...");
        byte[] remotePub = connection.readFrame();
        System.out.println("[HANDSHAKE] Clave del servidor recibida (" + remotePub.length + " bytes)");

        if (!keyEx.computeSharedSecret(remotePub)) {
            throw new Exception("Error calculando shared secret ECDH");
        }
        System.out.println("[HANDSHAKE] Shared secret calculado");

        // Luego envía su propia clave pública
        byte[] localPub = keyEx.getPublicKeyEncoded();
        System.out.println("[HANDSHAKE] Enviando clave pública propia (" + localPub.length + " bytes)...");
        connection.writeFrame(localPub);

        byte[] aesKey = cryptoManager.deriveAESKey(keyEx.getSharedSecret());
        KeyExchangeManager.logKeyDerivationDebug(aesKey);
        this.cryptor = new MessageCryptor(aesKey);

        System.out.println("[CHAT] Cifrado establecido. Escribe tu mensaje (o /salir para terminar):");
        try {
            chatLoop();
        } finally {
            shutdown();
        }
    }

    public void chatLoop() throws Exception {
        active.set(true);

        Thread receiver = new Thread(() -> {
            try {
                while (active.get()) {
                    byte[] frame = connection.readFrame();
                    if (frame == null) break;

                    String payload = cryptor.decrypt(frame);
                    if (payload == null) {
                        ui.displayError("Mensaje corrupto o manipulado detectado");
                        continue;
                    }

                    byte[] msgBytes = Base64.getDecoder().decode(payload);
                    Message msg = Message.fromBytes(msgBytes);
                    if (msg == null) continue;

                    if (msg.isShutdown()) {
                        ui.displaySystemMessage("El otro usuario se desconectó. Presiona Enter para salir.");
                        break;
                    }
                    ui.displayMessage("REMOTO", msg.getContent());
                }
            } catch (IOException e) {
                if (active.get()) {
                    ui.displaySystemMessage("Conexión cerrada inesperadamente. Presiona Enter para salir.");
                }
            } finally {
                active.set(false);
            }
        });
        receiver.setDaemon(true);
        receiver.start();

        try {
            while (active.get()) {
                System.out.print("> ");
                System.out.flush();
                String input = ui.readUserInput();

                if (!active.get()) break;
                if (input == null) {
                    sendShutdown();
                    break;
                }

                boolean isShutdown = input.equals("/salir");
                Message msg = isShutdown
                    ? new Message(Message.TYPE_SHUTDOWN, "")
                    : new Message(input);

                try {
                    String payload = Base64.getEncoder().encodeToString(msg.toBytes());
                    connection.writeFrame(cryptor.encrypt(payload));
                } catch (IOException e) {
                    break;
                }

                if (isShutdown) break;
            }
        } catch (IOException e) {
            // stdin cerrado
        }

        active.set(false);
        try { connection.close(); } catch (IOException e) { /* ya cerrada */ }
        receiver.join(2000);
    }

    private void sendShutdown() {
        if (cryptor == null) return;
        try {
            Message msg = new Message(Message.TYPE_SHUTDOWN, "");
            String payload = Base64.getEncoder().encodeToString(msg.toBytes());
            connection.writeFrame(cryptor.encrypt(payload));
        } catch (Exception e) {
            // conexión puede estar ya cerrada
        }
    }

    public void shutdown() throws Exception {
        active.set(false);
        try {
            if (connection != null) connection.close();
        } catch (IOException e) {
            // ya cerrada
        }
        System.out.println("[SISTEMA]: Chat terminado.");
    }
}
