<%-- 
    Document   : registroUsu
    Created on : 21 feb 2025, 14:29:31
    Author     : alumne
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("usuarioLogueado") != null) {
        response.sendRedirect("home.jsp");
        return;
    }
%>
<html lang="es">
<head>
    <title>Registro de usuarios - MiNetflix</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #141414;
            color: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        .registro-container {
            background-color: #1f1f1f;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
            text-align: center;
            width: 100%;
            max-width: 500px;
        }

        h2 {
            font-size: 28px;
            margin-bottom: 20px;
        }

        label {
            display: block;
            text-align: left;
            font-weight: bold;
            margin: 10px 0 5px;
            color: #ffffff;
        }

        input[type="text"],
        input[type="email"],
        input[type="password"] {
            width: calc(100% - 20px);
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #333;
            border-radius: 5px;
            background-color: #333;
            color: #fff;
        }

        input[type="submit"] {
            background-color: #e50914;
            color: white;
            border: none;
            padding: 10px 20px;
            font-size: 16px;
            cursor: pointer;
            border-radius: 5px;
            width: 100%;
        }

        input[type="submit"]:hover {
            background-color: #f40612;
        }

        p {
            margin-top: 15px;
            font-size: 14px;
        }

        a {
            color: #e50914;
            text-decoration: none;
        }

        a:hover {
            text-decoration: underline;
        }

        .error-message {
            color: #ff4d4d;
            margin-top: 10px;
        }

        .success-message {
            color: #28a745;
            margin-top: 10px;
        }
    </style>
</head>
<body>
    <div class="registro-container">
        <h2>Registro de usuarios</h2>
        <form action="servletUsuarios" method="post">
            <input type="hidden" name="accion" value="registrar" />

            <label>Nombre:</label>
            <input type="text" name="nombre" required />

            <label>Apellidos:</label>
            <input type="text" name="apellidos" required />

            <label>Correo electrónico:</label>
            <input type="email" name="email" required />

            <label>Nombre de usuario:</label>
            <input type="text" name="username" required />

            <label>Contraseña:</label>
            <input type="password" name="password" required />

            <label>Repetir contraseña:</label>
            <input type="password" name="password2" required />

            <input type="submit" value="Registrar usuario" />
        </form>

        <% if (request.getAttribute("mensajeError") != null) { %>
            <p class="error-message">
                <%= request.getAttribute("mensajeError") %>
            </p>
        <% } %>

        <% if (request.getAttribute("mensajeExito") != null) { %>
            <p class="success-message">
                <%= request.getAttribute("mensajeExito") %>
            </p>
        <% } %>

        <p><a href="login.jsp">¿Ya tienes cuenta? Inicia sesión aquí</a></p>
    </div>
</body>
</html>