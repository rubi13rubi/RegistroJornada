package Servlets;

import Logic.Log;
import Logic.Registro;
import Logic.Logic;
import Logic.RegistroRaw;
import com.google.gson.Gson;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.util.List;

/**
 * Servlet implementation class GetUser gestiona la obtencion de los datos que
 * se envian nada mas iniciar sesion
 */
@WebServlet("/GetUser")
public class GetUser extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUser() {
        super();
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response) Obtiene un json con los datos del usuario en la sesion.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json"); // Devuelve JSON
        response.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = request.getSession();
            String usuario = (String) session.getAttribute("usuario");
            String rol = (String) session.getAttribute("rol");
            if (usuario != null) {
                String jsonResponse;
                if (rol.equals("empleado")) {
                    //Los empleados obtienen su lista de registros recientes y su estado
                    long millis = System.currentTimeMillis(); // milisegundos actuales
                    Date fechaActual = new Date(millis);
                    RegistroRaw ultimo = Logic.getUltimoRegistro(usuario);
                    String estado;
                    if (ultimo == null || ultimo.getTipo().equals("Salida") || ultimo.getFecha().toLocalDate().isBefore(fechaActual.toLocalDate())){
                        estado = "No trabajando";
                    } else {
                        estado = "Trabajando";
                    }
                    jsonResponse = new Gson().toJson(new UserData(usuario, rol, Logic.getRegistrosRecientes(usuario), estado));
                } else {
                    //Los encargados obtienen la lista de empleados para poder consultar sus registros posteriormente
                    jsonResponse = new Gson().toJson(new UserData(usuario, rol, Logic.getEmpleados(), ""));
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

    // Clase interna para representar los datos que se devuelven
    private class UserData {

        private String usuario;
        private String rol;
        private List datos; // Datos que varian segun el rol
        private String estado; // Para empleados (trabajando o no trabajando)

        public UserData(String usuario, String rol, List datos, String estado) {
            this.usuario = usuario;
            this.rol = rol;
            this.estado = estado;
            this.datos = datos; // Datos es la lista de registros para empleados y la lista de nombres de empleados para encargados
        }
    }
}
