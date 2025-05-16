# 🎬 MiNetflix

MiNetflix es una plataforma de gestión de videos desarrollada en **Java 17**, utilizando **GlassFish 6.5.2** y **NetBeans 17**.  
Se implementan dos aplicaciones empaquetadas como WAR:

- **Front-end (`MisVids-1.0.war`)**: Interfaz web MVC con páginas JSP, Servlets y lógica de presentación.  
- **Servicio REST (`web-service-1.0.war`)**: API independiente para reproducción y gestión de videos.

---

## ✨ Features

- 📌 **Registro de usuarios** con validaciones en el cliente y servidor.  
- 🔐 **Inicio de sesión** con verificación de credenciales.  
- 📂 **Registro de videos** locales o enlaces embebidos de YouTube.  
- 📋 **Listado de videos** con filtros, paginación y ordenación consumiendo endpoint REST.  
- ▶️ **Reproducción de videos** a través de endpoints REST.  
- 🛡️ **Control de acceso** protegido mediante HttpSession.  
- 🛠️ **Gestión de base de datos** mediante conexión centralizada y ejecutor genérico.

---

## ✅ Validaciones implementadas

## 🔐 Login / Logout
- 🧩 Inputs con límite de caracteres.
- 🔑 Validación de credenciales para iniciar sesión.
- 🧠 Mantenimiento de sesión con HttpSession al hacer login/logout.

## 👤 Registro de usuario
- 📌 Todos los campos son requeridos.
- 🔐 Las contraseñas deben coincidir.
- 📧 El nombre de usuario y correo electrónico deben ser únicos.
- 🧩 Inputs con límite de caracteres.

## 🎥 Registro de videos
- 📌 Todos los campos son requeridos.
- 📆 La fecha del video debe ser anterior a la fecha actual.
- ⚠️ No puede existir un video con el mismo título y autor.
- 🧩 Inputs con límite de caracteres.
- 📁 Tamaño del archivo no debe exceder 50MB.
- 🔗 Si es un enlace de YouTube, debe estar en formato embed: https://www.youtube.com/embed/....

## 🌐 Validaciones generales en páginas
- 🔐 Control de acceso: páginas protegidas mediante HttpSession.
- ❌ Redirección a una página de error personalizada si se accede a una ruta inválida.

---

## 🔗 Endpoints API REST

| Método | Ruta                                | Descripción                                     |
|--------|-------------------------------------|-------------------------------------------------|
| GET    | `/api/videos/{id}`                  | Obtiene los datos de un video por ID.           |
| PUT    | `/api/videos/{id}/views`            | Incrementa contador de reproducciones.          |
| GET    | `/api/videos/{id}/stream`           | Streaming (range) de un video local.            |
| POST   | `/api/videos/search`                | Búsqueda de videos con filtros JSON.            |

### Swagger / OpenAPI

- **UI interactiva**: `/web-service/swagger-ui/index.html`  
- **Spec JSON**: `/web-service/api/openapi.json`

---

## 🗄️ Gestión de base de datos

Se utiliza un **archivo de propiedades** (`DB.properties`) cargado vía `-Dconfig.path` en GlassFish, que contiene:

```properties
db.url=jdbc:derby://localhost:1527/mydb;create=true
db.user=usuario_bd
db.pass=password_bd
videos.path=/ruta/absoluta/al/storage/videos
api.base.url=http://localhost:8080/web-service/api
encryption.key=encryption_key_value_here
```

La aplicación front-end y el servlet proxy (`servletRest`) leen `api.base.url` para invocar a la API REST.

---

## ⚙️ Configuración de GlassFish

En el archivo `glassfish/domains/domain1/config/domain.xml` de GlassFish, agrega la línea:

```xml
<java-config ...>
    <jvm-options>-Dconfig.path=/ruta/a/DB.properties</jvm-options>
</java-config>
```

Luego reinicia el servidor para cargar las propiedades.

---

## 🚀 Tecnologías utilizadas

- **Java 17** ☕  
- **GlassFish 6.5.2** 🐟  
- **NetBeans 17** 🏗️  
- **Derby Database** 🗄️  

---

## 👥 Autores

- Carlos Rodríguez  
- Óliver Chan
