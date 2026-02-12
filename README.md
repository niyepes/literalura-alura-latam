## Requerimientos
- Java 17+
- Maven 4+ (o Maven 3.8+)
- PostgreSQL 16+ (o 12+) funcionando localmente o en la red
- Spring Boot 3.x

## Dependencias destacadas
- Spring Boot Starter
- Spring Data JPA
- PostgreSQL driver
- Jackson 2.16

## Estructura
Ver la estructura en el README principal (src/main/java/...).

## Configuración de la BD
1. Crear la base de datos y el usuario (ejemplo con psql):
```sql
CREATE DATABASE gutendex_db;
CREATE USER gutendex_user WITH PASSWORD 'changeme';
GRANT ALL PRIVILEGES ON DATABASE gutendexdb TO gutendex_user;
```

## Ejecución desde la raíz
```
mvn clean package
mvn spring-boot:run

