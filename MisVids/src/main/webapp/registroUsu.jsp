<%-- 
    Document   : registroUsu
    Created on : 21 feb 2025, 14:29:31
    Author     : alumne
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Registro de usuarios</title>
</head>
<body>
    <h2>Registro de usuarios</h2>
    <form action="servletUsuarios" method="post">
        <input type="hidden" name="accion" value="registrar" />
        
        <label>Nombre:</label>
        <input type="text" name="nombre" /><br/><br/>
        
        <label>Apellidos:</label>
        <input type="text" name="apellidos" /><br/><br/>
        
        <label>Correo electrónico:</label>
        <input type="email" name="email" /><br/><br/>
        
        <label>Nombre de usuario:</label>
        <input type="text" name="username" /><br/><br/>
        
        <label>Contraseña:</label>
        <input type="password" name="password" /><br/><br/>
        
        <label>Repetir contraseña:</label>
        <input type="password" name="password2" /><br/><br/>
        
        <input type="submit" value="Registrar usuario" />
    </form>

    <!-- Mostrar mensajes de error o éxito -->
    <p style="color:red;">
        <%= (request.getAttribute("mensajeError") != null) ? request.getAttribute("mensajeError") : "" %>
    </p>
    <p style="color:green;">
        <%= (request.getAttribute("mensajeExito") != null) ? request.getAttribute("mensajeExito") : "" %>
    </p>
</body>
</html>
