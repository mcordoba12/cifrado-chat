package com.chat.ui;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Interfaz de usuario del chat.
 *
 * Responsabilidades:
 * - Lectura de entrada del usuario desde stdin
 * - Mostrar mensajes recibidos en stdout
 * - Manejo de comando /salir
 *
 * Flujo:
 * 1. new ChatUI(connection, messageWriter)
 * 2. start() → thread que lee stdin y envía mensajes cifrados
 * 3. Muestra mensajes recibidos en el hilo principal
 */
public class ChatUI {

    private BufferedReader inputReader;

    /**
     * Crea la UI del chat.
     */
    public ChatUI() {
        // TODO: Implementar
    }

    /**
     * Lee una línea de entrada del usuario.
     *
     * @return línea leída, null si EOF o error
     * @throws IOException si hay error en lectura
     */
    public String readUserInput() throws IOException {
        // TODO: Implementar
        return null;
    }

    /**
     * Muestra un mensaje recibido en la salida.
     *
     * @param sender quién envió el mensaje
     * @param content contenido del mensaje
     */
    public void displayMessage(String sender, String content) {
        // TODO: Implementar
    }

    /**
     * Muestra un mensaje informativo del sistema.
     *
     * @param message mensaje a mostrar
     */
    public void displaySystemMessage(String message) {
        // TODO: Implementar
    }

    /**
     * Muestra un mensaje de error.
     *
     * @param error error a mostrar
     */
    public void displayError(String error) {
        // TODO: Implementar
    }

    /**
     * Cierra la interfaz de usuario.
     *
     * @throws IOException si hay error al cerrar
     */
    public void close() throws IOException {
        // TODO: Implementar
    }
}
