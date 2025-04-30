<%-- 
    Document   : listadoVid
    Created on : 21 feb 2025, 14:30:20
    Author     : alumne
--%>

<%@ page session="true" contentType="text/html; charset=UTF-8" %>
<%
    // Control de sesión igual que antes
    if (session.getAttribute("usuarioLogueado") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Listado de Videos - MiNetflix</title>
  <!-- Bootstrap 5 CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" 
        rel="stylesheet" 
        integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" 
        crossorigin="anonymous">
  <link rel="stylesheet" href="css/listadoVid.css">
</head>
<body>
  <div class="container">
        <h2>Listado de Videos</h2>

    <!-- Formulario de filtros -->
    <form id="filterForm" class="row g-2 mb-3">
      <div class="col-md">
        <input id="fTitulo" type="text" class="form-control" placeholder="Título">
      </div>
      <div class="col-md">
        <input id="fAutor" type="text" class="form-control" placeholder="Autor">
      </div>
      <div class="col-md">
        <input id="fFecha" type="date" class="form-control">
      </div>
      <div class="col-auto">
        <button type="submit" class="btn btn-success">Filtrar</button>
        <button id="btnReset" type="button" class="btn btn-secondary">Mostrar todo</button>
      </div>
    </form>

    <!-- Tabla de resultados -->
    <div>
      <table id="videosTable">
        <thead>
          <tr>
            <th>Título</th><th>Autor</th><th>Fecha</th><th>Duración</th>
            <th>Reproducciones</th><th>Descripción</th><th>Formato</th>
            <th>Tamaño</th><th>Enlace</th>
          </tr>
        </thead>
        <tbody>
          <!-- Se inyectan filas aquí -->
        </tbody>
      </table>
    </div>

    <a href="home.jsp" class="back-link">Volver al menú principal</a>
  </div>

  <!-- Bootstrap + nuestro script -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
          integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
          crossorigin="anonymous"></script>
  <script>

    function renderTable(videos) {
        const tbody = document.querySelector('#videosTable tbody');
        tbody.innerHTML = '';
        if (videos.length === 0) {
          tbody.innerHTML = `<tr><td colspan="9" class="text-center">No hay videos registrados.</td></tr>`;
          return;
        }
        for (const v of videos) {
          const row = document.createElement('tr');
          row.innerHTML = 
            "<td>"+v.titulo+"</td>"+
            "<td>"+v.autor+"</td>"+
            "<td>"+v.fecha+"</td>"+
            "<td>"+v.duracion+"</td>"+
            "<td>"+v.reproducciones+"</td>"+
            "<td>"+v.descripcion+"</td>"+
            "<td>"+v.mimeType+"</td>"+
            "<td>"+v.tamano+"</td>"+
            "<td>"+
              "<a href="+"servletVerVideo?id="+v.id+" "+
                 "class="+"'link'>"+
                 "Ver video"+
              "</a>"+
            "</td>";
          tbody.appendChild(row);
        }
    }

    // Llama al endpoint POST /search con filtros y actualiza la tabla
    async function fetchVideos(filters) {
      try {
        const res = await fetch('/web-service/api/videos/search', {
          method: 'POST',
          headers: {
            'Content-Type':'application/json',
            'Accept':'application/json'
          },
          body: JSON.stringify(filters)
        });
        if (!res.ok) throw new Error(res.statusText);
        const data = await res.json();
        renderTable(data);
      } catch (e) {
        console.error('Error al cargar videos:', e);
      }
    }

    document.addEventListener('DOMContentLoaded', () => {
      // carga inicial sin filtros
      fetchVideos({});

      // manejar submit de filtros
      document.getElementById('filterForm').addEventListener('submit', e => {
        e.preventDefault();
        fetchVideos({
          titulo: document.getElementById('fTitulo').value,
          autor: document.getElementById('fAutor').value,
          fecha: document.getElementById('fFecha').value
        });
      });

      // reset
      document.getElementById('btnReset').addEventListener('click', () => {
        document.getElementById('fTitulo').value = '';
        document.getElementById('fAutor').value = '';
        document.getElementById('fFecha').value = '';
        fetchVideos({});
      });
    });
  </script>
</body>
</html>