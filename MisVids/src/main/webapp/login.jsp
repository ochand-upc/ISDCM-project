<%-- 
    Document   : login
    Created on : 21 feb 2025, 14:28:38
    Author     : alumne
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Login</title>
    </head>
    <body>
        <h2>Login</h2>
        <form action="servletUsuarios" method="post">
            <input type="hidden" name="accion" value="login" />
            <label>Username:</label>
            <input type="text" name="username" /><br/><br/>
            <label>Password:</label>
            <input type="password" name="password" /><br/><br/>
            <input type="submit" value="Iniciar sesión" />
        </form>

        <p>¿No tienes cuenta? <a href="registroUsu.jsp">Regístrate aquí</a></p>

        <!-- Aquí podrías mostrar mensajes de error o éxito -->
        <p style="color:red;">
            <%= (request.getAttribute("mensajeError") != null) ? request.getAttribute("mensajeError") : "" %>
        </p>
    </body>
</html>

