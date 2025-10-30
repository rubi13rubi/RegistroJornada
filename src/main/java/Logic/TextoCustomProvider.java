/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

import jakarta.servlet.ServletContext;

/**
 *
 * Obtiene el texto de firma personalizada
 */
public class TextoCustomProvider {
    public static String obtenerTexto(ServletContext context) {
        String texto = context.getInitParameter("FirmaPersonalizada");
        return (texto != null) ? texto : "";
    }
}

