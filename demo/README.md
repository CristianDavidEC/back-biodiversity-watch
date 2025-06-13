# Manual de Instalación y Uso - Biodiversity Watch Backend

Este manual proporciona instrucciones detalladas para la instalación, configuración y ejecución del backend de Biodiversity Watch desarrollado en Spring Boot.

## Requisitos Previos

### 1. Software Necesario

- Java JDK 17 o superior
- Maven 3.8.x o superior
- Git
- IDE recomendado: IntelliJ IDEA o Eclipse
- Postman (para pruebas de API)
- MySQL 8.0 o superior

### 2. Verificación de Instalaciones

```bash
# Verificar Java
java -version

# Verificar Maven
mvn -version

# Verificar Git
git --version
```

## 1. Instalación del Entorno

### 1.1 Instalación de Java JDK

1. Descargar JDK 17 desde [Oracle](https://www.oracle.com/java/technologies/downloads/#java17) o [OpenJDK](https://adoptium.net/)
2. Instalar siguiendo el asistente
3. Configurar variables de entorno:
   - `JAVA_HOME`: ruta de instalación del JDK
   - Agregar `%JAVA_HOME%\bin` al PATH

### 1.2 Instalación de Maven

1. Descargar Maven desde [maven.apache.org](https://maven.apache.org/download.cgi)
2. Extraer en una ubicación permanente (ej: `C:\Program Files\Apache\maven`)
3. Configurar variables de entorno:
   - `MAVEN_HOME`: ruta de instalación de Maven
   - Agregar `%MAVEN_HOME%\bin` al PATH

#### Verificación de la instalación

```bash
# Verificar versión de Maven
mvn -version

# Verificar configuración
mvn help:effective-settings
```

#### Configuración del archivo settings.xml

1. Crear/editar archivo en `%USERPROFILE%\.m2\settings.xml`:

```xml
<settings>
    <mirrors>
        <mirror>
            <id>maven-default-http-blocker</id>
            <mirrorOf>external:http:*</mirrorOf>
            <name>Pseudo repository to mirror external repositories initially using HTTP.</name>
            <url>http://0.0.0.0/</url>
            <blocked>true</blocked>
        </mirror>
    </mirrors>
</settings>
```

### 1.3 Configuración de Base de Datos

#### Opción 1: Usando Supabase (Recomendado)

1. Crear cuenta en [Supabase](https://supabase.com)
2. Crear nuevo proyecto
3. Obtener las credenciales de conexión:

   - URL de la base de datos
   - API Key
   - Database Password

4. Configurar variables de entorno en `.env`:

```properties
SUPABASE_URL=tu_url_de_supabase
SUPABASE_KEY=tu_api_key
SUPABASE_DB_PASSWORD=tu_password
```

5. Configurar `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://${SUPABASE_URL}:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=${SUPABASE_DB_PASSWORD}
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

#### Opción 2: PostgreSQL Local

1. Instalar PostgreSQL desde [postgresql.org](https://www.postgresql.org/download/)
2. Crear base de datos:

```sql
CREATE DATABASE biodiversity_watch;
```

3. Configurar `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/biodiversity_watch
spring.datasource.username=postgres
spring.datasource.password=tu_password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### 1.4 Ejecución de Scripts de Base de Datos

#### Para Supabase

1. Instalar Supabase CLI:

```bash
# Windows (usando scoop)
scoop install supabase

# Linux/Mac
brew install supabase/tap/supabase
```

2. Iniciar sesión en Supabase:

```bash
supabase login
```

3. Ejecutar scripts:

```bash
# Conectar a la base de datos
supabase db connect

# Ejecutar script específico
psql -h ${SUPABASE_URL} -U postgres -d postgres -f scripts/init.sql
```

#### Para PostgreSQL Local

1. Ejecutar scripts usando psql:

```bash
# Conectar a la base de datos
psql -U postgres -d biodiversity_watch

# Ejecutar script específico
\i scripts/init.sql
```

2. O usando el comando directo:

```bash
psql -U postgres -d biodiversity_watch -f scripts/init.sql
```

#### Scripts de Base de Datos Disponibles

```bash
# Estructura de directorios
scripts/
├── init.sql           # Script inicial con todas las tablas
├── seed.sql          # Datos iniciales
└── migrations/       # Scripts de migración
    ├── V1__create_tables.sql
    ├── V2__add_indexes.sql
    └── V3__add_constraints.sql
```

#### Comandos Útiles para Base de Datos

```bash
# Verificar conexión
psql -h ${SUPABASE_URL} -U postgres -d postgres -c "\conninfo"

# Listar tablas
psql -h ${SUPABASE_URL} -U postgres -d postgres -c "\dt"

# Ver estructura de una tabla
psql -h ${SUPABASE_URL} -U postgres -d postgres -c "\d nombre_tabla"

# Backup de la base de datos
pg_dump -h ${SUPABASE_URL} -U postgres postgres > backup.sql

# Restaurar backup
psql -h ${SUPABASE_URL} -U postgres -d postgres < backup.sql
```

### 1.5 Dependencias de Base de Datos

```xml
<!-- PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<!-- Supabase -->
<dependency>
    <groupId>io.github.supabase</groupId>
    <artifactId>postgrest-java</artifactId>
    <version>0.1.0</version>
</dependency>
```

### 1.6 Ejecución del Proyecto

#### Método 1: Usando Maven

```bash
# Compilar y ejecutar
mvn spring-boot:run

# Ejecutar con perfil específico
mvn spring-boot:run -Dspring.profiles.active=dev

# Ejecutar con parámetros personalizados
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

#### Método 2: Usando el JAR

```bash
# Compilar el proyecto
mvn clean package

# Ejecutar el JAR
java -jar target/demo-0.0.1-SNAPSHOT.jar

# Ejecutar con perfil específico
java -jar target/demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# Ejecutar con parámetros personalizados
java -jar target/demo-0.0.1-SNAPSHOT.jar --server.port=8081
```

#### Método 3: Usando el IDE

1. Abrir el proyecto en IntelliJ IDEA o Eclipse
2. Localizar la clase principal `DemoApplication.java`
3. Ejecutar como aplicación Java

#### Verificación de la Ejecución

```bash
# Verificar que la aplicación está corriendo
curl http://localhost:8080/api/health

# Verificar logs
tail -f logs/application.log
```

## 2. Configuración del Proyecto

### 2.1 Clonación del Repositorio

#### Requisitos Previos

- Git instalado
- Cuenta de GitHub
- Acceso al repositorio

#### Pasos para Clonar

1. Abrir terminal y navegar al directorio donde se desea clonar:

```bash
cd /ruta/deseada
```

2. Clonar el repositorio:

```bash
git clone https://github.com/CristianDavidEC/back-biodiversity-watch.git
```

3. Navegar al directorio del proyecto:

```bash
cd back-biodiversity-watch/demo
```

4. Verificar la rama actual:

```bash
git branch
```

5. Cambiar a la rama de desarrollo (si es necesario):

```bash
git checkout develop
```

6. Actualizar el repositorio:

```bash
git pull origin develop
```

### 2.2 Configuración Inicial

#### Estructura del Proyecto

```
demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── biodiversity/
│   │   │           └── demo/
│   │   │               ├── controller/
│   │   │               ├── model/
│   │   │               ├── repository/
│   │   │               ├── service/
│   │   │               └── DemoApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-dev.properties
│   └── test/
├── scripts/
│   ├── init.sql
│   └── migrations/
├── pom.xml
└── README.md
```

#### Configuración del IDE

1. Abrir el proyecto en IntelliJ IDEA:

   - File -> Open -> seleccionar el directorio `demo`
   - Importar como proyecto Maven

2. Configurar el JDK:

   - File -> Project Structure -> Project
   - Seleccionar JDK 17
   - Aplicar cambios

3. Configurar Maven:
   - View -> Tool Windows -> Maven
   - Verificar que todas las dependencias se descarguen correctamente

#### Configuración de Variables de Entorno

1. Crear archivo `.env` en la raíz del proyecto:

```properties
# Supabase
SUPABASE_URL=tu_url_de_supabase
SUPABASE_KEY=tu_api_key
SUPABASE_DB_PASSWORD=tu_password

# JWT
JWT_SECRET=tu_clave_secreta
JWT_EXPIRATION=86400000

# Servidor
SERVER_PORT=8080
```

2. Crear archivo `application-dev.properties`:

```properties
# Configuración de desarrollo
spring.profiles.active=dev
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Logging
logging.level.root=INFO
logging.level.com.biodiversity=DEBUG
```

### 2.3 Verificación de la Instalación

1. Compilar el proyecto:

```bash
mvn clean install
```

2. Verificar que no hay errores:

```bash
mvn verify
```

3. Ejecutar los tests:

```bash
mvn test
```

4. Verificar la estructura de la base de datos:

```bash
# Para Supabase
psql -h ${SUPABASE_URL} -U postgres -d postgres -c "\dt"

# Para PostgreSQL local
psql -U postgres -d biodiversity_watch -c "\dt"
```

## 3. Compilación y Ejecución

### 3.1 Compilar el Proyecto

```bash
# Limpiar y compilar
mvn clean install

# Verificar que no hay errores
mvn verify
```

### 3.2 Ejecutar la Aplicación

```bash
# Método 1: Usando Maven
mvn spring-boot:run

# Método 2: Usando el JAR
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

La aplicación estará disponible en:

- URL local: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## 4. Endpoints Principales

### 4.1 Autenticación

```
POST /api/auth/login
POST /api/auth/register
```

### 4.2 Administradores

```
GET    /api/admins
GET    /api/admins/{id}
POST   /api/admins
PATCH  /api/admins/{id}
DELETE /api/admins/{id}
```

### 4.3 Especies

```
GET    /api/species
POST   /api/species
GET    /api/species/{id}
PATCH  /api/species/{id}
DELETE /api/species/{id}
```

### 4.4 Observaciones

```
GET    /api/observations
POST   /api/observations
GET    /api/observations/{id}
PATCH  /api/observations/{id}
DELETE /api/observations/{id}
GET    /api/observations/user/{userId}
GET    /api/observations/species/{speciesId}
GET    /api/observations/area/{areaId}
POST   /api/observations/{id}/verify
GET    /api/observations/verified
GET    /api/observations/pending
```

### 4.6 Usuarios

```
GET    /api/users
POST   /api/users
GET    /api/users/{id}
PATCH  /api/users/{id}
DELETE /api/users/{id}
GET    /api/users/{id}/observations
GET    /api/users/{id}/contributions
```

## 5. Conexión con la Aplicación Móvil

### 5.1 Configuración del Servidor

1. Asegúrate de que el teléfono y la computadora estén en la misma red WiFi
2. Obtén la dirección IP local de tu computadora:
   - Windows: `ipconfig` en CMD
   - Linux/Mac: `ifconfig` en Terminal
3. En la aplicación móvil, configura la URL del servidor como:
   `http://[TU_IP_LOCAL]:8080`

### 5.2 Configuración de CORS

El backend ya está configurado para aceptar peticiones desde la aplicación móvil.

## 6. Solución de Problemas

### 6.1 Errores Comunes

1. **Error de puerto en uso**

   ```bash
   # Cambiar el puerto en application.properties
   server.port=8081
   ```

2. **Error de conexión a la base de datos**

   - Verificar que MySQL esté corriendo
   - Confirmar credenciales en `application.properties`
   - Verificar que la base de datos existe

3. **Error de compilación Maven**
   ```bash
   # Limpiar caché de Maven
   mvn clean
   # Actualizar dependencias
   mvn dependency:purge-local-repository
   ```

### 6.2 Logs y Depuración

- Los logs se encuentran en `logs/application.log`
- Para más detalles, modificar `application.properties`:

```properties
logging.level.root=DEBUG
logging.level.com.biodiversity=DEBUG
```

## 7. Mantenimiento

### 7.1 Actualización de Dependencias

```bash
# Verificar dependencias desactualizadas
mvn versions:display-dependency-updates

# Actualizar dependencias
mvn versions:use-latest-versions
```

### 7.2 Backup de Base de Datos

```bash
# Exportar
mysqldump -u root -p biodiversity_watch > backup.sql

# Importar
mysql -u root -p biodiversity_watch < backup.sql
```

## 8. Recursos Adicionales

- [Documentación de Spring Boot](https://spring.io/projects/spring-boot)
- [Documentación de Maven](https://maven.apache.org/guides/)
- [Documentación de MySQL](https://dev.mysql.com/doc/)

## Contacto y Soporte

Para reportar problemas o solicitar ayuda:

- Crear un issue en el repositorio
- Contactar al equipo de desarrollo

---

**Nota**: Este manual se actualizará periódicamente. Asegúrate de estar usando la última versión.
