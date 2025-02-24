<%-- 
    Document   : registroVid
    Created on : 21 feb 2025, 14:29:58
    Author     : alumne
--%>

<%@ page session="true" %>
<%
    // Comprobamos si existe sesi�n
    if (session.getAttribute("usuarioLogueado") == null) {
        // Redirigir a login si no est� logueado
        response.sendRedirect("login.jsp");
        return;
    }
%>
<html>
<head>
    <title>Registro de V�deos</title>
</head>
<body>
    <h2>Registrar nuevo v�deo</h2>
    <form action="servletRegistroVid" method="post">
        <label>T�tulo:</label>
        <input type="text" name="titulo" /><br/><br/>

        <label>Autor:</label>
        <input type="text" name="autor" /><br/><br/>

        <label>Fecha:</label>
        <input type="date" name="fecha" /><br/><br/>

        <label>Duraci�n:</label>
        <input type="text" name="duracion" /><br/><br/>

        <label>Reproducciones:</label>
        <input type="number" name="reproducciones" /><br/><br/>

        <label>Descripci�n:</label>
        <textarea name="descripcion"></textarea><br/><br/>

        <label>Formato:</label>
        <input type="text" name="formato" /><br/><br/>

        <input type="submit" value="Registrar V�deo" />
    </form>

    <p style="color:red;">
        <%= (request.getAttribute("mensajeError") != null) ? request.getAttribute("mensajeError") : "" %>
    </p>
    <p style="color:green;">
        <%= (request.getAttribute("mensajeExito") != null) ? request.getAttribute("mensajeExito") : "" %>
    </p>
</body>
</html>

