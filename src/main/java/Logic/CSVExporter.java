/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author ruben
 */
public class CSVExporter {

    private static final String SEPARADOR = ";";

    /**
     * Convierte una lista de registros a .csv, incluyendo las notas.
     *
     * @param lista lista de registros
     * @return cadena de texto que representa el archivo .csv
     */
    public static String registrosToCSV(List<Registro> lista) {
        StringBuilder sb = new StringBuilder();
        // Cabecera de nombre de empresa
        String nombre = TextoCustomProvider.obtenerTexto(ContextHolder.getContext());
        sb.append(nombre).append("\n");
        // Cabecera
        sb.append("Fecha").append(SEPARADOR)
                .append("Hora").append(SEPARADOR)
                .append("Tipo").append(SEPARADOR)
                .append("Minutos acumulados").append(SEPARADOR)
                .append("Notas").append("\n");

        for (Registro r : lista) {
            sb.append(r.getFecha()).append(SEPARADOR)
                    .append(r.getHora()).append(SEPARADOR)
                    .append(r.getTipo()).append(SEPARADOR)
                    .append(r.getMinutosAcumulados()).append(SEPARADOR);
            // Notas
            for (Nota n : Logic.getNotasRegistro(Integer.parseInt(r.getId()))) {
                sb.append(n.getAutor()).append(" (")
                        .append(n.getFecha()).append(" a las ")
                        .append(n.getHora()).append("): ")
                        .append(n.getTexto()).append(SEPARADOR);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Gestiona los caracteres especiales que pueden dar problemas en csv
     *
     * @param value cadena original
     * @return cadena con los caracteres especiales gestionados
     */
    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }

        boolean contieneCaracteresEspeciales
                = value.contains(SEPARADOR) || value.contains("\"")
                || value.contains("\n") || value.contains("\r");

        if (contieneCaracteresEspeciales) {
            // Escapar comillas dobles duplicándolas
            value = value.replace("\"", "\"\"");
            // Encerrar en comillas
            return "\"" + value + "\"";
        }

        return value;
    }

    /**
     * Comprime varios CSV en un ZIP.
     *
     * @param csvFiles lista de strings con contenido CSV
     * @param fileNames lista de nombres de archivo (ej: "empleados.csv"), debe
     * tener el mismo tamaño que csvFiles
     * @return un array de bytes que representa el ZIP
     * @throws java.io.IOException
     */
    public static byte[] toZip(List<String> csvFiles, List<String> fileNames) throws IOException {
        if (csvFiles.size() != fileNames.size()) {
            throw new IllegalArgumentException("El número de CSVs y nombres de archivo debe coincidir");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (int i = 0; i < csvFiles.size(); i++) {
                String fileName = fileNames.get(i) + ".csv";
                String csvContent = csvFiles.get(i);

                ZipEntry entry = new ZipEntry(fileName);
                zos.putNextEntry(entry);

                byte[] data = csvContent.getBytes("UTF-8"); // codificacion segura
                zos.write(data, 0, data.length);

                zos.closeEntry();
            }

            zos.finish();
            return baos.toByteArray();
        }
    }
}
