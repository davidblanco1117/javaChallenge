# Challenge - High Concurrency Order Processing API

## Descripción

Este proyecto implementa una API REST para procesamiento de pedidos con alta concurrencia, diseñada para manejar hasta 1000 requests concurrentes de manera eficiente. La aplicación utiliza Spring Boot con procesamiento asíncrono para simular un sistema de e-commerce real.

## Características Principales

- **Procesamiento Asíncrono**: Utiliza `CompletableFuture` para manejar pedidos de forma no bloqueante
- **Simulación de Delay**: Simula el tiempo real de procesamiento de pedidos (200-500ms)
- **Alta Concurrencia**: Diseñado para manejar múltiples requests simultáneos
- **Logging y Métricas**: Registra tiempos de procesamiento y operaciones clave
- **Arquitectura Limpia**: Separación clara de responsabilidades con DTOs y servicios

## Arquitectura del Sistema

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controller    │───▶│   OrderService  │───▶│  OrderStorage   │
│   (REST API)    │    │  (Async Logic)  │    │  (In-Memory)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       ▼                       │
         │              ┌─────────────────┐              │
         │              │ ProductCatalog  │              │
         │              │  (In-Memory)    │              │
         │              └─────────────────┘              │
         │                                               │
         ▼                                               ▼
┌─────────────────┐                            ┌─────────────────┐
│   DTOs          │                            │   Domain        │
│   (Request/     │                            │   Models        │
│    Response)    │                            │                 │
└─────────────────┘                            └─────────────────┘
```

### Componentes Principales

- **OrderController**: Endpoints REST para procesar pedidos y consultar datos
- **OrderService**: Lógica de negocio con procesamiento asíncrono
- **ProductCatalog**: Catálogo de productos en memoria
- **OrderStorage**: Almacenamiento de pedidos procesados
- **DTOs**: Objetos de transferencia para la API

## Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Web MVC**
- **Maven**
- **JUnit 5** (Testing)
- **MockMvc** (Integration Testing)

## Requisitos del Sistema

- Java 17 o superior
- Maven 3.6+
- Mínimo 2GB RAM disponible

## Instalación y Despliegue

### 1. Clonar el Repositorio

```bash
git clone <repository-url>
cd challenge
```

### 2. Compilar el Proyecto

```bash
./mvnw clean compile
```

### 3. Ejecutar Tests

```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar tests específicos
./mvnw test -Dtest=OrderServiceTest
./mvnw test -Dtest=IntegrationOrderControllerTest
```

### 4. Ejecutar la Aplicación

```bash
# Usando Maven
./mvnw spring-boot:run

# O compilar y ejecutar el JAR
./mvnw clean package
java -jar target/challenge-0.0.1-SNAPSHOT.jar
```

La aplicación estará disponible en: `http://localhost:8080`

## Endpoints de la API

### 1. Procesar Pedido
```http
POST /processOrder
Content-Type: application/json

{
    "orderId": "ORD-001",
    "customerId" : "CUS-001",
    "orderAmount" : 1100.0, 
    "orderItems": [
        {
            "itemId": "P-001",
            "quantity": 1
        },
        
        {
            "itemId": "P-002",
            "quantity": 2
        }
    ]
}
```

**Respuesta:**
```json
{
  "orderId": "ORDER-001",
  "status": "PROCESSED",
  "processingTimeMs": 276,
  "message": "Order processed successfully",
  "processedAt": "2024-01-01T10:00:00"
}
```
*(Los campos pueden variar según el resultado y el error)*

**Restricción importante:**
- No se permite crear una orden con un `orderId` que ya exista. Si se intenta, la API devolverá un error de validación.

**Ejemplo de error:**
```json
{
  "orderId": "ORDER-001",
  "status": "ERROR",
  "processingTimeMs": 0,
  "message": "Order with id 'ORDER-001' already exists",
  "processedAt": "2024-01-01T10:00:00"
}
```

### 2. Obtener Pedidos
```http
GET /get-orders?status=PROCESSED
GET /get-orders?status=ERROR
GET /get-orders
```

**Parámetros opcionales:**
- `status`: Filtra las órdenes por estado. Si no se especifica, devuelve todas las órdenes.

**Ejemplo de respuesta:**
```json
[
  {
    "orderId": "ORDER-001",
    "status": "PROCESSED",
    "processingTimeMs": 276,
    "message": "Order processed successfully",
    "processedAt": "2024-01-01T10:00:00"
  }
]
```

**Nota:** El filtrado se realiza de manera eficiente en una sola iteración, optimizando el rendimiento incluso con grandes volúmenes de órdenes.

### 3. Obtener Catálogo
```http
GET /get-catalog
```

**Respuesta:**
```json
[
  {
    "itemId": "P-001",
    "name": "Producto 1",
    "price": 500.00
  }
]
```

### 4. Verificar Órdenes de Carga JMeter
```http
GET /check-jmeter-orders
```
Devuelve `true` si todas las órdenes `ORDER-1` a `ORDER-1000` están en estado `PROCESSED`, o `false` si alguna falta o no fue procesada correctamente.

**Ejemplo de respuesta:**
```json
true
```

## Pruebas de Carga con JMeter

