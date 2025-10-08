package Logic;

import Database.ConectionDDBB;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.security.crypto.bcrypt.BCrypt;
import Logic.Registro;
import java.util.List;

public class Logic {

    public static int tryLogin(String usuario, String password) {
        ConectionDDBB conector = new ConectionDDBB();
        Connection con = null;
        try {
            con = conector.obtainConnection(true);
            Log.log.info("Database Connected");
            PreparedStatement ps = ConectionDDBB.GetEncargado(con);
            ps.setString(1, usuario);
            Log.log.info("Query => " + ps.toString());
            ResultSet rs = ps.executeQuery();
            int resultado;
            if (!rs.next()) {
                ps = ConectionDDBB.GetEmpleado(con);
                ps.setString(1, usuario);
                Log.log.info("Query => " + ps.toString());
                rs = ps.executeQuery();
                if (!rs.next()) {
                    return 0; //No existe el usuario en ninguna de las dos tablas
                } else {
                    resultado = 1; //El usuario es un empleado
                }
            } else {
                resultado = 2; //El usuario es un encargado
            }
            String hash_pw = rs.getString("hash_pw");
            if (BCrypt.checkpw(password, hash_pw)) {//Las pw coinciden, retorna segun es usuario o encargado
                return resultado;
            } else { //Si no coinciden retorna 0 (error)
                return 0;
            }
        } catch (SQLException e) {
            Log.log.error("Error al comprobar usuario y password" + e);
        } catch (NullPointerException e) {
            Log.log.error("Error: " + e);
        } catch (Exception e) {
            Log.log.error("Error inesperado: " + e);
        } finally {
            conector.closeConnection(con);
        }
        return 0;
    }

