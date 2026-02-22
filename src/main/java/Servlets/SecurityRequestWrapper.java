package Servlets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;
import Logic.Logic;

public class SecurityRequestWrapper extends HttpServletRequestWrapper {

    // Todas las requests se envuelven en esta clase, por lo tanto cualquier llamada a getsession pasa por aqui.
    public SecurityRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    // Interceptamos request.getSession()
    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    // Interceptamos request.getSession(boolean)
    @Override
    public HttpSession getSession(boolean create) {
        // Obtenemos la sesión real usando el método original
        HttpSession session = super.getSession(create);

        // Si hay una sesión, la validamos justo antes de entregarla
        if (session != null && session.getAttribute("usuario") != null) {
            String usuario = (String) session.getAttribute("usuario");
            String rol = (String) session.getAttribute("rol");
            String stamp = (String) session.getAttribute("stamp");
            if (!Logic.isSessionValid(usuario, rol, stamp)) {
                // Si el sello no coincide (el usuario fue borrado/modificado)
                session.invalidate(); 
                
                // Dependiendo del parametro 'create', devolvemos null o una sesión nueva (vacía)
                return super.getSession(create); 
            }
        }
        
        // Si es válida o es una sesión nueva sin usuario, la devolvemos tal cual
        return session;
    }
}