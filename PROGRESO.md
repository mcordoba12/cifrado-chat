# Estado del Proyecto: Chat Cifrado ECDH + AES-GCM

**Fecha de corte**: 2026-05-22
**Estado general**: 50% completo (core criptográfico + protocolo implementados)

---

## 1. Clases Completamente Implementadas ✅

### Criptografía (3 clases)
- **CryptographyManager.java**
  - `generateECKeyPair()` - ECDH P-256
  - `sha256()` - Hashing
  - `deriveAESKey()` - KDF SHA-256

- **KeyExchangeManager.java**
  - `generateLocalKeyPair()` - Genera par EC local
  - `getPublicKeyEncoded()` - X.509 format
  - `computeSharedSecret()` - ECDH con clave remota
  - `getSharedSecret()` - Retorna shared secret
  - `logKeyDerivationDebug()` - Log en hexadecimal

- **MessageCryptor.java**
  - `encrypt()` - AES-256-GCM con IV aleatorio
  - `decrypt()` - Verifica tag automáticamente
  - Formato: [IV 12B] [ciphertext] [tag 16B]

### Protocolo (3 clases)
- **Message.java**
  - `toBytes()` / `fromBytes()` - Serialización
  - Formato: [1B tipo] [4B length] [UTF-8]
  - Tipos: TYPE_NORMAL (0x00), TYPE_SHUTDOWN (0xFF)
  - `isShutdown()` - Detecta /salir

- **HandshakeMessage.java**
  - `toBytes()` / `fromBytes()` - Serialización
  - Formato: [4B length] [X.509 publicKey]

- **ProtocolHandler.java**
  - Wrapper para serialización de Message y HandshakeMessage

### Networking (1 clase)
- **Connection.java**
  - `readFrame()` - Lee [4B length][data]
  - `writeFrame()` - Escribe [4B length][data]
  - Thread-safe con synchronized blocks
  - `close()` / `isOpen()`

### UI (1 clase)
- **ChatUI.java**
  - `readUserInput()` - Lectura stdin
  - `displayMessage()` / `displaySystemMessage()` / `displayError()`
  - Formato: [SENDER]: mensaje

---

## 2. Clases Faltantes (50% del proyecto) ⚠️

### Networking - Orquestación (2 clases)

**ChatServer.java** - Servidor TCP
```
Responsabilidades:
- ServerSocket en puerto dado
- Aceptar conexión única de cliente
- Protocolo ECDH (envía pública primero)
- Chat loop: leer/encriptar/enviar y leer/desencriptar
- Shutdown graceful con /salir
- Logging detallado de handshake
```

**ChatClient.java** - Cliente TCP
```
Responsabilidades:
- Socket a host:port remoto
- Protocolo ECDH (recibe pública primero, envía la suya)
- Chat loop: leer/encriptar/enviar y leer/desencriptar
- Shutdown graceful con /salir
- Logging detallado de handshake
```

### Main (1 clase)

**Main.java** - Entry point
```
Responsabilidades:
- Parsear argumentos: --mode server/client --port/--host
- Crear e iniciar ChatServer o ChatClient
- Manejo básico de excepciones

Uso:
  java -jar cifrado-chat.jar --mode server --port 5000
  java -jar cifrado-chat.jar --mode client --host 0.tcp.ngrok.io --port 12345
```

### Tests (4 clases)

**CryptographyManagerTest.java** - Tests unitarios
**KeyExchangeManagerTest.java** - Tests de simetría ECDH
**MessageCryptorTest.java** - Tests de encrypt/decrypt
**ProtocolHandlerTest.java** - Tests de serialización

---

## 3. Orden Recomendado para Continuar

### Fase 1: Networking Core (2 horas estimado)
1. **ChatServer.java**
   - Constructor con puerto
   - Crear ServerSocket en start()
   - Aceptar conexión
   - Protocolo ECDH básico (recibir, enviar, calcular shared secret)

2. **ChatClient.java**
   - Constructor con host, port
   - Conectar en start()
   - Protocolo ECDH básico (enviar, recibir, calcular shared secret)

**Punto de control**: Ambos lados pueden hacer handshake y derivar la misma clave AES

### Fase 2: Chat Cifrado (2 horas estimado)
3. Implementar `chatLoop()` en ambos:
   - Thread para leer desde UI
   - Thread para leer desde red
   - Encriptar outgoing messages
   - Desencriptar incoming messages
   - Detectar /salir y shutdown graceful

**Punto de control**: Dos instancias locales pueden chatear cifrado

### Fase 3: Entry Point (30 minutos)
4. **Main.java**
   - Parsear argumentos
   - Crear servidor o cliente
   - Manejar excepciones

**Punto de control**: `mvn clean package` genera JAR ejecutable

### Fase 4: Testing (1 hora)
5. Implementar tests unitarios
6. Tests de integración manual (dos terminales)

---

## 4. Detalles Técnicos Importantes

### Protocolo ECDH - Orden Estricto

```
SERVIDOR:
  1. keyEx.generateLocalKeyPair(cryptoManager)
  2. conn.writeFrame(keyEx.getPublicKeyEncoded())
  3. byte[] remotePub = conn.readFrame()
  4. keyEx.computeSharedSecret(remotePub) ✓

CLIENTE:
  1. keyEx.generateLocalKeyPair(cryptoManager)
  2. byte[] remotePub = conn.readFrame()
  3. keyEx.computeSharedSecret(remotePub)
  4. conn.writeFrame(keyEx.getPublicKeyEncoded())

AMBOS:
  5. byte[] aesKey = cryptoManager.deriveAESKey(keyEx.getSharedSecret())
  6. KeyExchangeManager.logKeyDerivationDebug(aesKey)
  7. MessageCryptor msgCryptor = new MessageCryptor(aesKey)
```

