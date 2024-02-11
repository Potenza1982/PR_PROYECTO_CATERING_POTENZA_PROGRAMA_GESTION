package es.estudium.cateringfase2;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class Ayuda {
    private String usuario;
    private Conexion conexion;

    public Ayuda(String u) {
        usuario = u;
        conexion = new Conexion();
        abrirArchivoAyuda();
    }

    private void abrirArchivoAyuda() {
        String ayuda = "AyudaCatering.chm";

        try {
            File archivoAyuda = new File(ayuda);
            Desktop.getDesktop().open(archivoAyuda);
        } catch (IOException e) {
            // Manejo adecuado de la excepción, como mostrar un mensaje de error al usuario o registrar el error
            e.printStackTrace();
            conexion.apunteLog(usuario, "Error al abrir el archivo de ayuda: " + e.getMessage());
        }
        
        // Registrando la acción en el log (asegúrate de que el método apunteLog esté implementado en Conexion)
        conexion.apunteLog(usuario, "AYUDA");
    }
}
