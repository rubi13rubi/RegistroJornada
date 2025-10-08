package Servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import Logic.Log;
import Logic.Logic;

/**
 * Servlet implementation class TryLogin Gestiona el proceso de login de los
 * usuarios.
 */
@WebServlet("/TryCrearPrimerUsuario")
public class TryCrearPrimerUsuario extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TryCrearPrimerUsuario() {
        super();
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     * Intenta crear el primer encargado si no hay ninguno ya.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        Log.log.info("--Intentando crear el primer usuario--");
        response.setContentType("text/html;charset=UTF-8");

        String usuario = request.getParameter("usuario");
        String password = request.getParameter("password");

        try {
            if (!Logic.hayEncargados()) {
                Logic.crearUsuario(usuario, password, "Encargado");
                response.getWriter().write("correcto");
            } else {
                response.getWriter().write("incorrecto");
            }
        } catch (Exception e) {
            Log.log.error("Error durante la creacion del primer usuario: " + e.getMessage());
        }
    }
}
