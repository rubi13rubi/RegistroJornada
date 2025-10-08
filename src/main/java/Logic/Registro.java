/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;


public class Registro {
    
    private String fecha;
    private String hora;
    private String tipo;
    private String id;
    private int minutosAcumulados;

    public Registro(String fecha, String hora, String tipo, int minutosAcumulados, String id) {
        this.fecha = fecha;
        this.hora = hora;
        this.tipo = tipo;
        this.minutosAcumulados = minutosAcumulados;
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getTipo() {
        return tipo;
    }

    public String getId() {
        return id;
    }

    public int getMinutosAcumulados() {
        return minutosAcumulados;
    }
    
    
}
