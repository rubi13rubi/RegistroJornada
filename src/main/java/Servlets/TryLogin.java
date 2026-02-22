package Servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import Logic.Log;
import Logic.Logic;
import Logic.LoginState;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class TryLogin
 Gestiona el proceso de login de los usuarios.
 */
@WebServlet("/TryLogin")
public class TryLogin extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TryLogin() {
        super();
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     * Comprueba el usuario y password proporcionados para ver si son correctos y actualiza la sesion.
     */

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        Log.log.info("--Intentando login--");
        response.setContentType("text/html;charset=UTF-8");

        String usuario = request.getParameter("usuario");
        String password = request.getParameter("password");

        try {
            LoginState estadoLogin = Logic.tryLogin(usuario, password);
            int loginExitoso = estadoLogin.getState();
            String stamp = estadoLogin.getStamp();

            switch (loginExitoso) {
                case 1 ->                     {
                        HttpSession session = request.getSession();
                        session.setAttribute("usuario", usuario);
                        session.setAttribute("rol", "empleado");
                        session.setAttribute("stamp", stamp);
                        response.getWriter().write("correcto");
                    }
                case 2 ->                     {
                        HttpSession session = request.getSession();
                        session.setAttribute("usuario", usuario);
                        session.setAttribute("rol", "encargado");
                        session.setAttribute("stamp", stamp);
                        response.getWriter().write("correcto");
                    }
                case -1 ->                     { // Error de base de datos o de servidor
                        response.getWriter().write("error");
                    }
                default -> // Redirige a una página de error o muestra un mensaje en el formulario
                    response.getWriter().write("incorrecto");
            }
        } catch (Exception e) {
            Log.log.error("Error durante el login: " + e.getMessage());
        }
    }
}