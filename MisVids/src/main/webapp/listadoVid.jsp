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
<html lang="es">
<head>
    <title>Listado de Videos - MiNetflix</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #141414;
            color: #ffffff;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: flex-start;
            min-height: 100vh;
            padding-top: 30px;
        }

        .container {
            background-color: #1f1f1f;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
            width: 90%;
            max-width: 1000px;
            text-align: center;
        }

        h2 {
            font-size: 28px;
            color: #e50914;
            margin-bottom: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
            color: #ffffff;
        }

        table th, table td {
            border: 1px solid #333;
            padding: 10px;
            text-align: left;
        }

        table th {
            background-color: #e50914;
            color: white;
        }

        table tr:nth-child(even) {
            background-color: #2c2c2c;
        }

        table tr:nth-child(odd) {
            background-color: #1f1f1f;
        }

        table tr:hover {
            background-color: #333333;
        }

        .link {
            color: #e50914;
            text-decoration: none;
        }

        .link:hover {
            text-decoration: underline;
        }

        .back-link {
            margin-top: 20px;
            display: inline-block;
            color: #e50914;
            text-decoration: none;
            font-size: 14px;
        }

        .back-link:hover {
            text-decoration: underline;
        }

        td {
            word-break: break-word;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Listado de Videos</h2>

        <table>
            <thead>
                <tr>
                    <th>Título</th>
                    <th>Autor</th>
                    <th>Fecha</th>
                    <th>Duración</th>
                    <th>Reproducciones</th>
                    <th>Descripción</th>
                    <th>Formato</th>
                    <th>Enlace</th>
                </tr>
            </thead>
            <tbody>
            <%
                java.util.List<com.isdcm.minetflix.model.Video> listaVideos = 
                    (java.util.List<com.isdcm.minetflix.model.Video>) request.getAttribute("listaVideos");

                if (listaVideos != null && !listaVideos.isEmpty()) {
                    for (com.isdcm.minetflix.model.Video vid : listaVideos) {
            %>
                <tr>
                    <td><%= vid.getTitulo() %></td>
                    <td><%= vid.getAutor() %></td>
                    <td><%= vid.getFecha() %></td>
                    <td><%= vid.getDuracion() %></td>
                    <td><%= vid.getReproducciones() %></td>
                    <td><%= vid.getDescripcion() %></td>
                    <td><%= vid.getFormato() %></td>
                    <td><a href="<%= vid.getRutavideo() %>" target="_blank" class="link">Ver video</a></td>
                </tr>
            <%
                    }
                } else {
            %>
                <tr>
                    <td colspan="8">No hay videos registrados.</td>
                </tr>
            <%
                }
            %>
            </tbody>
        </table>

        <a href="home.jsp" class="back-link">Volver al menú principal</a>
    </div>
</body>
</html>