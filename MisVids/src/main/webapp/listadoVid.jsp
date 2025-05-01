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
                <th data-field="titulo" class="sortable text-start">Título <span class="sort-indicator"></span></th>
                <th data-field="autor" class="sortable text-start">Autor <span class="sort-indicator"></span></th>
                <th data-field="fecha" class="sortable text-center">Fecha <span class="sort-indicator"></span></th>
                <th data-field="duracion" class="sortable text-center">Duración <span class="sort-indicator"></span></th>
                <th data-field="vistas" class="sortable text-center">Vistas <span class="sort-indicator"></span></th>
                <th data-field="descripcion text-start">Descripción</th>
                <th data-field="mimeType text-start" class="sortable">Formato <span class="sort-indicator"></span></th>
                <th data-field="tamano" class="sortable text-center" >Tamaño <span class="sort-indicator"></span></th>
                <th class="text-center">Enlace</th>
              </tr>
            </thead>
            <tbody>
              <!-- Se inyectan filas aquí -->
            </tbody>
      </table>
    </div>
    
    <!-- Paginación -->
    <nav aria-label="Paginación">
      <ul class="pagination justify-content-center pagination-netflix my-3">
        <li class="page-item">
          <button id="firstPage" class="page-link" type="button">« Primera</button>
        </li>
        <li class="page-item">
          <button id="prevPage" class="page-link" type="button">‹ Anterior</button>
        </li>
        <li class="page-item">
          <input
            id="pageInput"
            type="number"
            class="form-control text-center"
            style="width:80px;"
            min="1"
            value="1"
            aria-label="Número de página"
          >
        </li>
        <li class="page-item">
          <button id="nextPage" class="page-link" type="button">Siguiente ›</button>
        </li>
        <li class="page-item">
          <button id="lastPage" class="page-link" type="button">Última »</button>
        </li>
      </ul>
    </nav>

    <a href="home.jsp" class="back-link">Volver al menú principal</a>
  </div>

  <script>

    const PAGE_SIZE = 5;
     let currentPage      = 1,
         totalPages       = 1,
         currentFilters   = {},
         currentSortField = 'fecha',     // inicial por defecto
         currentSortOrder = 'desc';      // o 'asc'
    
    function updatePagination() {
      const input = document.getElementById('pageInput');
      input.value = currentPage;
      input.min   = 1;
      input.max   = totalPages;

      document.getElementById('firstPage').disabled = currentPage === 1;
      document.getElementById('prevPage').disabled  = currentPage === 1;
      document.getElementById('nextPage').disabled  = currentPage === totalPages;
      document.getElementById('lastPage').disabled  = currentPage === totalPages;
    }

    // evento para botones
    document.getElementById('firstPage').addEventListener('click', () => fetchVideos(currentFilters, 1));
    document.getElementById('prevPage').addEventListener('click', e => {
      e.preventDefault();
      e.stopPropagation();
      if (currentPage > 1) fetchVideos(currentFilters, currentPage - 1);
    });    
    document.getElementById('nextPage').addEventListener('click', e => {
      e.preventDefault();
      e.stopPropagation();
      if (currentPage < totalPages) fetchVideos(currentFilters, currentPage + 1);
    });    document.getElementById('lastPage').addEventListener ('click', () => fetchVideos(currentFilters, totalPages));


    // 1) formatea "2025-01-02 13:45:00" a "2 de enero de 2025" (o "20 de marzo de 2025")
    function formatDate(iso) {
      const d = new Date(iso.replace(' ', 'T'));
      const opciones = { day: 'numeric', month: 'long', year: 'numeric' };
      return d.toLocaleDateString('es-ES', opciones);
    }

    // 2) formatea segundos (por ej. 3665) a "1h 1m 5s" o "37m 5s" si no hay horas
    function formatDuration(totalSec) {
      let s = Math.floor(totalSec);
      const h = Math.floor(s / 3600);
      s %= 3600;
      const m = Math.floor(s / 60);
      s = s % 60;

      const partes = [];
      if (h) partes.push(h+"h");
      partes.push(m+"m");
      partes.push(s+"s");
      return partes.join(' ');
    }

    // formatea bytes a KB/MB/GB
    function formatBytes(bytes) {
      if (bytes == null) return '—';
      const kb = 1024, mb = kb*1024, gb = mb*1024;
      if (bytes >= gb) return (bytes/gb).toFixed(1) + ' GB';
      if (bytes >= mb) return (bytes/mb).toFixed(1) + ' MB';
      if (bytes >= kb) return (bytes/kb).toFixed(1) + ' KB';
      return bytes + ' B';
    }

    function renderTable(videos) {
        const tbody = document.querySelector('#videosTable tbody');
        tbody.innerHTML = '';
        if (videos.length === 0) {
          tbody.innerHTML = `<tr><td colspan="9" class="text-center">No hay videos registrados.</td></tr>`;
          return;
        }
        for (const v of videos) {
            const fullDesc = v.descripcion || '';
            const shortDesc = fullDesc.length > 100
              ? fullDesc.slice(0,100) + '…'
              : fullDesc;
            
            const row = document.createElement('tr');
            row.innerHTML = 
              "<td class='text-start'>"+v.titulo+"</td>"+
              "<td class='text-start'>"+v.autor+"</td>"+
              "<td class='text-center'>"+formatDate(v.fecha)+"</td>"+
              "<td class='text-center'>"+formatDuration(v.duracion)+"</td>"+
              "<td class='text-center'>"+v.reproducciones+"</td>"+
              "<td title='"+fullDesc+"' class='text-start'>"+shortDesc+"</td>"+
              "<td class='text-start'>"+v.mimeType+"</td>"+
              "<td class='text-center'>"+formatBytes(v.tamano)+"</td>"+
              "<td class='text-center'>"+
                "<a href="+"servletVerVideo?id="+v.id+" "+
                   "class="+"'link'>"+
                   "Ver video"+
                "</a>"+
              "</td>";
            tbody.appendChild(row);
        }
    }

    async function fetchVideos(filters, page = 1) {
        currentFilters = filters;
        currentPage    = page;

        const payload = {
          ...filters,
          page,
          pageSize: PAGE_SIZE,
          sortField: currentSortField,
          sortOrder: currentSortOrder
        };
        const res = await fetch('/web-service/api/videos/search', {
          method: 'POST',
          headers:{ 'Content-Type':'application/json','Accept':'application/json' },
          body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error(res.statusText);

        const { total, items } = await res.json();
        totalPages = Math.ceil(total / PAGE_SIZE);
        renderTable(items);
        updatePagination();
    }
    
    // después de definir fetchVideos…
    function bindSortControls() {
      document
        .querySelectorAll('#videosTable th.sortable')
        .forEach(th => {
          th.style.cursor = 'pointer';
          th.addEventListener('click', () => {
            const f = th.dataset.field;
            if (currentSortField === f) {
              currentSortOrder = currentSortOrder === 'asc' ? 'desc' : 'asc';
            } else {
              currentSortField = f;
              currentSortOrder = 'asc';
            }
            document.querySelectorAll('.sort-indicator')
                    .forEach(span => span.textContent = '');
            const arrow = currentSortOrder === 'asc' ? '↑' : '↓';
            th.querySelector('.sort-indicator').textContent = arrow;

            fetchVideos(currentFilters, 1);
          });
        });
    }
    

    document.addEventListener('DOMContentLoaded', () => {
        // 1) Bind filtros
        document.getElementById('filterForm').addEventListener('submit', e => {
          e.preventDefault();
          const filtros = {
            titulo: document.getElementById('fTitulo').value,
            autor:  document.getElementById('fAutor').value,
            fecha:  document.getElementById('fFecha').value
          };
          fetchVideos(filtros, 1);
        });
        document.getElementById('btnReset').addEventListener('click', () => {
          document.getElementById('fTitulo').value = '';
          document.getElementById('fAutor').value  = '';
          document.getElementById('fFecha').value  = '';
          fetchVideos({}, 1);
        });

        // 2) Bind paginación (AHORA ya existe en el DOM y no dispara submit)
        document.getElementById('firstPage').addEventListener('click', () => fetchVideos(currentFilters, 1));
        document.getElementById('prevPage').addEventListener('click',  () => fetchVideos(currentFilters, currentPage--));
        document.getElementById('nextPage').addEventListener('click',  () => fetchVideos(currentFilters, currentPage++));
        document.getElementById('lastPage').addEventListener('click',  () => fetchVideos(currentFilters, totalPages));
        document.getElementById('pageInput').addEventListener('keydown', e => {
          if (e.key === 'Enter') {
            e.preventDefault();
            const target = parseInt(pageInput.value, 10);
            if (target >= 1 && target <= totalPages && target !== currentPage) {
              fetchVideos(currentFilters, target);
            }
          }
        });
        
        // 3) Bind ordenación:
        bindSortControls()

        // 4) Carga inicial
        fetchVideos({}, 1);
      });
  </script>
</body>
</html>