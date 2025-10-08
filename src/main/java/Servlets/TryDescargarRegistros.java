package Servlets;

import Logic.CSVExporter;
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

/**
 * Servlet implementation class TryDescargarRegistros Gestiona la descarga de registros de empleados
 */
@WebServlet("/TryDescargarRegistros")
public class TryDescargarRegistros extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TryDescargarRegistros() {
        super();
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     * Obtiene un csv con todos los registros del usuario solicitado si el solicitante es un encargado o lo solicita el mismo.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        Log.log.info("Intentando descargar registros de usuario");
        response.setContentType("text/csv"); // Devuelve un csv
        response.setHeader("Content-Disposition", "attachment; filename=\"registros.csv\""); // Lo devuelve como archivo
        response.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = request.getSession();
            String usuario = request.getParameter("usuario");
            String rol = (String) session.getAttribute("rol");
            String csvResponse = "Error desconocido."; // Mensaje de error por si falla algo
            if (usuario != null) {
                if (rol.equals("encargado") || usuario.equals( (String) session.getAttribute("usuario"))){
                    csvResponse = CSVExporter.registrosToCSV(Logic.getRegistros(usuario));
                }
                else{
                    csvResponse = "Error. El usuario no está autorizado para esta consulta.";
                }
            }
            // Escribe los datos
            response.getWriter().write(csvResponse);
        } catch (IOException e) {
            Log.log.error("Error descargando registros de usuario: " + e.getMessage());
        }
    }
}
