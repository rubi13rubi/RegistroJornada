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
 * Servlet implementation class TryLogout cierra la sesión del usuario actual invalidando la cookie
 */
@WebServlet("/Logout")
public class Logout extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Logout() {
        super();
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     * Cierra la sesion del usuario.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        try {
            Log.log.info("--Cerrando la sesion--");

            HttpSession session = request.getSession();
            session.invalidate();
        } catch (Exception e) {
            Log.log.error("Error cerrando la sesion: " + e.getMessage());
        }
    }
}
