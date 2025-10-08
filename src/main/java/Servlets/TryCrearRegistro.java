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
@WebServlet("/TryCrearRegistro")
public class TryCrearRegistro extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TryCrearRegistro() {
        super();
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     * Intenta crear un registro para el empleado que manda la solicitud.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        Log.log.info("--Intentando crear registro--");
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            HttpSession session = request.getSession();
            String usuario = (String) session.getAttribute("usuario");
            String rol = (String) session.getAttribute("rol");
            if (usuario != null && !"encargado".equals(rol)) {
                // Determinar el tipo de registro que se debe crear
                RegistroRaw ultimo = Logic.getUltimoRegistro(usuario);
                long millis = System.currentTimeMillis(); // milisegundos actuales
                Date fechaActual = new Date(millis);
                Time horaActual = new Time(millis);
                String tipo;

                if (ultimo == null || ultimo.getTipo().equals("Salida") || ultimo.getFecha().toLocalDate().isBefore(fechaActual.toLocalDate())){
                    // Estado no trabajando, se registra entrada
                    tipo = "Entrada";
                }
                else{
                    // Estado trabajando, se registra salida
                    tipo = "Salida";
                }
                // Determinar los minutos acumulados;
                int minutosAcumulados = 0;
                if (tipo.equals("Salida")) minutosAcumulados = (int) Duration.between(ultimo.getHora().toLocalTime(), horaActual.toLocalTime()).toMinutes();
                
                // Crear registro y devolver respuesta
                Logic.crearRegistro(usuario, fechaActual, horaActual, tipo, minutosAcumulados);
                response.getWriter().write("correcto");
            } else {
                // Si no hay sesion o es encargado, devuelve un error
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("no_autorizado");
            }
        } catch (Exception e) {
            Log.log.error("Error creando registro: " + e.getMessage());
        }
    }
}