    public static Boolean hayEncargados() {
        ConectionDDBB conector = new ConectionDDBB();
        Connection con = null;
        try {
            con = conector.obtainConnection(true);
            Log.log.info("Database Connected");
            PreparedStatement ps = ConectionDDBB.GetEncargados(con);

            Log.log.info("Query => " + ps.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next(); // Si hay datos de resultado significa que hay empleados
        } catch (SQLException e) {
            Log.log.error("Error al comprobar si hay encargados" + e);
        } catch (NullPointerException e) {
            Log.log.error("Error: " + e);
        } catch (Exception e) {
            Log.log.error("Error inesperado: " + e);
        } finally {
            conector.closeConnection(con);
        }
        return true;
    }

    public static Boolean existeUsuario(String usuario) {
        ConectionDDBB conector = new ConectionDDBB();
        Connection con = null;
        try {
            con = conector.obtainConnection(true);
            Log.log.info("Database Connected");

            PreparedStatement ps1 = ConectionDDBB.GetEncargado(con);
            ps1.setString(1, usuario);
            Log.log.info("Query1 => " + ps1.toString());
            ResultSet rs1 = ps1.executeQuery();

            PreparedStatement ps2 = ConectionDDBB.GetEmpleado(con);
            ps2.setString(1, usuario);
            Log.log.info("Query1 => " + ps2.toString());
            ResultSet rs2 = ps2.executeQuery();

            return (rs1.next() || rs2.next()); // Si hay datos de resultado en cualquiera de los resultados significa que hay un usuario con ese nombre
        } catch (SQLException e) {
            Log.log.error("Error al comprobar si hay encargados" + e);
        } catch (NullPointerException e) {
            Log.log.error("Error: " + e);
        } catch (Exception e) {
            Log.log.error("Error inesperado: " + e);
        } finally {
            conector.closeConnection(con);
        }
        return true;
    }

    public static void crearUsuario(String usuario, String password, String rol) {
        ConectionDDBB conector = new ConectionDDBB();
        Connection con = null;
        try {
            con = conector.obtainConnection(true);
            Log.log.info("Database Connected");
            PreparedStatement ps;
            if (rol.equals("Empleado")) {
                ps = ConectionDDBB.CrearEmpleado(con);
            } else {
                ps = ConectionDDBB.CrearEncargado(con);
            }
            String pw_hash = BCrypt.hashpw(password, BCrypt.gensalt());
            // Parametros de la sentencia
            ps.setString(1, usuario);
            ps.setString(2, pw_hash);

            Log.log.info("Query => " + ps.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            Log.log.error("Error al insertar nuevo usuario: " + e);
        } catch (NullPointerException e) {
            Log.log.error("Error: " + e);
        } catch (Exception e) {
            Log.log.error("Error inesperado: " + e);
        } finally {
            conector.closeConnection(con);
        }
    }
    
    /**
     * Cambia la password del usuario por una nueva.
     * @param usuario Nombre del usuario para cambiar la password
     * @param password nueva password para el usuario
     * @param rol rol del usuario que se desea cambiar
     */
    public static void cambiarPassword(String usuario, String password, String rol) {
        ConectionDDBB conector = new ConectionDDBB();
        Connection con = null;
        try {
            con = conector.obtainConnection(true);
            Log.log.info("Database Connected");
            PreparedStatement ps;
            if (rol.equals("empleado")) {
                ps = ConectionDDBB.EditarPwEmpleado(con);
            } else {
                ps = ConectionDDBB.EditarPwEncargado(con);
            }
            String pw_hash = BCrypt.hashpw(password, BCrypt.gensalt());
            // Parametros de la sentencia
            ps.setString(1, pw_hash);
            ps.setString(2, usuario);

            Log.log.info("Query => " + ps.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            Log.log.error("Error al insertar cambiar password: " + e);
        } catch (NullPointerException e) {
            Log.log.error("Error: " + e);
        } catch (Exception e) {
            Log.log.error("Error inesperado: " + e);
        } finally {
            conector.closeConnection(con);
        }
    }

    /**
     * Obtiene una lista con los últimos 20 registros de cualquier empleado
     * @param usuario Nombre del usuario para consultar los registros
     * @return lista de los registros
     */
    public static List<Registro> getRegistrosRecientes(String usuario) {
        ConectionDDBB conector = new ConectionDDBB();
        Connection con = null;
        List<Registro> resultado = new ArrayList<>();
        try {
            con = conector.obtainConnection(true);
            Log.log.info("Database Connected");
            PreparedStatement ps;
            ps = ConectionDDBB.GetUltimosRegistros(con);
            
            // Parametros de la sentencia
            ps.setString(1, usuario);

            Log.log.info("Query => " + ps.toString());
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()){
                Date fecha = rs.getDate("fecha");
                Time hora = rs.getTime("hora");
                String tipo = rs.getString("tipo");
                int minutosAcumulados = rs.getInt("minutos_acumulados");
                String id = rs.getString("id_registro");
                Registro registro = new Registro(fecha.toString(), hora.toString(), tipo, minutosAcumulados, id);
                resultado.add(registro);
            }
        } catch (SQLException e) {
            Log.log.error("Error al obtener los registros del usuario: " + e);
        } catch (NullPointerException e) {
            Log.log.error("Error: " + e);
        } catch (Exception e) {
            Log.log.error("Error inesperado: " + e);
        } finally {
            conector.closeConnection(con);
        }
        return resultado;
    }
    
    /**
     * Obtiene una lista con todos los registros de cualquier empleado
     * @param usuario Nombre del usuario para consultar los registros
     * @return lista de los registros
     */
    public static List<Registro> getRegistros(String usuario) {
        ConectionDDBB conector = new ConectionDDBB();
        Connection con = null;
        List<Registro> resultado = new ArrayList<>();
        try {
            con = conector.obtainConnection(true);
            Log.log.info("Database Connected");
            PreparedStatement ps;
            ps = ConectionDDBB.GetRegistros(con);
            
            // Parametros de la sentencia
            ps.setString(1, usuario);

            Log.log.info("Query => " + ps.toString());
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()){
                Date fecha = rs.getDate("fecha");
                Time hora = rs.getTime("hora");
                String tipo = rs.getString("tipo");
                int minutosAcumulados = rs.getInt("minutos_acumulados");
                String id = rs.getString("id_registro");
                Registro registro = new Registro(fecha.toString(), hora.toString(), tipo, minutosAcumulados, id);
                resultado.add(registro);
            }
        } catch (SQLException e) {
            Log.log.error("Error al obtener los registros del usuario: " + e);
        } catch (NullPointerException e) {
            Log.log.error("Error: " + e);
        } catch (Exception e) {
            Log.log.error("Error inesperado: " + e);
        } finally {
            conector.closeConnection(con);
        }
        return resultado;
    }
    
    /**
     * Obtiene una lista con todos los registros de cualquier empleado en un rango de fechas
     * @param usuario Nombre del usuario para consultar los registros
     * @param fechaInicial Fecha inicial del rango de fechas
     * @param fechaFinal Fecha final del rango de fechas
     * @return lista de los registros
     */
    public static List<Registro> getRegistrosFecha(String usuario, Date fechaInicial, Date fechaFinal) {
        ConectionDDBB conector = new ConectionDDBB();
        Connection con = null;
        List<Registro> resultado = new ArrayList<>();
        try {
            con = conector.obtainConnection(true);
            Log.log.info("Database Connected");
            PreparedStatement ps;
            ps = ConectionDDBB.GetRegistrosFecha(con);
            
            // Parametros de la sentencia
            ps.setString(1, usuario);
            ps.setDate(2, fechaInicial);
            ps.setDate(3, fechaFinal);

            Log.log.info("Query => " + ps.toString());
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()){
                Date fecha = rs.getDate("fecha");
                Time hora = rs.getTime("hora");
                String tipo = rs.getString("tipo");
                int minutosAcumulados = rs.getInt("minutos_acumulados");
                String id = rs.getString("id_registro");
                Registro registro = new Registro(fecha.toString(), hora.toString(), tipo, minutosAcumulados, id);
                resultado.add(registro);
            }
        } catch (SQLException e) {
            Log.log.error("Error al obtener los registros del usuario: " + e);
        } catch (NullPointerException e) {
            Log.log.error("Error: " + e);
        } catch (Exception e) {
            Log.log.error("Error inesperado: " + e);
        } finally {
            conector.closeConnection(con);
        }
        return resultado;
    }
    
