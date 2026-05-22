package com.chat.network;

import java.io.IOException;

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
        // TODO: Implementar
        return null;
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
        // TODO: Implementar
    }

    /**
     * Cierra la conexión de forma ordenada.
     *
     * @throws IOException si hay error al cerrar
     */
    public void close() throws IOException {
        // TODO: Implementar
    }

    /**
     * Verifica si la conexión está abierta.
     *
     * @return true si está abierta, false si está cerrada
     */
    public boolean isOpen() {
        // TODO: Implementar
        return false;
    }
}
