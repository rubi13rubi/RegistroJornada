/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;


public class Nota {
    
    private String autor;
    private String fecha;
    private String hora;
    private String texto;

    public Nota(String autor, String fecha, String hora, String texto) {
        this.autor = autor;
        this.fecha = fecha;
        this.hora = hora;
        this.texto = texto;
    }

    public String getAutor() {
        return autor;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getTexto() {
        return texto;
    }
    
}