El archivo `order-process-concurrency-test.jmx` se encuentra en la carpeta `src/main/resources` del proyecto. Puedes importarlo directamente en JMeter para simular 1000 solicitudes concurrentes al endpoint `/processOrder` y ajustar los parámetros según tus necesidades de stress test.

Para usarlo:
1. Abre JMeter.
2. Ve a `File` > `Open...` y selecciona el archivo `order-process-concurrency-test.jmx`.
3. Ajusta la cantidad de hilos, ramp-up o el body si lo deseas.
4. Ejecuta la prueba y analiza los resultados.

## Resultados de Pruebas de Carga

Consulta los resultados y análisis de las pruebas de carga en el archivo [`src/main/resources/jmeter-test-results.md`](src/main/resources/jmeter-test-results.md), donde se muestran capturas (`img/jmeter-summary.png`, `img/postman-jmeter-check.png`) y conclusiones del rendimiento bajo 1000 solicitudes concurrentes y la verificación de órdenes procesadas.

## Colección de Postman

En la carpeta `src/main/resources` se deja una colección de Postman lista para importar y probar todos los endpoints del sistema (`/processOrder`, `/get-orders`, `/get-catalog`, `/check-jmeter-orders`).

**Para usarla:**
1. Abre Postman.
2. Importa el archivo de colección desde `src/main/resources/postman-collection.json` .
3. Ejecuta y prueba los endpoints fácilmente.

## Testing

### Tests Unitarios
- **OrderServiceTest**: Prueba la lógica de negocio del servicio
- Cobertura de casos de éxito y error
- Uso de mocks para dependencias externas

### Tests de Integración
- **IntegrationOrderControllerTest**: Prueba los endpoints REST
- Manejo correcto de respuestas asíncronas
- Verificación de respuestas HTTP y contenido

### Ejecutar Tests Específicos

```bash
# Tests unitarios del servicio
./mvnw test -Dtest=OrderServiceTest

# Tests de integración
./mvnw test -Dtest=IntegrationOrderControllerTest

# Test específico de procesamiento
./mvnw test -Dtest=IntegrationOrderControllerTest#process_integration
```

## Configuración

### application.properties
```properties
# Configuración del servidor
server.port=8080

# Configuración de logging
logging.level.ar.com.francoblanco.challenge=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# Tomcat
server.tomcat.max-threads=2000
server.tomcat.accept-count=2000
server.tomcat.max-connections=4000

# Pool de threads para procesamiento asíncrono
executorTareas.corePoolSize=300
executorTareas.maxPoolSize=500
executorTareas.queueCapacity=2000
```

### Configuración de Thread Pool
El sistema utiliza un thread pool configurado para manejar la concurrencia:
- Tamaño del pool: Configurado para manejar múltiples requests simultáneos
- Política de rechazo: CallerRunsPolicy para evitar pérdida de requests

## Monitoreo y Logging

### Logs de Procesamiento
La aplicación registra:
- Tiempo de procesamiento de cada pedido
- Información del pedido procesado
- Errores y excepciones

### Ejemplo de Log
```
2024-01-01 10:00:00 - Order ORDER-001 processed in 276ms
```

## Casos de Uso y Escenarios

### 1. Procesamiento Normal
- Cliente envía pedido válido
- Sistema procesa de forma asíncrona
- Retorna confirmación inmediata

### 2. Alta Concurrencia
- Múltiples pedidos simultáneos
- Procesamiento paralelo sin bloqueos
- Logging de tiempos de respuesta

### 3. Consulta de Datos
- Obtención de pedidos procesados
- Consulta del catálogo de productos
- Respuestas rápidas y eficientes

## Estructura del Proyecto

```
challenge/
├── src/
│   ├── main/
│   │   ├── java/ar/com/francoblanco/challenge/
│   │   │   ├── application/
│   │   │   │   └── config/
│   │   │   │       ├── Config.java
│   │   │   │       └── OpenApiConfig.java
│   │   │   ├── domain/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── OrderItemRequest.java
│   │   │   │   │   └── OrderRequest.java
│   │   │   │   └── model/
│   │   │   │       ├── OrderResult.java
│   │   │   │       └── Product.java
│   │   │   ├── infraestructure/
│   │   │   │   ├── controller/
│   │   │   │   │   └── OrderController.java
│   │   │   │   └── service/
│   │   │   │       ├── OrderService.java
│   │   │   │       ├── OrderStorage.java
│   │   │   │       └── ProductCatalog.java
│   │   │   └── ChallengeApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/ar/com/francoblanco/challenge/
│           ├── infraestructure/
│           │   └── service/
│           │       ├── IntegrationOrderControllerTest.java
│           │       └── OrderServiceTest.java
│           └── ChallengeApplicationTests.java
├── pom.xml
└── README.md
```

## Solución de Problemas

### Error: Puerto en Uso
```bash
# Cambiar puerto en application.properties
server.port=8081
```

### Error: Memoria Insuficiente
```bash
# Aumentar memoria JVM
java -Xmx2g -jar target/challenge-0.0.1-SNAPSHOT.jar
```

### Tests Fallando
```bash
# Limpiar y recompilar
./mvnw clean compile test
```

## Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## Contacto

Franco Blanco - [riddid117@hotmail.com]

Link del proyecto: [https://github.com/davidblanco1117/javaChallenge](https://github.com/davidblanco1117/javaChallenge) 