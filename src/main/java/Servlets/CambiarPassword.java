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
 * Servlet implementation class CambiarPassword gestiona el cambio de password
 */
@WebServlet("/CambiarPassword")
public class CambiarPassword extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CambiarPassword() {
        super();
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response) Comprueba la password para cambiarla
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Log.log.info("--Intentando login--");
        response.setContentType("text/html;charset=UTF-8");

        String passwordVieja = request.getParameter("passwordVieja");
        String passwordNueva = request.getParameter("passwordNueva");
        HttpSession session = request.getSession();
        String usuario = (String) session.getAttribute("usuario");
        String rol = (String) session.getAttribute("rol");

        try {
            if (usuario != null) {
                if (Logic.tryLogin(usuario, passwordVieja) == 0) {
                    response.getWriter().write("incorrecto");
                } else {
                    Logic.cambiarPassword(usuario, passwordNueva, rol);
                    response.getWriter().write("correcto");
                }
            } else{
                response.getWriter().write("no_autorizado");
            }

        } catch (IOException e) {
            Log.log.error("Error durante el login: " + e.getMessage());
        }
    }
}
