package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import Logic.Log;

public class ConectionDDBB {

    public Connection obtainConnection(boolean autoCommit) throws NullPointerException {
        Connection con = null;
        int intentos = 5;
        for (int i = 0; i < intentos; i++) {
            Log.log.info("Attempt " + i + " to connect to the database");
            try {
                Context ctx = new InitialContext();
                // Get the connection factory configured in Tomcat
                DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/registrosDB");
                
                // Obtiene una conexion
                con = ds.getConnection();
                Calendar calendar = Calendar.getInstance();
                java.sql.Date date = new java.sql.Date(calendar.getTime().getTime());
                Log.log.debug("Connection creation. Bd connection identifier: " + con.toString() + " obtained in " + date.toString());
                con.setAutoCommit(autoCommit);
                Log.log.info("Conection obtained in the attempt: " + i);
                i = intentos;
            } catch (NamingException ex) {
                Log.log.error("Error getting connection while trying: " + i + " = " + ex.toString());
            } catch (SQLException ex) {
                Log.log.error("ERROR sql getting connection while trying: " + i + " = " + ex.getSQLState() + "\n" + ex.toString());
                throw (new NullPointerException("SQL connection is null"));
            }
        }
        return con;
    }

    public void closeTransaction(Connection con) {
        try {
            con.commit();
            Log.log.debug("Transaction closed");
        } catch (SQLException ex) {
            Log.log.error("Error closing the transaction: " + ex);
        }
    }

    public void cancelTransaction(Connection con) {
        try {
            con.rollback();
            Log.log.debug("Transaction canceled");
        } catch (SQLException ex) {
            Log.log.error("ERROR sql when canceling the transation: " + ex.getSQLState() + "\n" + ex.toString());
        }
    }

    public void closeConnection(Connection con) {
        try {
            Log.log.info("Closing the connection");
            if (null != con) {
                Calendar calendar = Calendar.getInstance();
                java.sql.Date date = new java.sql.Date(calendar.getTime().getTime());
                Log.log.debug("Connection closed. Bd connection identifier: " + con.toString() + " obtained in " + date.toString());
                con.close();
            }

            Log.log.info("The connection has been closed");
        } catch (SQLException e) {
            Log.log.error("ERROR sql closing the connection: " + e);
            e.printStackTrace();
        }
    }

    public static PreparedStatement getStatement(Connection con, String sql) {
        PreparedStatement ps = null;
        try {
            if (con != null) {
                ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            }
        } catch (SQLException ex) {
            Log.log.warn("ERROR sql creating PreparedStatement: " + ex.toString());
        }

        return ps;
    }

    public static PreparedStatement CrearEncargado(Connection con) {
        // Crea un nuevo usuario encargado
        return getStatement(con, "INSERT INTO Encargados(nombre_encargado, hash_pw) VALUES (?, ?)");
    }

    public static PreparedStatement CrearEmpleado(Connection con) {
        // Crea un nuevo usuario encargado
        return getStatement(con, "INSERT INTO Empleados(nombre_empleado, hash_pw) VALUES (?, ?)");
    }

    public static PreparedStatement GetEncargados(Connection con) {
        // Crea un nuevo usuario encargado
        return getStatement(con, "SELECT * FROM Encargados");
    }

    public static PreparedStatement GetEmpleados(Connection con) {
        // Crea un nuevo usuario encargado
        return getStatement(con, "SELECT * FROM Empleados");
    }

    public static PreparedStatement GetNombreEmpleados(Connection con) {
        // Crea un nuevo usuario encargado
        return getStatement(con, "SELECT nombre_empleado FROM Empleados");
    }

    public static PreparedStatement GetEncargado(Connection con) {
        // Busca un usuario encargado
        return getStatement(con, "SELECT * FROM Encargados WHERE nombre_encargado = ?");
    }

    public static PreparedStatement GetEmpleado(Connection con) {
        // Busca un usuario empleado
        return getStatement(con, "SELECT * FROM Empleados WHERE nombre_empleado = ?");
    }

    public static PreparedStatement GetUltimosRegistros(Connection con) {
        // Consulta los últimos 20 registros de un empleado en orden descendente por fecha y hora
        return getStatement(con, "SELECT * FROM Registros WHERE nombre_empleado = ? ORDER BY fecha DESC, hora DESC LIMIT 20");
    }

    public static PreparedStatement GetUltimoRegistro(Connection con) {
        // Consulta el ultimo registro de un empleado
        return getStatement(con, "SELECT * FROM Registros WHERE nombre_empleado = ? ORDER BY fecha DESC, hora DESC LIMIT 1");
    }

    public static PreparedStatement GetRegistros(Connection con) {
        // Consulta todos los registros de un empleado en orden descendente por fecha y hora
        return getStatement(con, "SELECT * FROM Registros WHERE nombre_empleado = ? ORDER BY fecha DESC, hora DESC");
    }

    public static PreparedStatement GetRegistrosFecha(Connection con) {
        // Consulta los registros de un empleado en un intervalo de fechas, ordenados por fecha y hora descendente
        return getStatement(con, "SELECT * FROM Registros WHERE nombre_empleado = ? AND fecha BETWEEN ? AND ? ORDER BY fecha DESC, hora DESC");
    }

    public static PreparedStatement CrearRegistro(Connection con) {
        // Inserta un nuevo registro en la tabla Registros
        return getStatement(con, "INSERT INTO Registros(nombre_empleado, fecha, hora, tipo, minutos_acumulados) VALUES (?, ?, ?, ?, ?)");
    }

    public static PreparedStatement EditarPwEmpleado(Connection con) {
        // Actualiza el campo hash_pw de un empleado en la tabla Empleados
        return getStatement(con, "UPDATE Empleados SET hash_pw = ? WHERE nombre_empleado = ?");
    }

    public static PreparedStatement EditarPwEncargado(Connection con) {
        // Actualiza el campo hash_pw de un encargado en la tabla Encargados
        return getStatement(con, "UPDATE Encargados SET hash_pw = ? WHERE nombre_encargado = ?");
    }

    public static PreparedStatement GetNotasRegistro(Connection con) {
        // Devuelve todas las notas asociadas a un registro concreto, ordenadas por fecha y hora
        return getStatement(con,
                "SELECT id_registro, autor, fecha, hora, texto FROM Notas WHERE id_registro = ? ORDER BY fecha DESC, hora DESC");
    }

    public static PreparedStatement GetUsuarioRegistro(Connection con) {
        // Devuelve el nombre del empleado asociado a un id_registro
        return getStatement(con,
                "SELECT nombre_empleado FROM Registros WHERE id_registro = ?");
    }

    public static PreparedStatement CrearNota(Connection con) {
        // Inserta una nueva nota asociada a un registro
        return getStatement(con,
                "INSERT INTO Notas (id_registro, autor, fecha, hora, texto) VALUES (?, ?, ?, ?, ?)");
    }

}
