/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.minetflix.filtros;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class PageNotFoundFilter implements Filter {

    // Lista de páginas válidas
    private static final List<String> VALID_PAGES = Arrays.asList(
        "servletUsuarios",
        "home.jsp",
        "registroVid.jsp",
        "servletRegistroVid",
        "listadoVid.jsp",
        "servletListadoVid",
        "login.jsp",
        "registroUsu.jsp",
        "logout.jsp",
        "notFoundPage.jsp",
        "verVideo.jsp",
        "servletVerVideo",
        "servletStreamVideo"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String pageName = requestURI.substring(requestURI.lastIndexOf("/") + 1);

        System.out.println("requestURI: " + requestURI);
        System.out.println("pageName: " + pageName);

        
        if (isValidPage(pageName) || isResource(pageName)) {
            // Si es página válida o recurso (CSS, JS, imágenes), continuar.
            chain.doFilter(request, response);
        } else {
            // Página no válida: redirigir a la página de error
            httpResponse.sendRedirect("notFoundPage.jsp");
        }

    }

    private boolean isValidPage(String pageName) {
        return VALID_PAGES.contains(pageName);
    }

    private boolean isResource(String pageName) {
        return pageName.endsWith(".css") || pageName.endsWith(".js") || pageName.endsWith(".png") || pageName.endsWith(".jpg");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}