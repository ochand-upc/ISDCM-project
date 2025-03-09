<%-- 
    Document   : registroVid
    Created on : 21 feb 2025, 14:29:58
    Author     : alumne
--%>

<%@ page session="true" %>
<%
    // Comprobamos si existe sesión
    if (session.getAttribute("usuarioLogueado") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<html lang="es">
<head>
    <title>Registro de Videos - MiNetflix</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #141414;
            color: #ffffff;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }

        .registro-container {
            background-color: #1f1f1f;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
            width: 100%;
            max-width: 600px;
            text-align: center;
        }

        h2 {
            margin-bottom: 20px;
            color: #e50914;
            font-size: 24px;
        }

        label {
            display: block;
            margin: 10px 0 5px;
            text-align: left;
            font-weight: bold;
            color: #ffffff;
        }

        input[type="text"],
        input[type="date"],
        input[type="number"],
        textarea,
        select {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #333;
            border-radius: 5px;
            background-color: #333;
            color: #fff;
            box-sizing: border-box;
        }

        textarea {
            height: 80px;
        }

        input[type="submit"] {
            background-color: #e50914;
            color: white;
            border: none;
            padding: 12px 15px;
            cursor: pointer;
            border-radius: 5px;
            font-size: 16px;
            width: 100%;
        }

        input[type="submit"]:hover {
            background-color: #b20710;
        }

        p {
            margin-top: 15px;
        }

        .error-message {
            color: #ff4c4c;
            margin-top: 10px;
        }

        .success-message {
            color: #4caf50;
            margin-top: 10px;
        }

        .back-link {
            margin-top: 15px;
            display: inline-block;
            color: #e50914;
            text-decoration: none;
            font-size: 14px;
        }

        .back-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="registro-container">
        <h2>Registrar nuevo video</h2>
        <form action="servletRegistroVid" method="post">
            <label>Título:</label>
            <input type="text" name="titulo" required />

            <label>Autor:</label>
            <input type="text" name="autor" required />

            <label>Fecha:</label>
            <input type="date" name="fecha" required />
            
            <label>Ruta</label>
            <input type="text" name="rutaVideo" placeholder="/path_to_video/video.mp4" required />

            <label>Duración:</label>
            <input type="text" name="duracion" placeholder="Ej. 2h 15min" required />

            <label>Reproducciones:</label>
            <input type="number" name="reproducciones" min="0" value="0" required />

            <label>Descripción:</label>
            <textarea name="descripcion" placeholder="Añade una breve descripción del video"></textarea>

            <label>Formato:</label>
            <select name="formato" required>
                <option value="">Seleccione un formato</option>
                <option value="MP4">MP4</option>
                <option value="AVI">AVI</option>
                <option value="MKV">MKV</option>
                <option value="MOV">MOV</option>
                <option value="WMV">WMV</option>
                <option value="FLV">FLV</option>
            </select>

            <input type="submit" value="Registrar Video" />
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

        <a href="home.jsp" class="back-link">Volver al menú principal</a>
    </div>
</body>
</html>