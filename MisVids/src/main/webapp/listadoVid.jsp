<%-- 
    Document   : listadoVid
    Created on : 21 feb 2025, 14:30:20
    Author     : alumne2
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
                    <!-- <input id="fFecha" type="text" class="form-control" placeholder="YYYY, YYYY-MM o YYYY-MM-DD"> -->
                    <div class="col-12 col-md-auto d-flex gap-2 align-items-center">
                        <select id="fAnio" class="form-select">
                            <option value="">Año</option>
                            <option value="2025">2025</option>
                            <option value="2024">2024</option>
                            <option value="2023">2023</option>
                            <option value="2022">2022</option>
                            <option value="2021">2021</option>
                            <option value="2020">2020</option>
                            <option value="2019">2019</option>
                            <option value="2018">2018</option>
                            <option value="2017">2017</option>
                            <option value="2016">2016</option>
                            <option value="2015">2015</option>
                            <option value="2014">2014</option>
                            <option value="2013">2013</option>
                            <option value="2012">2012</option>
                            <option value="2011">2011</option>
                            <option value="2010">2010</option>
                            <option value="2009">2009</option>
                            <option value="2008">2008</option>
                            <option value="2007">2007</option>
                            <option value="2006">2006</option>
                            <option value="2005">2005</option>
                            <option value="2004">2004</option>
                            <option value="2003">2003</option>
                            <option value="2002">2002</option>
                            <option value="2001">2001</option>
                            <option value="2000">2000</option>
                        </select>
                        <select id="fMes" class="form-select">
                            <option value="">Mes</option>
                            <option value="01">01</option>
                            <option value="02">02</option>
                            <option value="03">03</option>
                            <option value="04">04</option>
                            <option value="05">05</option>
                            <option value="06">06</option>
                            <option value="07">07</option>
                            <option value="08">08</option>
                            <option value="09">09</option>
                            <option value="10">10</option>
                            <option value="11">11</option>
                            <option value="12">12</option>
                        </select>
                        <select id="fDia" class="form-select">
                            <option value="">Día</option>
                            <option value="01">01</option>
                            <option value="02">02</option>
                            <option value="03">03</option>
                            <option value="04">04</option>
                            <option value="05">05</option>
                            <option value="06">06</option>
                            <option value="07">07</option>
                            <option value="08">08</option>
                            <option value="09">09</option>
                            <option value="10">10</option>
                            <option value="11">11</option>
                            <option value="12">12</option>
                            <option value="13">13</option>
                            <option value="14">14</option>
                            <option value="15">15</option>
                            <option value="16">16</option>
                            <option value="17">17</option>
                            <option value="18">18</option>
                            <option value="19">19</option>
                            <option value="20">20</option>
                            <option value="21">21</option>
                            <option value="22">22</option>
                            <option value="23">23</option>
                            <option value="24">24</option>
                            <option value="25">25</option>
                            <option value="26">26</option>
                            <option value="27">27</option>
                            <option value="28">28</option>
                            <option value="29">29</option>
                            <option value="30">30</option>
                            <option value="31">31</option>
                        </select>

                    </div>

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
                            <th data-field="reproducciones" class="sortable text-center">Vistas <span class="sort-indicator"></span></th>
                            <th data-field="descripcion" class="text-start">Descripción</th>
                            <th data-field="mime_type" class="sortable text-start">Formato <span class="sort-indicator"></span></th>
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


            <div class="position-fixed top-0 end-0 p-3" style="z-index: 9999;">
                <div id="customToast" class="toast align-items-center text-bg-danger border-0"
                     role="alert" aria-live="assertive" aria-atomic="true"
                     data-bs-autohide="false">
                    <div class="d-flex">
                        <div class="toast-body">
                            <!-- Mensaje dinámico -->
                        </div>
                        <button type="button" class="btn-close btn-close-white me-2 m-auto"
                                data-bs-dismiss="toast" aria-label="Close"></button>
                    </div>
                </div>            
            </div>
        </div>

        <script>

            const PAGE_SIZE = 5;
            let currentPage = 1,
                    totalPages = 1,
                    currentFilters = {},
                    currentSortField = 'fecha', // inicial por defecto
                    currentSortOrder = 'desc';      // o 'asc'

            function updatePagination() {
                const input = document.getElementById('pageInput');
                input.value = currentPage;
                input.min = 1;
                input.max = totalPages;

                document.getElementById('firstPage').disabled = currentPage === 1;
                document.getElementById('prevPage').disabled = currentPage === 1;
                document.getElementById('nextPage').disabled = currentPage === totalPages;
                document.getElementById('lastPage').disabled = currentPage === totalPages;
            }

            // 1) formatea "2025-01-02 13:45:00" a "2 de enero de 2025" (o "20 de marzo de 2025")
            function formatDate(iso) {
                const d = new Date(iso.replace(' ', 'T'));
                const opciones = {day: 'numeric', month: 'long', year: 'numeric'};
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
                if (h)
                    partes.push(h + "h");
                partes.push(m + "m");
                partes.push(s + "s");
                return partes.join(' ');
            }

            // formatea bytes a KB/MB/GB
            function formatBytes(bytes) {
                if (bytes == null)
                    return '—';
                const kb = 1000, mb = kb * 1000, gb = mb * 1000;
                if (bytes >= gb)
                    return (bytes / gb).toFixed(1) + ' GB';
                if (bytes >= mb)
                    return (bytes / mb).toFixed(1) + ' MB';
                if (bytes >= kb)
                    return (bytes / kb).toFixed(1) + ' KB';
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
                            ? fullDesc.slice(0, 100) + '…'
                            : fullDesc;

                    const row = document.createElement('tr');
                    row.innerHTML =
                            "<td title='" + v.titulo + "' class='text-start cell-truncate'>" + v.titulo + "</td>" +
                            "<td title='" + v.autor + "' class='text-start cell-truncate'>" + v.autor + "</td>" +
                            "<td class='text-center'>" + formatDate(v.fecha) + "</td>" +
                            "<td class='text-center'>" + formatDuration(v.duracion) + "</td>" +
                            "<td class='text-center'>" + v.reproducciones + "</td>" +
                            "<td title='" + fullDesc + "' class='text-start'>" + shortDesc + "</td>" +
                            "<td class='text-start'>" + v.mimeType + "</td>" +
                            "<td class='text-center'>" + formatBytes(v.tamano) + "</td>" +
                            "<td class='text-center'>" +
                            "<a href=" + "servletRest?action=view&id=" + v.id + " " +
                            "class=" + "'link'>" +
                            "Ver video" +
                            "</a>" +
                            "</td>";
                    tbody.appendChild(row);
                }
            }

            async function fetchVideos(filters, page = 1) {
                currentFilters = filters;
                currentPage = page;

                const payload = {
                    ...filters,
                    page,
                    pageSize: PAGE_SIZE,
                    sortField: currentSortField,
                    sortOrder: currentSortOrder
                };

                try {
                    const res = await fetch('servletRest?action=search', {
                        method: 'POST',
                        headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
                        body: JSON.stringify(payload)
                    });

                    // Si el recurso no existe (404) o hay otro error -> mostramos toast
                    if (res.status === 404) {
                        showToast('Servicio no encontrado (404)');
                        return;
                    }
                    if (!res.ok) {
                        showToast(`Error ${res.status}: ${res.statusText}`);
                        return;
                    }

                    const {total, items} = await res.json();
                    totalPages = Math.ceil(total / PAGE_SIZE);
                    renderTable(items);
                    updatePagination();
                } catch (e) {
                    console.error(e);
                    showToast('Error de red al comunicarse con el servicio.');
            }
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


            // mostrar toast
            function showToast(textContent, error = true) {
                const toastEl = document.getElementById("customToast");
                const toastBody = toastEl.querySelector(".toast-body");
                const toast = new bootstrap.Toast(toastEl);

                if (error) {
                    toastEl.classList.remove("text-bg-success");
                    toastEl.classList.add("text-bg-danger");
                } else {
                    toastEl.classList.add("text-bg-success");
                    toastEl.classList.remove("text-bg-danger");
                }
                toastBody.textContent = textContent;
                // Mostrar el toast
                toast.show();
            }

            document.addEventListener('DOMContentLoaded', () => {
                // 1) Referencias a los selects de fecha
                const anioSelect = document.getElementById('fAnio');
                const mesSelect = document.getElementById('fMes');
                const diaSelect = document.getElementById('fDia');

                // 2) Evento submit con construcción correcta de fecha
                document.getElementById('filterForm').addEventListener('submit', e => {
                    e.preventDefault();

                    const titulo = document.getElementById('fTitulo').value;
                    const autor = document.getElementById('fAutor').value;
                    const anio = anioSelect.value;
                    const mes = mesSelect.value;
                    const dia = diaSelect.value;

                    if (!anio && (mes || dia)) {
                        showToast("Selecciona un año primero.");
                        return;
                    }
                    if (mes && !anio) {
                        showToast("No puedes seleccionar mes sin año.");
                        return;
                    }
                    if (dia && (!anio || !mes)) {
                        showToast("Selecciona primero año y mes para elegir un día.");
                        return;
                    }



                    let partes = [];

                    if (anio !== "")
                        partes.push(anio);
                    if (mes !== "")
                        partes.push(mes);
                    if (dia !== "")
                        partes.push(dia);

                    const fechaFiltro = partes.join("-");

                    const filtros = {
                        titulo: titulo,
                        autor: autor,
                        fecha: fechaFiltro
                    };

                    console.log("Payload generado:", filtros); // Debug temporal
                    fetchVideos(filtros, 1);
                });

                // 3) Reset completo
                document.getElementById('btnReset').addEventListener('click', () => {
                    document.getElementById('fTitulo').value = '';
                    document.getElementById('fAutor').value = '';
                    anioSelect.value = '';
                    mesSelect.value = '';
                    diaSelect.value = '';
                    fetchVideos({}, 1);
                });

                // 4) Bind paginación
                document.getElementById('firstPage').addEventListener('click', () => fetchVideos(currentFilters, 1));
                document.getElementById('prevPage').addEventListener('click', e => {
                    e.preventDefault();
                    e.stopPropagation();
                    if (currentPage > 1)
                        fetchVideos(currentFilters, currentPage - 1);
                });
                document.getElementById('nextPage').addEventListener('click', e => {
                    e.preventDefault();
                    e.stopPropagation();
                    if (currentPage < totalPages)
                        fetchVideos(currentFilters, currentPage + 1);
                });
                document.getElementById('lastPage').addEventListener('click', () => fetchVideos(currentFilters, totalPages));
                document.getElementById('pageInput').addEventListener('keydown', e => {
                    if (e.key === 'Enter') {
                        e.preventDefault();
                        const target = parseInt(e.target.value, 10);
                        if (target >= 1 && target <= totalPages && target !== currentPage) {
                            fetchVideos(currentFilters, target);
                        }
                    }
                });

                // 5) Bind ordenación
                bindSortControls();

                // 6) Carga inicial
                fetchVideos({}, 1);

                // 7) Mostrar errores desde el servidor si vienen en sesión
            <% if (session.getAttribute("mensajeError") != null) {%>
                showToast('<%= session.getAttribute("mensajeError")%>');
            <% session.removeAttribute("mensajeError"); %>
            <% }%>
            });


        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
    </body>
</html>