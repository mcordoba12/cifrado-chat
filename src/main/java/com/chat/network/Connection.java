package com.chat.network;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Abstracción de una conexión TCP.
 *
 * Responsabilidades:
 * - Lectura/escritura de frames con tamaño prefijado (length-prefixed)
 * - Formato: [4 bytes big-endian length] [data de length bytes]
 * - Manejo de cierres de conexión
 *
 * Thread-safety:
 * - Los métodos de lectura y escritura pueden llamarse desde threads distintos
 */
public class Connection {

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private boolean open;

    /**
     * Crea una conexión a partir de un socket existente.
     *
     * @param socket socket TCP existente
     * @throws IOException si hay error al crear los streams
     */
    public Connection(Socket socket) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Socket no puede ser nulo");
        }
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.open = true;
    }

    /**
     * Lee un frame completo de la conexión.
     *
     * Formato esperado:
     *   [4 bytes big-endian length] [data de length bytes]
     *
     * @return array de bytes recibido, null si la conexión se cerró
     * @throws IOException si hay error en la lectura
     */
    public byte[] readFrame() throws IOException {
        if (!open) {
            throw new IOException("Conexión cerrada");
        }

        synchronized (inputStream) {
            try {
                // Leer 4 bytes de length (big-endian)
                int length = inputStream.readInt();

                if (length < 0) {
                    System.err.println("Error: Longitud negativa recibida");
                    return null;
                }

                // Leer exactamente 'length' bytes
                byte[] data = new byte[length];
                inputStream.readFully(data);

                return data;
            } catch (IOException e) {
                // Si hay error de lectura, la conexión se cerró
                open = false;
                throw e;
            }
        }
    }

    /**
     * Escribe un frame completo en la conexión.
     *
     * Formato enviado:
     *   [4 bytes big-endian length] [data]
     *
     * @param data bytes a enviar
     * @throws IOException si hay error en la escritura
     */
    public void writeFrame(byte[] data) throws IOException {
        if (data == null) {
            throw new IllegalArgumentException("Datos no pueden ser nulos");
        }

        if (!open) {
            throw new IOException("Conexión cerrada");
        }

        synchronized (outputStream) {
            try {
                // Escribir 4 bytes de length (big-endian)
                outputStream.writeInt(data.length);

                // Escribir datos
                outputStream.write(data);

                // Flush para asegurar que se envía inmediatamente
                outputStream.flush();
            } catch (IOException e) {
                // Si hay error de escritura, la conexión se cerró
                open = false;
                throw e;
            }
        }
    }

    /**
     * Cierra la conexión de forma ordenada.
     *
     * @throws IOException si hay error al cerrar
     */
    public void close() throws IOException {
        if (!open) {
            return;
        }

        try {
            open = false;
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error cerrando conexión: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Verifica si la conexión está abierta.
     *
     * @return true si está abierta, false si está cerrada
     */
    public boolean isOpen() {
        return open && socket != null && socket.isConnected() && !socket.isClosed();
    }
}