**CRÍTICO**: Los primeros 8 bytes hexadecimales de la clave derivada DEBEN coincidir entre servidor y cliente. Si no coinciden, hay error en ECDH.

### Chat Loop - Patrón Concurrente

```
THREAD 1: Lectura desde usuario
  loop:
    input = ui.readUserInput()
    if input == null → EOF/error, break
    if input.equals("/salir"):
      msg = new Message(TYPE_SHUTDOWN, "")
      break
    else:
      msg = new Message(input)

    encrypted = msgCryptor.encrypt(msg.toBytes())
    conn.writeFrame(encrypted)

THREAD 2: Lectura desde red (paralelo)
  loop:
    encryptedFrame = conn.readFrame()
    if encryptedFrame == null → conexión cerrada, break

    decrypted = msgCryptor.decrypt(encryptedFrame)
    if decrypted == null → tampering detectado, mostrar error

    msg = Message.fromBytes(decrypted)
    ui.displayMessage("REMOTO", msg.getContent())

    if msg.isShutdown():
      break

SINCRONIZACIÓN:
  - Usar ExecutorService o dos Threads
  - En start() crear threads, usar join() para esperar
  - En shutdown() interrupt threads si es necesario
```

### Manejo de Errores Común
```
1. Conexión rechazada → Server no está escuchando
   - Verificar puerto correcto
   - Verificar ngrok running

2. Desincronización protocolo ECDH
   - Log está disponible en consola
   - Verificar orden: servidor primero, cliente responde

3. Tag de autenticación falla
   - Posible tampering (error real en demo)
   - Posible sincronización de mensajes corrupta
   - Mostrar error pero no desconectar (continuar chat)

4. EOF inesperado
   - Otro lado se desconectó
   - Lanzar IOException desde readFrame()
   - Capturar y hacer shutdown graceful
```

### Logging Recomendado
```
[HANDSHAKE] Generando par de claves locales...
[HANDSHAKE] Enviando pública (91 bytes)...
[HANDSHAKE] Esperando pública remota...
[HANDSHAKE] Clave remota recibida (91 bytes)
[HANDSHAKE] Shared secret calculado
[KEY EXCHANGE DEBUG]
  Clave derivada (primeros 8 bytes): 1A 2B 3C 4D 5E 6F 7G 8H
  Longitud total: 32 bytes
[CHAT] Cifrado/desencriptado establecido
```

---

## 5. Compilación y Testing Local

### Compilar
```bash
cd cifrado-chat/
mvn clean package
# Genera: target/cifrado-chat.jar
```

### Testing Local - Dos Terminales

**Terminal 1 (Servidor)**
```bash
java -jar target/cifrado-chat.jar --mode server --port 5000
# Output:
# [HANDSHAKE] Generando par de claves locales...
# [HANDSHAKE] Esperando cliente...
# [HANDSHAKE] Cliente conectado
# ...
```

**Terminal 2 (Cliente)**
```bash
java -jar target/cifrado-chat.jar --mode client --host localhost --port 5000
# Output:
# [HANDSHAKE] Conectando a localhost:5000...
# [HANDSHAKE] Conexión exitosa
# ...
```

**Luego en ambas terminales**
```
> Hola
[REMOTO]: Hola, ¿cómo estás?
> Bien, ¿y tú?
[REMOTO]: Excelente, gracias
> /salir
[SISTEMA]: Cerrando conexión...
```

### Testing con ngrok (Demo Real)

**Terminal 1 (Servidor)**
```bash
java -jar target/cifrado-chat.jar --mode server --port 5000
# Anotar la pública IP

# En otra ventana:
ngrok tcp 5000
# Copiar: tcp://X.tcp.ngrok.io:PUERTO
```

**Terminal 2 (Cliente - en otra máquina)**
```bash
java -jar target/cifrado-chat.jar --mode client --host X.tcp.ngrok.io --port PUERTO
```

### Validación del Handshake
- Ambos lados deben mostrar los **mismos primeros 8 bytes hexadecimales** de la clave derivada
- Si no coinciden → error crítico en ECDH
- Si coinciden → chat listo para usar

---

## 6. Checklist Final (Antes de Entrega)

- [ ] Ambas clases (ChatServer, ChatClient) implementadas
- [ ] Main.java parsea argumentos correctamente
- [ ] Handshake ECDH funciona (claves coinciden)
- [ ] Chat cifrado funciona con dos instancias locales
- [ ] `/salir` cierra conexión gracefully
- [ ] Logs detallados de handshake visibles
- [ ] Compilación: `mvn clean package` sin errores
- [ ] JAR ejecutable generado en `target/cifrado-chat.jar`
- [ ] Tests unitarios implementados y pasan
- [ ] README.md actualizado con instrucciones finales
- [ ] REPORTE.md completado con análisis y conclusiones

---

## 7. Archivos Clave para Referencias

- **Protocolo ECDH**: Ver diagrama en `REPORTE.md` sección "Flujo de Ejecución - Fase 3"
- **Formato de frames**: `Connection.java` línea 10
- **Formato de mensajes**: `Message.java` línea 11
- **Constantes criptográficas**: `MessageCryptor.java` línea 23-26

---

## Notas Importantes

- `CryptographyManager` es thread-safe (no guarda estado)
- `KeyExchangeManager` es de una sola use (no reutilizar después de shutdown)
- `MessageCryptor` es thread-safe (IV aleatorio por mensaje)
- `Connection` es thread-safe (synchronized en input/output)

**Good luck! 🚀**
