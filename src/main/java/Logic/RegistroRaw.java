/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

import java.sql.Date;
import java.sql.Time;

/**
 * Clase registro sin pasar a String la fecha y hora
 */
public class RegistroRaw {

    private Date fecha;
    private Time hora;
    private String tipo;
    private String id;
    private int horasAcumuladas;

    public RegistroRaw(Date fecha, Time hora, String tipo, int horasAcumuladas, String id) {
        this.fecha = fecha;
        this.hora = hora;
        this.tipo = tipo;
        this.horasAcumuladas = horasAcumuladas;
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public Time getHora() {
        return hora;
    }

    public String getTipo() {
        return tipo;
    }

}
