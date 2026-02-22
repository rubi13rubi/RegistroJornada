/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servlets;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author ruben
 * Filtra las peticiones para manejar la validez de sesiones en el wrapper
 */
@WebFilter("/*") // Filtro para todas las peticiones
public class RequestWrappingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest) {
            // Transforma la request en la implementacion personalizada que maneja la validez de cookies
            HttpServletRequest wrappedRequest = new SecurityRequestWrapper((HttpServletRequest) request);
            
            // Pasa la peticion modificada a la aplicacion.
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}