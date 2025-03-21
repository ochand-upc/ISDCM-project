# 🎬 MiNetflix

MiNetflix es una plataforma de gestión de videos desarrollada en **Java 17**, utilizando **GlassFish 6.5.2** y **NetBeans 17**.  
Se implementa el patrón **Modelo-Vista-Controlador (MVC)**, con **DAO** para la gestión de la base de datos.

---

## ✨ Features

- 📌 **Registro de usuarios** con validaciones en el cliente y servidor.
- 🔐 **Inicio de sesión** con verificación de credenciales.
- 📂 **Registro de videos** asegurando consistencia de datos.
- 📋 **Listado de videos** disponibles en la plataforma.
- 🛡️ **Filtros de acceso** para proteger las páginas de usuarios autenticados.
- 🛠️ **Gestión de base de datos** mediante una conexión centralizada y un ejecutor de consultas.

---

## ✅ Validaciones implementadas

### 🧑‍💻 Registro de usuario
- Todos los campos son **requeridos**.
- Las contraseñas deben **coincidir**.
- El **nombre de usuario** y **correo electrónico** no deben existir previamente.

### 🎥 Registro de videos
- Todos los campos son **requeridos**.
- La **fecha del video** debe ser **anterior** a la fecha actual.
- No puede existir un video con el **mismo título y autor**.

---

## 🔗 Endpoints disponibles
- `/registroVid` ➝ Registro de videos
- `/login` ➝ Inicio de sesión
- `/home` ➝ Página de inicio
- `/listadoVid` ➝ Listado de videos
- `/notFoundPage` ➝ Página de error 404

---

## 🗄️ Gestión de base de datos
Se implementa una **clase de conexión centralizada** (`DatabaseConnection`) y un **ejecutor de consultas** (`DatabaseExecutor`), utilizados por los DAO para la interacción con la base de datos.

📌 *En futuras versiones, se planea almacenar los archivos de video en la base de datos.*

---

## 🚀 Tecnologías utilizadas
- **Java 17** ☕
- **GlassFish 6.5.2** 🐟
- **NetBeans 17** 🏗️
- **Derby Database** 🗄️

## ⚙️ Configuración de Variables de Entorno
Para el funcionamiento correcto de la aplicación es necesario establecer una variable de entorno en la que se especifica la ruta absoluta del archivo de conexión a la base de datos. 
1. Ubica el archivo de configuración domain.xml, localizado normalmente en glassfish/domains/domain1/config/
2. Coloca el archivo DB.properties en la ruta deseada. Debe contener lo siguiente:
> db.url=jdbc:derby://localhost:1527/BASEDEDATOS;create=true
> 
> db.user=usuario_bd
> 
> db.pass=password_bd
>
> videos.path=/ruta/absoluta/en/tu/SO/a/la/ubicacion/
3. Agrega la siguiente línea al archivo:
> <java-config ...>
> 
>     <jvm-options>-Dconfig.path=/ruta/absoluta/en/tu/SO/a/DB.properties</jvm-options>
> 
> <\/java-config> 
4. Reinicia el servidor GlassFish

---

## 👥 Autores
- **Carlos Rodríguez**
- **Óliver Chan**