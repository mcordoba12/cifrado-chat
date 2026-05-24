package com.chat;

import com.chat.network.ChatClient;
import com.chat.network.ChatServer;

public class Main {

    public static void main(String[] args) {
        String mode = null;
        String host = null;
        int port = -1;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--mode":
                    if (i + 1 < args.length) mode = args[++i];
                    break;
                case "--host":
                    if (i + 1 < args.length) host = args[++i];
                    break;
                case "--port":
                    if (i + 1 < args.length) {
                        try {
                            port = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Error: puerto inválido '" + args[i] + "'");
                            System.exit(1);
                        }
                    }
                    break;
            }
        }

        if (mode == null) {
            printUsage();
            System.exit(1);
        }

        try {
            if (mode.equals("server")) {
                if (port == -1) {
                    System.err.println("Error: --port es requerido para modo server");
                    printUsage();
                    System.exit(1);
                }
                new ChatServer(port).start();

            } else if (mode.equals("client")) {
                if (host == null) {
                    System.err.println("Error: --host es requerido para modo client");
                    printUsage();
                    System.exit(1);
                }
                if (port == -1) {
                    System.err.println("Error: --port es requerido para modo client");
                    printUsage();
                    System.exit(1);
                }
                new ChatClient(host, port).start();

            } else {
                System.err.println("Error: --mode debe ser 'server' o 'client'");
                printUsage();
                System.exit(1);
            }

        } catch (Exception e) {
            System.err.println("Error fatal: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.err.println("Uso:");
        System.err.println("  Servidor: java -jar cifrado-chat.jar --mode server --port 5000");
        System.err.println("  Cliente:  java -jar cifrado-chat.jar --mode client --host 0.tcp.ngrok.io --port 12345");
    }
}
