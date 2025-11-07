package Servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import Logic.Log;
import Logic.Logic;
import Logic.RegistroRaw;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.sql.Time;
import java.time.Duration;

/**
 * Servlet implementation class TryCrearRegistro gestiona la edicion de usuarios
 */
@WebServlet("/TryEditarUsuarios")
public class TryEditarUsuarios extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TryEditarUsuarios() {
        super();
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     * Intenta editar el usuario que se indica en la peticion
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Log.log.info("--Intentando crear nota--");
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            HttpSession session = request.getSession();
            String usuario = (String) session.getAttribute("usuario"); // Usuario que realiza la peticion
            String rol = (String) session.getAttribute("rol"); // Rol del usuario que realiza la peticion
            String nombreCambio = request.getParameter("nombre"); // Nombre del usuario que se desea modificar
            String rolCambio = request.getParameter("nombre"); // Rol del usuario que se desea modificar
            String accion = request.getParameter("accion"); // Accion que se desea realizar
            String input = request.getParameter("input"); // Texto de la caja de texto

            if (rol.equals("encargado")) {
                if (usuario.equals(nombreCambio)) {
                    // No se pueden realizar cambios a su propio usuario
                    response.getWriter().write("usuario_igual");
                } else {
                    if (accion.equals("cambiarnombre")){
                        // Accion de cambio de nombre
                        if (input.length()>60){
                            // El nuevo nombre no puede tener más de 60 caracteres
                            response.getWriter().write("muy_largo");
                        } else{
                            // Llamada a logic para cambio de nombre
                            //...
                            response.getWriter().write("correcto");
                        }
                    } else{
                        if (input.equals(nombreCambio)){
                            if (accion.equals("cambiarrol")){
                                // Llamada a logic para cambio de rol
                                //...
                                response.getWriter().write("correcto");
                            } else{
                                // Llamada a logic para borrado
                                //...
                                response.getWriter().write("correcto");
                            }
                        } else{
                            // Confirmacion de cambio (el nombre debe ser igual al input)
                            response.getWriter().write("no_coincide");
                        }
                    }
                }
                
            } else {
                // Si no hay sesion o no esta autorizado, devuelve error
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("no_autorizado");
            }
        } catch (Exception e) {
            Log.log.error("Error creando nota: " + e.getMessage());
        }
    }
}
