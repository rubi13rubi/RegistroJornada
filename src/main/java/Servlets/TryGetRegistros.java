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
 * Servlet implementation class TryGetRegistros Gestiona la obtencion de registros de empleados por parte de los encargados
 */
@WebServlet("/TryGetRegistros")
public class TryGetRegistros extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TryGetRegistros() {
        super();
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     * Obtiene un json con los registros del usuario solicitado si el solicitante es un encargado.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json"); // Devuelve JSON
        response.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = request.getSession();
            String usuario = request.getParameter("usuario");
            String rol = (String) session.getAttribute("rol");
            if (usuario != null) {
                String jsonResponse;
                if (rol.equals("encargado")){
                    jsonResponse = new Gson().toJson(new ResponseData("correcto", Logic.getRegistrosRecientes(usuario)));
                }
                else{
                    jsonResponse = new Gson().toJson(new ResponseData("no_autorizado"));
                }
                // Devuelve los datos del usuario en formato JSON
                response.getWriter().write(jsonResponse);
            } else {
                // Si no hay sesión, devuelve un error
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"No autorizado\"}");
            }
        } catch (Exception e) {
            Log.log.error("Error obteniendo datos de usuario: " + e.getMessage());
        }
    }

    // Clase interna para representar los datos del usuario
    private class ResponseData {

        private String estado;
        private List<Registro> registros; //Solo en el caso de empleados

        public ResponseData(String estado, List<Registro> registros) { //Constructor para devolver datos de registros
            this.estado = estado;
            this.registros = registros;
        }
        
        public ResponseData(String estado) { //Constructor para devolver errores
            this.estado = estado;
        }
    }
}