    /**
     * Obtiene una lista con los nombres de todos los empleados
     * @return lista de nombres
     */
    public static List<String> getEmpleados() {
        ConectionDDBB conector = new ConectionDDBB();
        Connection con = null;
        List<String> resultado = new ArrayList<>();
        try {
            con = conector.obtainConnection(true);
            Log.log.info("Database Connected");
            PreparedStatement ps;
            ps = ConectionDDBB.GetNombreEmpleados(con);

            Log.log.info("Query => " + ps.toString());
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()){
                resultado.add(rs.getString("nombre_empleado"));
            }
        } catch (SQLException e) {
            Log.log.error("Error al obtener la lista de empleados: " + e);
        } catch (NullPointerException e) {
            Log.log.error("Error: " + e);
        } catch (Exception e) {
            Log.log.error("Error inesperado: " + e);
        } finally {
            conector.closeConnection(con);
        }
        return resultado;
    }
    
    /**
     * Intenta crear un registro en la base de datos en funcion de los parametros indicados
     * @param usuario Nombre del empleado para crear el registro
     * @param fecha Fecha del registro
     * @param hora Hora del registro
     * @param tipo Entrada o salida
     * @param minutosAcumulados minutos acumulados en registros de salida
     */
    public static void crearRegistro(String usuario, Date fecha, Time hora, String tipo, int minutosAcumulados) {
        ConectionDDBB conector = new ConectionDDBB();
        Connection con = null;
        try {
            con = conector.obtainConnection(true);
            Log.log.info("Database Connected");
            PreparedStatement ps;
            ps = ConectionDDBB.CrearRegistro(con);
            
            // Parametros de la sentencia
            ps.setString(1, usuario);
            ps.setDate(2, fecha);
            ps.setTime(3, hora);
            ps.setString(4, tipo);
            ps.setInt(5, minutosAcumulados);

            Log.log.info("Query => " + ps.toString());
            ps.executeUpdate();
            
        } catch (SQLException e) {
            Log.log.error("Error al crear registro: " + e);
        } catch (NullPointerException e) {
            Log.log.error("Error: " + e);
        } catch (Exception e) {
            Log.log.error("Error inesperado: " + e);
        } finally {
            conector.closeConnection(con);
        }
    }
    
    /**
     * Obtiene el ultimo registro de un empleado concreto
     * @param usuario Nombre del usuario para consultar el registro
     * @return el ultimo registro
     */
    public static RegistroRaw getUltimoRegistro(String usuario) {
        ConectionDDBB conector = new ConectionDDBB();
        Connection con = null;
        RegistroRaw registro = null;
        try {
            con = conector.obtainConnection(true);
            Log.log.info("Database Connected");
            PreparedStatement ps;
            ps = ConectionDDBB.GetUltimoRegistro(con);
            
            // Parametros de la sentencia
            ps.setString(1, usuario);

            Log.log.info("Query => " + ps.toString());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()){
                Date fecha = rs.getDate("fecha");
                Time hora = rs.getTime("hora");
                String tipo = rs.getString("tipo");
                int minutosAcumulados = rs.getInt("minutos_acumulados");
                String id = rs.getString("id_registro");
                registro = new RegistroRaw(fecha, hora, tipo, minutosAcumulados, id);
            }
        } catch (SQLException e) {
            Log.log.error("Error al obtener el ultimo registro del usuario: " + e);
        } catch (NullPointerException e) {
            Log.log.error("Error: " + e);
        } catch (Exception e) {
            Log.log.error("Error inesperado: " + e);
        } finally {
            conector.closeConnection(con);
        }
        return registro;
    }
    
    /**
     * Intenta crear una nota en un registro
     * @param idRegistro id del registro donde se escribe la nota
     * @param usuario Nombre del usuario que escribe la nota
     * @param fecha Fecha de escritura
     * @param hora Hora de escritura
     * @param textoNota Contenido de la nota
     */
    public static void crearNota(int idRegistro, String usuario, Date fecha, Time hora, String textoNota) {
        ConectionDDBB conector = new ConectionDDBB();
        Connection con = null;
        try {
            con = conector.obtainConnection(true);
            Log.log.info("Database Connected");
            PreparedStatement ps;
            ps = ConectionDDBB.CrearNota(con);
            
            // Parametros de la sentencia
            ps.setInt(1, idRegistro);
            ps.setString(2, usuario);
            ps.setDate(3, fecha);
            ps.setTime(4, hora);
            ps.setString(5, textoNota);

            Log.log.info("Query => " + ps.toString());
            ps.executeUpdate();
            
        } catch (SQLException e) {
            Log.log.error("Error al crear registro: " + e);
        } catch (NullPointerException e) {
            Log.log.error("Error: " + e);
        } catch (Exception e) {
            Log.log.error("Error inesperado: " + e);
        } finally {
            conector.closeConnection(con);
        }
    }
    
    /**
     * Obtiene el nombre del usuario de un registro concreto
     * @param idRegistro el id del registro a consultar
     * @return nombre del usuario
     */
    public static String getUsuarioRegistro (int idRegistro) {
        ConectionDDBB conector = new ConectionDDBB();
        Connection con = null;
        String nombre = "";
        try {
            con = conector.obtainConnection(true);
            Log.log.info("Database Connected");
            PreparedStatement ps;
            ps = ConectionDDBB.GetUsuarioRegistro(con);
            
            // Parametros de la sentencia
            ps.setInt(1, idRegistro);

            Log.log.info("Query => " + ps.toString());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()){
                nombre = rs.getString("nombre_empleado");
            }
        } catch (SQLException e) {
            Log.log.error("Error al obtener el usuario de un registro: " + e);
        } catch (NullPointerException e) {
            Log.log.error("Error: " + e);
        } catch (Exception e) {
            Log.log.error("Error inesperado: " + e);
        } finally {
            conector.closeConnection(con);
        }
        return nombre;
    }
    
    /**
     * Obtiene las notas asociadas a un registro
     * @param idRegistro el id del registro a consultar
     * @return Lista de las notas del registro
     */
    public static List<Nota> getNotasRegistro(int idRegistro) {
        ConectionDDBB conector = new ConectionDDBB();
        Connection con = null;
        List<Nota> resultado = new ArrayList<>();
        try {
            con = conector.obtainConnection(true);
            Log.log.info("Database Connected");
            PreparedStatement ps;
            ps = ConectionDDBB.GetNotasRegistro(con);
            
            // Parametros de la sentencia
            ps.setInt(1, idRegistro);

            Log.log.info("Query => " + ps.toString());
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()){
                String autor = rs.getString("autor");
                Date fecha = rs.getDate("fecha");
                Time hora = rs.getTime("hora");
                String texto = rs.getString("texto");
                Nota nota = new Nota(autor, fecha.toString(), hora.toString(), texto);
                resultado.add(nota);
            }
        } catch (SQLException e) {
            Log.log.error("Error al obtener las notas del registro: " + e);
        } catch (NullPointerException e) {
            Log.log.error("Error: " + e);
        } catch (Exception e) {
            Log.log.error("Error inesperado: " + e);
        } finally {
            conector.closeConnection(con);
        }
        return resultado;
    }
}
