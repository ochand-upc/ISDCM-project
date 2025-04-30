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
          <button type="submit" class="btn btn-netflix" >Filtrar</button>
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
    

    
    <!-- Paginación -->
    <nav aria-label="Paginación">
      <ul class="pagination pagination-netflix justify-content-center my-3">
        <li class="page-item">
          <button id="prevPage" class="page-link" disabled>Anterior</button>
        </li>
        <li class="page-item disabled">
          <span class="page-link">
            Página <span id="pageNumber">1</span> de <span id="pageCount">1</span>
          </span>
        </li>
        <li class="page-item">
          <button id="nextPage" class="page-link">Siguiente</button>
        </li>
      </ul>
    </nav>

    <a href="home.jsp" class="back-link">Volver al menú principal</a>
  </div>

  <script>

    let currentPage = 1, pageSize = 5, totalPages = 1, lastFilters = {};

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

    async function fetchVideos(filters) {
        lastFilters = { ...filters };
        const res = await fetch('/web-service/api/videos/search', {
          method: 'POST',
          headers: { 'Content-Type':'application/json' },
          body: JSON.stringify({ ...filters, page: currentPage, pageSize })
        });
        if (!res.ok) throw new Error(res.statusText);
        const { total, items } = await res.json();
        totalPages = Math.max(1, Math.ceil(total / pageSize));
        document.getElementById('pageNumber').textContent = currentPage;
        document.getElementById('pageCount').textContent  = totalPages;
        document.getElementById('prevPage').disabled = currentPage === 1;
        document.getElementById('nextPage').disabled = currentPage === totalPages;
        renderTable(items);
      }

    document.addEventListener('DOMContentLoaded', () => {
        const form    = document.getElementById('filterForm');
        const prevBtn = document.getElementById('prevPage');
        const nextBtn = document.getElementById('nextPage');

        // Carga inicial
        fetchVideos({});

        form.addEventListener('submit', e => {
          e.preventDefault();
          currentPage = 1;
          fetchVideos({
            titulo: document.getElementById('fTitulo').value,
            autor:  document.getElementById('fAutor').value,
            fecha:  document.getElementById('fFecha').value
          });
        });

        prevBtn.addEventListener('click', () => {
          if (currentPage > 1) {
            currentPage--;
            fetchVideos(lastFilters);
          }
        });

        nextBtn.addEventListener('click', () => {
          if (currentPage < totalPages) {
            currentPage++;
            fetchVideos(lastFilters);
          }
        });

        document.getElementById('btnReset').addEventListener('click', () => {
          document.getElementById('fTitulo').value = '';
          document.getElementById('fAutor').value  = '';
          document.getElementById('fFecha').value  = '';
          currentPage = 1;
          fetchVideos({});
        });
    });
  </script>
</body>
</html>