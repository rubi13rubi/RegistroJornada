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
 * Servlet implementation class TryCrearRegistro gestiona la creacion de registros por parte de los usuarios
 */
@WebServlet("/TryCrearNota")
public class TryCrearNota extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TryCrearNota() {
        super();
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     * Intenta crear una nota para el empleado que manda la solicitud en un registro.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Log.log.info("--Intentando crear nota--");
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            HttpSession session = request.getSession();
            String usuario = (String) session.getAttribute("usuario");
            String rol = (String) session.getAttribute("rol");
            int idRegistro = Integer.parseInt(request.getParameter("idRegistro"));
            String textoNota = request.getParameter("textoNota");
            String usuarioRegistro = Logic.getUsuarioRegistro(idRegistro);
            if (rol.equals("encargado") || (rol.equals("empleado") && usuario.equals(usuarioRegistro))) {
                // Crear nota y devolver respuesta
                long millis = System.currentTimeMillis(); // milisegundos actuales
                Date fecha = new Date(millis);
                Time hora = new Time(millis);
                Logic.crearNota(idRegistro, usuario, fecha, hora, textoNota);
                response.getWriter().write("correcto");
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
