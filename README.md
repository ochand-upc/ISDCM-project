# 🎬 MiNetflix - Plataforma de Gestión de Videos

MiNetflix es una plataforma completa de gestión y streaming de videos desarrollada en **Java 17**, utilizando **GlassFish 6.5.2** como servidor de aplicaciones. La aplicación está estructurada en dos módulos independientes que trabajan en conjunto:

- **🖥️ Frontend (`MisVids-1.0.war`)**: Interfaz web MVC con JSP, Servlets y control de sesiones
- **🔌 Backend API (`web-service-1.0.war`)**: Servicio REST independiente para gestión y streaming de videos

---

## 📋 Tabla de Contenidos

- [✨ Características Principales](#-características-principales)
- [🛠️ Requisitos Previos](#️-requisitos-previos)
- [🚀 Instalación y Configuración](#-instalación-y-configuración)
- [⚙️ Configuración de la Base de Datos](#️-configuración-de-la-base-de-datos)
- [🔧 Configuración de GlassFish](#-configuración-de-glassfish)
- [▶️ Ejecución de la Aplicación](#️-ejecución-de-la-aplicación)
- [📖 Guía de Uso](#-guía-de-uso)
- [🔗 API REST](#-api-rest)
- [🏗️ Estructura del Proyecto](#️-estructura-del-proyecto)
- [🔒 Seguridad](#-seguridad)
- [🐛 Troubleshooting](#-troubleshooting)
- [👥 Autores](#-autores)

---

## ✨ Características Principales

### 🔐 **Gestión de Usuarios**
- Registro de usuarios con validaciones completas
- Sistema de login/logout con control de sesiones HTTP
- Validación de unicidad de usuario y correo electrónico
- Protección de rutas mediante filtros de sesión

### 🎥 **Gestión de Videos**
- **Subida de archivos locales**: Soporte para videos MP4 (máx. 50MB)
- **Enlaces de YouTube**: Integración con videos embebidos
- **Encriptación AES**: Videos locales se almacenan cifrados
- **Streaming con rangos**: Reproducción eficiente con soporte HTTP Range
- **Metadatos cifrados**: Exportación/importación de metadatos en XML cifrado

### 📊 **Funcionalidades Avanzadas**
- **Búsqueda y filtros**: Por título, autor, fecha con paginación
- **Ordenación dinámica**: Por cualquier campo (título, fecha, vistas, etc.)
- **Contador de reproducciones**: Registro automático de visualizaciones
- **Interfaz responsive**: Diseño moderno con Bootstrap 5
- **Manejo de errores inteligente**: Retroalimentación específica al usuario

### 🛡️ **Seguridad y Calidad**
- **Autenticación JWT**: Para comunicación entre frontend y API
- **Encriptación AES-256**: Para archivos de video sensibles
- **Validaciones robustas**: Cliente y servidor
- **Manejo de errores específicos**: Mensajes claros para el usuario
- **Codificación UTF-8**: Soporte completo para caracteres especiales

---

## 🛠️ Requisitos Previos

### **Software Necesario:**

| Componente | Versión Mínima | Recomendada | Enlace de Descarga |
|------------|----------------|-------------|-------------------|
| **Java JDK** | 17 | 17+ | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) |
| **GlassFish Server** | 6.5.2 | 6.5.2+ | [Eclipse GlassFish](https://glassfish.org/download) |
| **NetBeans IDE** | 17 | 18+ | [NetBeans](https://netbeans.apache.org/download/) |
| **Apache Derby** | 10.14+ | 10.16+ | Incluido con GlassFish |

### **Recursos del Sistema:**
- **RAM**: Mínimo 4GB, recomendado 8GB+
- **Almacenamiento**: 2GB libres (videos + base de datos)
- **Puertos**: 8080 (GlassFish), 1527 (Derby)

---

## 🚀 Instalación y Configuración

### **1. Preparación del Entorno**

```bash
# Verificar instalación de Java
java -version
# Debe mostrar: java version "17.x.x" o superior

# Configurar JAVA_HOME (si no está configurado)
export JAVA_HOME=/path/to/jdk-17
export PATH=$JAVA_HOME/bin:$PATH
```

### **2. Configuración de GlassFish**

```bash
# Descomprimir GlassFish
unzip glassfish-6.5.2.zip
cd glassfish6

# Iniciar GlassFish
./bin/asadmin start-domain domain1

# Verificar que funciona
# Navegar a: http://localhost:8080
```

### **3. Clonar el Proyecto**

```bash
git clone <repository-url>
cd ISDCM-project
```

### **4. Crear Estructura de Directorios**

```bash
# Crear directorio para videos (ajustar la ruta según tu sistema)
mkdir -p /path/to/video/storage
chmod 755 /path/to/video/storage

# Crear directorio para configuración
mkdir -p /path/to/config
```

---

## ⚙️ Configuración de la Base de Datos

### **1. Configurar Apache Derby**

```bash
# Iniciar Derby Network Server (si no está iniciado)
$GLASSFISH_HOME/javadb/bin/startNetworkServer &

# Verificar que Derby está funcionando
netstat -ln | grep 1527
```

### **2. Crear Base de Datos**

```sql
-- Ejecutar el script SQL incluido
-- Ubicación: DB/GENERATE_DB.sql

-- Conectar a Derby
connect 'jdbc:derby://localhost:1527/minetflix;create=true';

-- Ejecutar el contenido de GENERATE_DB.sql
-- (Crear tablas USUARIOS y VIDEOS)
```

### **3. Archivo de Configuración**

Crear `/path/to/config/DB.properties`:

```properties
# Configuración de Base de Datos
db.url=jdbc:derby://localhost:1527/minetflix;create=true
db.user=tu_usuario
db.pass=tu_password

# Rutas del Sistema
videos.path=/path/to/video/storage
config.path=/path/to/config

# URLs de Servicios
api.base.url=http://localhost:8080/web-service/api
web-service.url=http://localhost:8080/web-service/api

# Claves de Seguridad (CAMBIAR EN PRODUCCIÓN)
encryption.key=mi_clave_secreta_32_caracteres_exactos
jwt.secret=mi_jwt_secret_clave_para_tokens_seguros
```

---

## 🔧 Configuración de GlassFish

### **1. Configurar JVM Options**

```bash
# Detener GlassFish
./bin/asadmin stop-domain domain1

# Editar domain.xml
nano domains/domain1/config/domain.xml
```

Agregar en la sección `<java-config>`:

```xml
<java-config ...>
    <!-- Opciones JVM existentes -->
    <jvm-options>-Dconfig.path=/path/to/config/DB.properties</jvm-options>
    <jvm-options>-Dfile.encoding=UTF-8</jvm-options>
    <jvm-options>-Djava.awt.headless=true</jvm-options>
    <jvm-options>-Xmx2048m</jvm-options>
    <jvm-options>-Xms512m</jvm-options>
</java-config>
```

### **2. Configurar Connection Pool (Opcional)**

```bash
# Iniciar GlassFish
./bin/asadmin start-domain domain1

# Crear Connection Pool
./bin/asadmin create-jdbc-connection-pool \
    --datasourceclassname org.apache.derby.jdbc.ClientDataSource \
    --restype javax.sql.DataSource \
    --property portNumber=1527:password=tu_password:user=tu_usuario:serverName=localhost:databaseName=minetflix:connectionAttributes=\\;create\\=true \
    DerbyPool

# Crear JDBC Resource
./bin/asadmin create-jdbc-resource --connectionpoolid DerbyPool jdbc/minetflix
```

---

## ▶️ Ejecución de la Aplicación

### **1. Compilar el Proyecto**

```bash
# Opción A: Usar NetBeans
# 1. Abrir NetBeans
# 2. File > Open Project > Seleccionar MisVids y web-service
# 3. Botón derecho > Clean and Build en ambos proyectos

# Opción B: Usar Maven (si está configurado)
cd MisVids
mvn clean package

cd ../web-service  
mvn clean package
```

### **2. Desplegar en GlassFish**

```bash
# Asegurar que GlassFish está ejecutándose
./bin/asadmin start-domain domain1

# Desplegar ambas aplicaciones
./bin/asadmin deploy /path/to/ISDCM-project/MisVids/target/MisVids-1.0.war
./bin/asadmin deploy /path/to/ISDCM-project/web-service/target/web-service-1.0.war

# Verificar despliegue
./bin/asadmin list-applications
```

### **3. Verificar Funcionamiento**

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **Frontend** | http://localhost:8080/MisVids-1.0 | Interfaz principal |
| **API REST** | http://localhost:8080/web-service/api | Endpoints REST |
| **Swagger UI** | http://localhost:8080/web-service/swagger-ui/index.html | Documentación API |
| **GlassFish Admin** | http://localhost:4848 | Panel de administración |

---

## 📖 Guía de Uso

### **🔐 1. Primer Acceso**

1. **Navegar a**: http://localhost:8080/MisVids-1.0
2. **Crear cuenta**: Hacer clic en "Registrarse"
3. **Completar formulario**:
   - Usuario único (3-50 caracteres)
   - Correo electrónico válido
   - Contraseña (mínimo 6 caracteres)
   - Confirmar contraseña
4. **Iniciar sesión** con las credenciales creadas

### **🎥 2. Gestión de Videos**

#### **Subir Video Local:**
1. **Ir a**: "Registrar nuevo video"
2. **Completar información**:
   - Título único para el autor
   - Nombre del autor
   - Fecha (no puede ser futura)
   - Descripción (opcional)
3. **Seleccionar**: "Subir archivo"
4. **Elegir archivo**: MP4 (máx. 50MB)
5. **Hacer clic**: "Registrar Video"

#### **Agregar Video de YouTube:**
1. **Seguir pasos 1-3** anteriores
2. **Seleccionar**: "Enlace de YouTube"
3. **Pegar URL**: Formato `https://www.youtube.com/embed/VIDEO_ID`
4. **Registrar video**

### **📋 3. Navegación y Búsqueda**

#### **Listado de Videos:**
- **Filtros disponibles**:
  - Por título (búsqueda parcial)
  - Por autor (búsqueda parcial)  
  - Por fecha (año, mes, día)
- **Ordenación**: Clic en encabezados de columna
- **Paginación**: 5 videos por página

#### **Reproducción:**
1. **Hacer clic**: "Ver video" en el listado
2. **El video se carga automáticamente**
3. **En caso de error**: Mensaje específico y opciones de acción

### **🔒 4. Metadatos Cifrados**

#### **Exportar Metadatos:**
1. **En la página del video**: Clic "Descargar metadatos cifrados"
2. **Se descarga**: archivo `.xml.enc` cifrado

#### **Importar Metadatos:**
1. **Seleccionar archivo**: `.xml` o `.enc`
2. **Clic**: "Ver metadatos desencriptados"
3. **Se abre**: nueva pestaña con XML legible

---

## 🔗 API REST

### **🎯 Endpoints Principales**

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|---------------|
| `GET` | `/api/videos/{id}` | Obtener información de video | JWT |
| `GET` | `/api/videos/{id}/stream` | Streaming de video | JWT |
| `HEAD` | `/api/videos/{id}/stream` | Verificar disponibilidad | JWT |
| `PUT` | `/api/videos/{id}/views` | Incrementar reproducciones | JWT |
| `POST` | `/api/videos/search` | Buscar videos con filtros | JWT |
| `GET` | `/api/videos/{id}/metadata/encrypted` | Descargar metadatos cifrados | JWT |
| `POST` | `/api/videos/{id}/metadata/decrypt` | Descifrar metadatos | JWT |

### **📝 Ejemplos de Uso**

#### **Buscar Videos:**
```bash
curl -X POST http://localhost:8080/web-service/api/videos/search \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "titulo": "mi video",
    "autor": "usuario",
    "page": 1,
    "pageSize": 5,
    "sortField": "fecha",
    "sortOrder": "desc"
  }'
```

#### **Streaming con Rangos:**
```bash
curl -H "Range: bytes=0-1023" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     http://localhost:8080/web-service/api/videos/1/stream
```

### **📚 Documentación Swagger**

- **Interfaz Interactiva**: http://localhost:8080/web-service/swagger-ui/index.html
- **Especificación OpenAPI**: http://localhost:8080/web-service/api/openapi.json

---

## 🏗️ Estructura del Proyecto

```
ISDCM-project/
├── 📁 MisVids/                    # Frontend Web (WAR)
│   ├── 📁 src/main/java/
│   │   └── 📁 com/isdcm/minetflix/
│   │       ├── 📁 controladores/  # Servlets
│   │       ├── 📁 dao/           # Acceso a datos
│   │       ├── 📁 model/         # Modelos de datos
│   │       ├── 📁 security/      # Servicios de encriptación
│   │       ├── 📁 utils/         # Utilidades
│   │       └── 📁 filtros/       # Filtros HTTP
│   └── 📁 src/main/webapp/
│       ├── 📄 *.jsp             # Páginas JSP
│       ├── 📁 css/              # Estilos CSS
│       └── 📁 WEB-INF/          # Configuración web
│
├── 📁 web-service/               # Backend API REST (WAR)
│   ├── 📁 src/main/java/
│   │   └── 📁 com/isdcm/
│   │       ├── 📁 web/service/   # Recursos REST
│   │       ├── 📁 manager/       # Lógica de negocio
│   │       ├── 📁 dao/          # Acceso a datos
│   │       ├── 📁 model/        # Modelos de datos
│   │       ├── 📁 security/     # JWT y encriptación
│   │       └── 📁 utils/        # Utilidades
│   └── 📁 src/main/webapp/      # Configuración web
│
├── 📁 DB/                       # Scripts de base de datos
│   └── 📄 GENERATE_DB.sql       # Script de creación
│
├── 📁 Meta/                     # Documentación del proyecto
│   ├── 📄 *.md                 # Especificaciones
│   └── 📁 images/              # Imágenes de documentación
│
├── 📁 videos/                   # Almacenamiento de videos (cifrados)
├── 📁 config/                  # Archivos de configuración
│   └── 📄 DB.properties        # Configuración principal
│
├── 📄 README.md                # Este archivo
└── 📄 *.md                     # Documentación adicional
```

---

## 🔒 Seguridad

### **🛡️ Medidas Implementadas**

1. **Autenticación y Autorización**:
   - Sesiones HTTP para frontend
   - JWT tokens para API REST
   - Filtros de seguridad en todas las rutas protegidas

2. **Encriptación de Datos**:
   - Videos almacenados con cifrado AES-256
   - Metadatos exportables en formato XML cifrado
   - Claves configurables vía archivos de propiedades

3. **Validaciones de Entrada**:
   - Sanitización de inputs en cliente y servidor
   - Límites de tamaño para archivos (50MB)
   - Validación de formatos (MP4, URLs de YouTube)

4. **Manejo de Errores**:
   - No exposición de información sensible
   - Logs detallados para administradores
   - Mensajes amigables para usuarios

### **🔧 Configuración de Seguridad**

**Cambiar claves por defecto en `DB.properties`:**
```properties
# Generar clave AES-256 (32 caracteres)
encryption.key=tu_clave_aes_256_de_32_caracteres

# Generar secreto JWT fuerte
jwt.secret=tu_secreto_jwt_muy_seguro_y_largo
```

**Configurar HTTPS en producción:**
```bash
# En GlassFish para producción
./bin/asadmin create-ssl --type network-listener --certname s1as network-listener-name
```

---

## 🐛 Troubleshooting

### **❗ Problemas Comunes**

#### **1. Error: "Puerto 8080 en uso"**
```bash
# Verificar qué proceso usa el puerto
lsof -i :8080

# Cambiar puerto de GlassFish
./bin/asadmin set server-config.network-config.network-listeners.network-listener.http-listener-1.port=8081
```

#### **2. Error: "No se puede conectar a Derby"**
```bash
# Verificar estado de Derby
netstat -ln | grep 1527

# Reiniciar Derby
$GLASSFISH_HOME/javadb/bin/stopNetworkServer
$GLASSFISH_HOME/javadb/bin/startNetworkServer
```

#### **3. Error: "Video no se puede reproducir"**
- ✅ **Verificar**: Archivo existe en `videos.path`
- ✅ **Verificar**: Permisos de lectura en directorio
- ✅ **Verificar**: Clave de cifrado correcta
- ✅ **Ver logs**: GlassFish para detalles específicos

#### **4. Error: "Caracteres especiales mal mostrados"**
- ✅ **Verificar**: Codificación UTF-8 en JSP
- ✅ **Verificar**: `file.encoding=UTF-8` en JVM options
- ✅ **Verificar**: Base de datos con charset UTF-8

#### **5. Error: "JWT Token inválido"**
```bash
# Verificar configuración
grep jwt.secret /path/to/config/DB.properties

# Limpiar sesiones navegador
# Developer Tools > Application > Clear Storage
```

### **📊 Logs y Debugging**

#### **Ubicaciones de Logs:**
```bash
# Logs de GlassFish
tail -f $GLASSFISH_HOME/domains/domain1/logs/server.log

# Logs de aplicación (buscar mensajes específicos)
grep "Error en streaming" server.log
grep "Video no encontrado" server.log
```

#### **Activar Debug:**
```properties
# En DB.properties para más logs
debug.enabled=true
log.level=DEBUG
```

### **🔍 Verificación de Estado**

#### **Healthcheck Rápido:**
```bash
# 1. Verificar GlassFish
curl http://localhost:8080

# 2. Verificar Derby
telnet localhost 1527

# 3. Verificar API
curl http://localhost:8080/web-service/api/openapi.json

# 4. Verificar aplicaciones desplegadas
./bin/asadmin list-applications
```

---

## 📈 Características Avanzadas

### **🎯 Últimas Mejoras Implementadas**

#### **Manejo Inteligente de Errores (v1.2):**
- ✅ Verificación preventiva de videos antes de reproducir
- ✅ Mensajes específicos según tipo de error (404, corrupto, no cifrado)
- ✅ Interfaz visual de error con opciones de acción
- ✅ Toasts informativos en tiempo real

#### **Soporte Completo UTF-8 (v1.1):**
- ✅ Codificación correcta en todos los archivos JSP
- ✅ Soporte para acentos y caracteres especiales
- ✅ Configuración JVM para UTF-8

#### **Mejoras de UX:**
- ✅ Metadatos se abren en nueva pestaña
- ✅ Timeouts inteligentes para carga de videos
- ✅ Feedback visual durante operaciones

### **🚀 Próximas Funcionalidades**

- 📱 **App móvil**: Cliente nativo para iOS/Android
- 🎬 **Streaming adaptativo**: Calidad según ancho de banda
- 👥 **Colaboración**: Compartir videos entre usuarios
- 📊 **Analytics**: Dashboard de estadísticas de uso
- 🔄 **Sincronización**: Backup automático en la nube

---

## 👥 Autores

| Desarrollador | Rol | Contacto |
|---------------|-----|----------|
| **Carlos Rodríguez** | Backend Developer & Database | carlos.andres.rodriguez.torres@estudiantat.upc.edu |
| **Óliver Chan** | Frontend Developer & UX/UI | oliver.eduardo.chan@estudiantat.upc.edu |

### **🤝 Contribuciones**

Las contribuciones son bienvenidas. Por favor:

1. **Fork** el repositorio
2. **Crear** una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. **Commit** tus cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. **Push** a la rama (`git push origin feature/nueva-funcionalidad`)
5. **Abrir** un Pull Request

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

---

## 🏷️ Versión

**Versión Actual**: 1.2.0  
**Fecha**: Mayo 2025  
**Estado**: Estable para producción

---

<div align="center">

**🎬 ¡Disfruta gestionando tus videos con MiNetflix! 🎬**

*Una plataforma moderna, segura y fácil de usar para todos tus contenidos audiovisuales.*

</div>
