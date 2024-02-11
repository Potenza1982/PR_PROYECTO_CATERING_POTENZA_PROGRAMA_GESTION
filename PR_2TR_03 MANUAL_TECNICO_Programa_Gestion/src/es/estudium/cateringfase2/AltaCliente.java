package es.estudium.cateringfase2;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class AltaCliente implements WindowListener, ActionListener {
    // Diseño de la ventana: componentes
	Frame ventana = new Frame("Alta de Cliente");
    Dialog dlgMensaje = new Dialog(ventana, "Mensaje", true);
    Dialog dlgMensajeError = new Dialog(ventana, "Mensaje", true);

    Label lblTitulo = new Label("------------ Alta de Clientes ------------");

    Label lblNombre = new Label("Nombre:");
    Label lblApellidos = new Label("Apellidos:");
    Label lblDireccion = new Label("Dirección:");
    Label lblTelefono = new Label("Teléfono:");

    Label lblMensajeAlta = new Label("Alta de Cliente Correcta");
    Label lblMensajeError = new Label("Los campos están vacíos");

    TextField txtNombreCliente = new TextField(20);
    TextField txtApellidosCliente = new TextField(20);
    TextField txtDireccionCliente = new TextField(20);
    TextField txtTelefonoCliente = new TextField(20);
    
    Button btnAceptar = new Button("Aceptar");
    Button btnLimpiar = new Button("Limpiar");

    Conexion conexion = new Conexion();
    String usuario = "";

    // Constructor
    public AltaCliente(String u) {
        usuario = u;
        ventana.setLayout(new FlowLayout());
        ventana.setSize(260, 340);

        // Listener
        ventana.addWindowListener(this);
        dlgMensaje.addWindowListener(this);
        dlgMensajeError.addWindowListener(this);
        btnAceptar.addActionListener(this);
        btnLimpiar.addActionListener(this);

        // añadimos los componentes a la ventana según su orden de aparición
        ventana.add(lblTitulo);

        ventana.add(lblNombre);
        ventana.add(txtNombreCliente);

        ventana.add(lblApellidos);
        ventana.add(txtApellidosCliente);

        ventana.add(lblDireccion);
        ventana.add(txtDireccionCliente);

        ventana.add(lblTelefono);
        ventana.add(txtTelefonoCliente);

        ventana.add(btnAceptar);
        ventana.add(btnLimpiar);

        // características ventana
        ventana.setResizable(false);
        ventana.setLocationRelativeTo(null);
        ventana.setBackground(new Color(173, 216, 230));
        ventana.setVisible(true);
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (dlgMensaje.isActive()) {
            dlgMensaje.setVisible(false);
        } else if (dlgMensajeError.isActive()) {
            dlgMensajeError.setVisible(false);
        } else {
            ventana.setVisible(false);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        // Nuevo Cliente
        if (evento.getSource().equals(btnAceptar)) {
            dlgMensaje.setLayout(new FlowLayout());
            dlgMensaje.setSize(170, 200);
            dlgMensaje.addWindowListener(this);

            // condición: si los campos están vacíos, mostramos un error
            if (txtNombreCliente.getText().length() == 0 ||
                    txtApellidosCliente.getText().length() == 0 ||
                    txtDireccionCliente.getText().length() == 0 ||
                    txtTelefonoCliente.getText().length() == 0 ) 
            {
                // características del dialogo de error
                System.out.println("Campos vacios");
                dlgMensajeError.add(lblMensajeError);
                dlgMensajeError.setSize(180, 75);
                dlgMensajeError.setLayout(new FlowLayout());
                dlgMensajeError.setResizable(false);
                dlgMensajeError.add(lblMensajeAlta);
                dlgMensajeError.setLocationRelativeTo(null);
                dlgMensajeError.setBackground(new Color(0, 76, 153));
                dlgMensajeError.setVisible(true);
            } else {
                // Conectar
                conexion.conectar();

                // creamos cadenas y metemos dentro de las cadenas los datos del formulario
                String nombre = txtNombreCliente.getText();
                String apellidos = txtApellidosCliente.getText();
                String direccion = txtDireccionCliente.getText();
                String telefono = txtTelefonoCliente.getText();

                // Hacer el INSERT INTO con esos datos

                // la cadena sentencia es la que le mandamos al método insertarClientes implementado en la clase Conexion
                String sentencia = "INSERT INTO Clientes VALUES(null,'" +
                        nombre + "','" +
                        apellidos + "','" +
                        direccion + "','" +
                        telefono + "');";

                int resultado = conexion.insertarClientes(sentencia);
                
                if (resultado == 0) {
                    lblMensajeAlta.setText("Alta cliente correcta");
                    dlgMensaje.setBackground(new Color(0, 102, 204));
                    conexion.apunteLog(usuario, sentencia);
                    
                    txtNombreCliente.setText("");
                    txtApellidosCliente.setText("");
                    txtDireccionCliente.setText("");
                    txtTelefonoCliente.setText("");
                    
                } else {
                    lblMensajeAlta.setText("Error en Alta");
                    dlgMensaje.setBackground(new Color(0, 51, 102));
                    conexion.apunteLog(usuario, "ERROR INSERT INTO CLIENTES");
                }
                // Desconectar
                conexion.desconectar();
                System.out.println(sentencia);

                // características del diálogo
                dlgMensaje.setSize(180, 75);
                dlgMensaje.setLayout(new FlowLayout());
                dlgMensaje.setResizable(false);
                dlgMensaje.add(lblMensajeAlta);
                dlgMensaje.setLocationRelativeTo(null);

                dlgMensaje.setVisible(true);
            }
        }
        // Limpiar
        else if (evento.getSource().equals(btnLimpiar)) {
            limpiar();
        }
    }

    private void limpiar() {
        txtNombreCliente.selectAll();
        txtNombreCliente.setText("");
        txtApellidosCliente.selectAll();
        txtApellidosCliente.setText("");
        txtDireccionCliente.setText("");
        txtDireccionCliente.selectAll();
        txtTelefonoCliente.setText("");
        txtTelefonoCliente.selectAll();
        txtNombreCliente.requestFocus();
    }
}
