<%-- 
    Document   : listadoVid
    Created on : 21 feb 2025, 14:30:20
    Author     : alumne
--%>

<%@ page session="true" %>
<%
    if (session.getAttribute("usuarioLogueado") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<html>
<head>
    <title>Listado de Vídeos</title>
</head>
<body>
    <h2>Listado de Vídeos</h2>
    <%
        // Suponiendo que en el request se haya enviado una lista de videos
        java.util.List<com.isdcm.minetflix.model.Video> listaVideos = (java.util.List<com.isdcm.minetflix.model.Video>) request.getAttribute("listaVideos");
        if (listaVideos != null) {
            for (com.isdcm.minetflix.model.Video vid : listaVideos) {
                out.println("<p>");
                out.println("Título: " + vid.getTitulo() + "<br/>");
                out.println("Autor: " + vid.getAutor() + "<br/>");
                out.println("Fecha: " + vid.getFecha() + "<br/>");
                out.println("Duración: " + vid.getDuracion() + "<br/>");
                out.println("Reproducciones: " + vid.getReproducciones() + "<br/>");
                out.println("Descripción: " + vid.getDescripcion() + "<br/>");
                out.println("Formato: " + vid.getFormato() + "<br/>");
                out.println("Elance: " + vid.getRutavideo() + "<br/>");
                out.println("</p><hr/>");
            }
        }
    %>
</body>
</html>

