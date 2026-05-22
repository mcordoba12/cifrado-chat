package com.chat.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Interfaz de usuario del chat.
 *
 * Responsabilidades:
 * - Lectura de entrada del usuario desde stdin
 * - Mostrar mensajes recibidos en stdout
 * - Manejo de comando /salir
 *
 * Flujo:
 * 1. new ChatUI()
 * 2. readUserInput() → lee líneas de stdin en thread separado
 * 3. displayMessage() → muestra mensajes recibidos
 */
public class ChatUI {

    private BufferedReader inputReader;

    /**
     * Crea la UI del chat.
     */
    public ChatUI() {
        this.inputReader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Lee una línea de entrada del usuario.
     *
     * @return línea leída, null si EOF o error
     * @throws IOException si hay error en lectura
     */
    public String readUserInput() throws IOException {
        try {
            String line = inputReader.readLine();
            return line;  // null si EOF
        } catch (IOException e) {
            System.err.println("Error leyendo entrada del usuario: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Muestra un mensaje recibido en la salida.
     *
     * @param sender quién envió el mensaje
     * @param content contenido del mensaje
     */
    public void displayMessage(String sender, String content) {
        System.out.println();
        System.out.println("[" + sender + "]: " + content);
        System.out.print("> ");
        System.out.flush();
    }

    /**
     * Muestra un mensaje informativo del sistema.
     *
     * @param message mensaje a mostrar
     */
    public void displaySystemMessage(String message) {
        System.out.println();
        System.out.println("[SISTEMA]: " + message);
        System.out.print("> ");
        System.out.flush();
    }

    /**
     * Muestra un mensaje de error.
     *
     * @param error error a mostrar
     */
    public void displayError(String error) {
        System.out.println();
        System.out.println("[ERROR]: " + error);
        System.out.print("> ");
        System.out.flush();
    }

    /**
     * Cierra la interfaz de usuario.
     *
     * @throws IOException si hay error al cerrar
     */
    public void close() throws IOException {
        try {
            if (inputReader != null) {
                inputReader.close();
            }
        } catch (IOException e) {
            System.err.println("Error cerrando UI: " + e.getMessage());
            throw e;
        }
    }
}
