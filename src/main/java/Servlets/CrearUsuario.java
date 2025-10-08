package Servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import Logic.Log;
import Logic.Logic;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class CrearUsuario gestiona la creacion de nuevos usuarios
 */
@WebServlet("/CrearUsuario")
public class CrearUsuario extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CrearUsuario() {
        super();
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     * Crea un usuario de nombre, password y rol cualquiera si el usuario que lo intenta esta autorizado.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        Log.log.info("--Intentando crear usuario--");
        response.setContentType("text/html;charset=UTF-8");

        String usuario = request.getParameter("usuario");
        String password = request.getParameter("password");
        String rol = request.getParameter("rol");
        try {
            HttpSession session = request.getSession();
            if (session.getAttribute("rol").equals("encargado")) {
                if (!Logic.existeUsuario(usuario)) {
                    Logic.crearUsuario(usuario, password, rol);
                    response.getWriter().write("correcto");
                } else if (session.getAttribute("rol") == null){
                    response.getWriter().write("no_autorizado");
                } else{
                    response.getWriter().write("incorrecto");
                }
            } else {
                response.getWriter().write("no_autorizado");
            }
        } catch (Exception e) {
            Log.log.error("Error durante la creacion de usuario: " + e.getMessage());
        }
    }
}
