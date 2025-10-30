/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 *
 * Almacena el contexto para poder extraer datos
 */
@WebListener
public class ContextHolder implements ServletContextListener {
    private static ServletContext context;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        context = sce.getServletContext();
    }
    public static ServletContext getContext() {
        return context;
    }
}

