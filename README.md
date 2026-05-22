# Chat Cifrado con ECDH y AES-GCM

Sistema de chat cifrado entre dos instancias usando Java, con negociación de claves mediante Elliptic Curve Diffie-Hellman (ECDH P-256) y cifrado AES-256-GCM.

## Requisitos

- Java 11+
- Maven 3.6+
- ngrok (para exposición de puerto en demostración)

## Compilación

```bash
mvn clean package
```

Genera: `target/cifrado-chat.jar`

## Uso

### Servidor

```bash
java -jar target/cifrado-chat.jar --mode server --port 5000
```

Expone con ngrok:
```bash
ngrok tcp 5000
# Obtiene: tcp://0.tcp.ngrok.io:PUERTO
```

### Cliente

```bash
java -jar target/cifrado-chat.jar --mode client --host 0.tcp.ngrok.io --port PUERTO
```

## Protocolo

1. **Key Exchange (ECDH P-256)**
   - Servidor genera par de claves, envía pública
   - Cliente genera par de claves, recibe pública del servidor y envía la propia
   - Ambos calculan el shared secret (256 bits)

2. **Derivación de Clave**
   - `AES_KEY = SHA-256(shared_secret + "CIPHER_KEY_DERIVATION")`

3. **Chat Cifrado (AES-256-GCM)**
   - Cada mensaje: `[IV 12B] [CIPHER_TEXT] [TAG 16B]`
   - IV aleatorio por cada mensaje

## Shutdown

Escribir `/salir` para cerrar la conexión de forma ordenada.

## Arquitectura

Ver `REPORTE.md` para análisis detallado.
