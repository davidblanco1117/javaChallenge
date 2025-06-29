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
POST /process
Content-Type: application/json

{
  "orderId": "ORDER-001",
  "customerId": "CUST-001",
  "orderAmount": 1000.00,
  "orderItems": [
    {
      "itemId": "P-001",
      "quantity": 2
    }
  ]
}
```

**Respuesta:**
```
PROCESSED
```

### 2. Obtener Pedidos
```http
GET /get-orders
```

**Respuesta:**
```json
[
  {
    "orderId": "ORDER-001",
    "customerId": "CUST-001",
    "orderAmount": 1000.00,
    "orderItems": [...],
    "processedAt": "2024-01-01T10:00:00"
  }
]
```

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