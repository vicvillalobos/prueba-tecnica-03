# Prueba Técnica 03

API REST para gestión de dos modelos (Book y Customer), hecha con Java 21, Javalin 7 y MapDB.

## Descripción

El proyecto expone dos CRUDs completos a través de endpoints REST (con validación de campos):

- **Libros** (`/api/v1/books`) — título, año de publicación, edición, nombre del autor.
- **Clientes** (`/api/v1/customers`) — nombre, apellido, email, género (M, F, O).

Los datos son guardados en un archivo local usando MapDB (`data/app.db`).

## Requisitos

- Java 21 o superior
- Maven 3.x

## Compilación

```bash
mvn clean package
```

Esto generará un JAR ejecutable con todas las dependencias incluidas en `target/prueba-tecnica-03-1.0-SNAPSHOT.jar`.

## Ejecución

```bash
java -jar target/prueba-tecnica-03-1.0-SNAPSHOT.jar
```

El servidor escuchará en `http://localhost:7070`.

## Endpoints

| Metodo   | Ruta                      | Descripcion               |
|----------|---------------------------|---------------------------|
| `GET`    | `/api/v1/books`           | Listar todos los libros   |
| `GET`    | `/api/v1/books/{id}`      | Obtener un libro por ID   |
| `POST`   | `/api/v1/books`           | Crear un libro            |
| `PATCH`  | `/api/v1/books/{id}`      | Actualizar un libro       |
| `DELETE` | `/api/v1/books/{id}`      | Eliminar un libro         |
| `GET`    | `/api/v1/customers`       | Listar todos los clientes |
| `GET`    | `/api/v1/customers/{id}`  | Obtener un cliente por ID |
| `POST`   | `/api/v1/customers`       | Crear un cliente          |
| `PATCH`  | `/api/v1/customers/{id}`  | Actualizar un cliente     |
| `DELETE` | `/api/v1/customers/{id}`  | Eliminar un cliente       |

## Ejemplos

Crear un libro:

```bash
curl -X POST http://localhost:7070/api/v1/books \
  -H "Content-Type: application/json" \
  -d '{"title": "Cien años de soledad", "publicationYear": 1967, "edition": 1, "authorName": "Gabriel Garcia Marquez"}'
```

Crear un cliente:

```bash
curl -X POST http://localhost:7070/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{"firstName": "Juan", "lastName": "Perez", "email": "juan@correo.cl", "gender": "M"}'
```

## Pruebas

Las pruebas se encuentran en `src/test/java/com/vsraven/ApiTest.java`. Se ejecutan como un programa Java independiente usando `HttpClient` del JDK, sin dependencias adicionales.

Para ejecutarlas, iniciar el servidor y luego ejecutar `ApiTest.main()`. Estas pruebas verifican el funcionamiento
correcto de cada endpoint. **Importante**: la base de datos del servidor de desarrollo se utiliza para las pruebas.
