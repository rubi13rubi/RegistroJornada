package Servlets;

import Logic.CSVExporter;
import Logic.Log;
import Logic.Logic;
import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet implementation class TryDescargarRegistros Gestiona la descarga de
 * registros de empleados
 */
@WebServlet("/TryDescargarRegistrosZip")
public class TryDescargarRegistrosZip extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TryDescargarRegistrosZip() {
        super();
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response) Obtiene un zip con todos los registros de todos los usuarios si
     * el solicitante es encargado.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        Log.log.info("Intentando descargar registros de todos los usuarios");
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"reportes.zip\"");

        try {
            HttpSession session = request.getSession();
            String rol = (String) session.getAttribute("rol");
            List<String> csvs = new ArrayList();
            List<String> nombres = new ArrayList();
            
            if (rol.equals("encargado")) {
                Date fechaInicio = java.sql.Date.valueOf(request.getParameter("fechaInicio"));
                Date fechaFin = java.sql.Date.valueOf(request.getParameter("fechaFin"));
                nombres = Logic.getEmpleados();
                for (String nombre:nombres){
                    csvs.add(CSVExporter.registrosToCSV(Logic.getRegistrosFecha(nombre, fechaInicio, fechaFin)));
                }
            } else {
                nombres.add("Error");
                csvs.add("Usuario no autorizado");
            }
            // Escribe los datos
            byte[] zipBytes = CSVExporter.toZip(csvs, nombres);

            response.setContentLength(zipBytes.length);
            response.getOutputStream().write(zipBytes);
        } catch (IOException e) {
            Log.log.error("Error descargando registros en zip: " + e.getMessage());
        }
    }
}
