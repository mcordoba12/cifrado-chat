# Reporte: Chat Cifrado con ECDH y AES-GCM

## Resumen Ejecutivo

Este proyecto implementa un sistema de chat cifrado entre dos instancias de un programa Java, utilizando:
- **Intercambio de claves**: Elliptic Curve Diffie-Hellman (ECDH) con curva P-256
- **Cifrado**: AES-256 en modo GCM (Galois/Counter Mode)
- **Transporte**: TCP sobre ngrok para demostración remota

## Análisis de Requisitos

### Requisitos Funcionales
1. Dos instancias conectadas por red (TCP)
2. Negociación de clave ECDH
3. Derivación a 256 bits para AES
4. Cifrado de mensajes con AES-256-GCM
5. API criptográfica nativa de Java

### Requisitos No-Funcionales
- Sustentación remota via Zoom + ngrok
- CLI simple (stdin/stdout)
- Logging detallado del handshake para verificación

## Decisiones Arquitectónicas

### 1. ECDH vs DH Tradicional

**Decisión**: ECDH con curva P-256 (secp256r1)

**Justificación**:
- Seguridad equivalente a DH-3072 con llaves de 256 bits
- Mejor rendimiento (~10x más rápido)
- Soporte nativo en javax.crypto
- Recomendado por NIST SP 800-56A

### 2. AES-GCM vs AES-CBC

**Decisión**: AES-256-GCM

**Justificación**:
- AEAD: Autenticación integrada sin HMAC separado
- Detecta tampering y reordenamiento
- Más seguro contra ataques de malleabilidad
- Soporte nativo en javax.crypto

### 3. Arquitectura de Clases

```
CryptographyManager
  ├─ generateECKeyPair()
  ├─ sha256()
  └─ deriveAESKey()

KeyExchangeManager
  ├─ generateLocalKeyPair()
  ├─ getPublicKeyEncoded()
  ├─ computeSharedSecret()
  └─ getSharedSecret()

MessageCryptor
  ├─ encrypt()
  └─ decrypt()

Connection
  ├─ readFrame()
  ├─ writeFrame()
  └─ close()

ChatServer / ChatClient
  ├─ start()
  ├─ chatLoop()
  └─ shutdown()

ProtocolHandler
  ├─ serializeMessage()
  ├─ deserializeMessage()
  ├─ serializeHandshake()
  └─ deserializeHandshake()

ChatUI
  ├─ readUserInput()
  ├─ displayMessage()
  └─ displaySystemMessage()
```

## Flujo de Ejecución

### Fase 1: Inicio del Servidor
```
Servidor: java -jar cifrado-chat.jar --mode server --port 5000
  → Escucha en 0.0.0.0:5000
  → Expone con ngrok: tcp://0.tcp.ngrok.io:PUERTO
  → Comparte URL con cliente
```

### Fase 2: Conexión del Cliente
```
Cliente: java -jar cifrado-chat.jar --mode client --host 0.tcp.ngrok.io --port PUERTO
  → Conecta TCP
  → Inicia protocolo ECDH
```

### Fase 3: Key Exchange (ECDH)
```
Servidor:
  1. Genera par EC (privada_s, pública_s)
  2. Envía: [LENGTH 2B] [pública_s en X.509]
  
Cliente:
  3. Recibe pública_s
  4. Genera par EC (privada_c, pública_c)
  5. Calcula: shared_secret = ECDH(privada_c, pública_s)
  6. Envía: [LENGTH 2B] [pública_c en X.509]
  
Servidor:
  7. Recibe pública_c
  8. Calcula: shared_secret = ECDH(privada_s, pública_c)
  → Ambos tienen shared_secret idéntico
```

### Fase 4: Derivación de Clave AES
```
Ambos:
  aes_key = SHA-256(shared_secret + "CIPHER_KEY_DERIVATION")
  → 32 bytes listos para AES-256
  
  Log detallado: "Clave derivada (primeros 8 bytes hex): XX XX XX XX XX XX XX XX"
```

### Fase 5: Chat Cifrado
```
Usuario A: "Hola"
  → Message m("Hola") → bytes
  → encrypt(m.bytes, aes_key, random_iv)
  → [IV 12B] [CIPHER] [TAG 16B]
  → Envía por TCP

Usuario B:
  → Recibe [IV 12B] [CIPHER] [TAG 16B]
  → decrypt(data, aes_key) → verifica TAG
  → Muestra "Hola"
```

## Dificultades Técnicas y Soluciones

| Dificultad | Solución |
|-----------|----------|
| Resultado ECDH no es directamente una clave | Usar SHA-256 como KDF sobre shared_secret |
| IV debe ser único por mensaje | `SecureRandom` genera 12 bytes aleatorios por mensaje |
| Serialización de claves EC | `getEncoded()` retorna X.509 SubjectPublicKeyInfo |
| Sincronización de handshake | Protocolo strict: servidor primero, cliente responde |
| Verificación de que ambos lados tiene la misma clave | Logging de primeros bytes de clave derivada |
| Mensajes variable-length por TCP | Prefijo length big-endian: [4B] [data] |
| Graceful shutdown | Comando `/salir` especial en UI |

## Protocolo en Detalle

### Intercambio de Claves Públicas
```
[2 bytes big-endian length] [X.509 SubjectPublicKeyInfo bytes]
Longitud típica: ~91 bytes
```

### Mensaje de Chat (Cifrado)
```
[4 bytes big-endian length] [IV 12B] [CIPHER_TEXT] [TAG 16B]
Longitud: 4 + 12 + variable + 16 bytes
```

### Mensaje de Chat (Serializado, antes de cifrado)
```
[1 byte tipo] [4 bytes big-endian length] [texto UTF-8]
Tipo 0x00 = mensaje normal
Tipo 0xFF = comando /salir
```

## Estructura del Código

```
cifrado-chat/
├── pom.xml
├── README.md
├── REPORTE.md
└── src/
    ├── main/java/com/chat/
    │   ├── Main.java
    │   ├── crypto/
    │   │   ├── CryptographyManager.java
    │   │   ├── KeyExchangeManager.java
    │   │   └── MessageCryptor.java
    │   ├── network/
    │   │   ├── ChatServer.java
    │   │   ├── ChatClient.java
    │   │   └── Connection.java
    │   ├── protocol/
    │   │   ├── Message.java
    │   │   ├── HandshakeMessage.java
    │   │   └── ProtocolHandler.java
    │   └── ui/
    │       └── ChatUI.java
    └── test/java/com/chat/
        ├── crypto/*Test.java
        └── protocol/*Test.java
```

## Testing

### Tests Unitarios
- `CryptographyManagerTest`: validación de operaciones criptográficas
- `KeyExchangeManagerTest`: simetría de shared secret
- `MessageCryptorTest`: encrypt/decrypt, formato, autenticación
- `ProtocolHandlerTest`: serialización/deserialización

### Testing Manual
1. Compilar: `mvn clean package`
2. Terminal 1: Servidor en localhost
3. Terminal 2: Cliente conecta a servidor
4. Verificar logs de key derivation coinciden
5. Intercambiar mensajes
6. Usar `/salir` para shutdown

## Conclusiones y Lecciones Aprendidas

[Por completar tras implementación]

## Referencias

- NIST SP 800-56A Rev. 3: Recommendation for Pair-Wise Key-Establishment Schemes
- FIPS 186-4: Digital Signature Standard (DSS)
- RFC 3394: Advanced Encryption Standard (AES) Key Wrap Algorithm
- Java Cryptography Architecture (JCA)

