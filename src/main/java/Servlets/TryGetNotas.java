package Servlets;

import Logic.Log;
import Logic.Logic;
import Logic.Nota;
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
 * Servlet implementation class TryGetNotas gestiona la obtencion de notas de un registro
 */
@WebServlet("/TryGetNotas")
public class TryGetNotas extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TryGetNotas() {
        super();
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     * Obtiene un json con las notas
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = request.getSession();
            String usuario = (String) session.getAttribute("usuario");
            String rol = (String) session.getAttribute("rol");
            int idRegistro = Integer.parseInt(request.getParameter("idRegistro"));
            String usuarioRegistro = Logic.getUsuarioRegistro(idRegistro);
            String jsonResponse;
            if (rol.equals("encargado") || (rol.equals("empleado") && usuario.equals(usuarioRegistro))) {
                // Obtener registros
                jsonResponse = new Gson().toJson(new ResponseData("correcto", Logic.getNotasRegistro(idRegistro)));
            } else {
                // Si no hay sesion o no esta autorizado, devuelve error
                jsonResponse = new Gson().toJson(new ResponseData("no_autorizado"));
            }
            response.getWriter().write(jsonResponse);
        } catch (Exception e) {
            Log.log.error("Error obteniendo notas de registro: " + e.getMessage());
        }
    }

    // Clase interna para representar los datos de la respuesta
    private class ResponseData {

        private String estado;
        private List<Nota> notas; //Solo en el caso de autorizados

        public ResponseData(String estado, List<Nota> notas) { //Constructor para devolver datos de registros
            this.estado = estado;
            this.notas = notas;
        }
        
        public ResponseData(String estado) { //Constructor para devolver errores
            this.estado = estado;
        }
    }
}
