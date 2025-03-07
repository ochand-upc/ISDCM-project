<%-- 
    Document   : home
    Created on : Mar 7, 2025, 4:01:49 PM
    Author     : alumn
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<%
    if (session.getAttribute("usuarioLogueado") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<html>
<head>
    <title>MiNetflix - Home</title>
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

        .container {
            background-color: #1f1f1f;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
            text-align: center;
        }

        h2 {
            margin: 0 0 20px 0;
            font-size: 28px;
        }

        p {
            font-size: 16px;
            margin-bottom: 20px;
        }

        button {
            background-color: #e50914;
            color: white;
            border: none;
            padding: 10px 20px;
            font-size: 16px;
            margin: 10px;
            cursor: pointer;
            border-radius: 5px;
        }

        button:hover {
            background-color: #f40612;
        }

        form {
            display: inline-block;
            margin: 0;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Bienvenido a MiNetflix</h2>
        <p>Seleccione una opción:</p>

        <form action="registroVid.jsp">
            <button type="submit">➕ Agregar Video</button>
        </form>

        <form action="servletListadoVid">
            <button type="submit">📋 Ver Listado de Videos</button>
        </form>

        <form action="logout.jsp">
            <button type="submit">🚪 Cerrar Sesión</button>
        </form>
    </div>
</body>
</html>