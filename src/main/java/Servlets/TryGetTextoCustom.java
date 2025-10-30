package Servlets;

import Logic.Log;
import Logic.TextoCustomProvider;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class TryGetTextoCustom gestiona la obtencion del texto personalizado de la empresa
 */
@WebServlet("/TryGetTextoCustom")
public class TryGetTextoCustom extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TryGetTextoCustom() {
        super();
    }

    /**
     * @param request
     * @param response
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     * Obtiene el texto de firma personalizado
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");

        try {
            String texto = TextoCustomProvider.obtenerTexto(getServletContext());
            response.getWriter().write(texto);
        } catch (Exception e) {
            Log.log.error("Error obteniendo el texto custom: " + e.getMessage());
        }
    }
}
