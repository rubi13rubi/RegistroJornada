package Servlets;

import Logic.Log;
import Logic.Registro;
import Logic.Logic;
import com.google.gson.Gson;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * Servlet implementation class TryGetRegistros Gestiona la obtencion de los nombres de todos los usuarios
 */
@WebServlet("/TryGetUsuarios")
public class TryGetUsuarios extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TryGetUsuarios() {
        super();
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     * Obtiene un json con los nombres de los empleados y encargados
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json"); // Devuelve JSON
        response.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = request.getSession();
            String rol = (String) session.getAttribute("rol");
            if (rol.equals("encargado")) {
                String jsonResponse;
                jsonResponse = new Gson().toJson(new ResponseData(Logic.getEmpleados(), Logic.getEncargados()));
                // Devuelve los datos del usuario en formato JSON
                response.getWriter().write(jsonResponse);
            } else {
                // Si no hay sesión o el usuario no está autorizado, devuelve un error
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"No autorizado\"}");
            }
        } catch (Exception e) {
            Log.log.error("Error obteniendo datos de usuario: " + e.getMessage());
        }
    }

    // Clase interna para representar los datos del usuario
    private class ResponseData {
;
        private final List<String> empleados; //Lista de nombres de empleados
        private final List<String> encargados; //Lista de nombres de encargados

        public ResponseData(List<String> empleados, List<String> encargados) { //Constructor para devolver datos de registros
            this.empleados = empleados;
            this.encargados = encargados;
        }
    }
}
