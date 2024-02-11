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

public class AltaServicio implements WindowListener, ActionListener {
	// Diseño de la ventana: componentes
	Frame ventana = new Frame("Alta Servicio");
	Dialog dlgMensaje = new Dialog(ventana, "Mensaje", true);
	Dialog dlgMensajeError = new Dialog(ventana, "Mensaje", true);

	Label lblAlta = new Label("--------- Alta de Servicios ---------");
	Label lblNombreMenu = new Label("Nombre del menú:");
	Label lblIngredientesMenu = new Label("Ingredientes del menú:");
	Label lblPrecioMenu = new Label("Precio del menú:");

	Label lblMensajeAlta = new Label("Alta de Servicio Correcta");
	Label lblMensajeError = new Label("Los Campos están vacíos");
	Label lblMensaje = new Label("Alta de Servicio Correcta");

	TextField txtNombreMenu = new TextField(20);
	TextField txtIngredienteMenu = new TextField(20);
	TextField txtPrecioMenu = new TextField(20);

	Button btnAceptar = new Button("Aceptar");
	Button btnLimpiar = new Button("Limpiar");

	Conexion conexion = new Conexion();
	String usuario = "";

	// Constructor
	public AltaServicio(String u) {
		usuario = u;
		ventana.setLayout(new FlowLayout());
		ventana.setSize(300, 280); // Ajustar tamaño de la ventana

		ventana.addWindowListener(this);
		dlgMensaje.addWindowListener(this);
		dlgMensajeError.addWindowListener(this);
		btnAceptar.addActionListener(this);
		btnLimpiar.addActionListener(this);

		ventana.add(lblAlta);
		ventana.add(lblNombreMenu);
		ventana.add(txtNombreMenu);
		ventana.add(lblIngredientesMenu);
		ventana.add(txtIngredienteMenu);
		ventana.add(lblPrecioMenu);
		ventana.add(txtPrecioMenu);

		ventana.add(btnAceptar);
		ventana.add(btnLimpiar);

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
		if (evento.getSource().equals(btnAceptar)) {
			dlgMensaje.setLayout(new FlowLayout());
			dlgMensaje.setSize(170, 200);
			dlgMensaje.addWindowListener(this);

			if (txtNombreMenu.getText().length() == 0 || txtIngredienteMenu.getText().length() == 0
					|| txtPrecioMenu.getText().length() == 0) {
				System.out.println("Campos vacíos");
				dlgMensajeError.add(lblMensajeError);
				dlgMensajeError.setSize(180, 75);
				dlgMensajeError.setLayout(new FlowLayout());
				dlgMensajeError.setResizable(false);
				dlgMensajeError.add(lblMensajeAlta);
				dlgMensajeError.setLocationRelativeTo(null);
				dlgMensajeError.setBackground(new Color(30, 144, 255));
				dlgMensajeError.setVisible(true);
			} else {
				conexion.conectar();
				String nombreMenu = txtNombreMenu.getText();
				String ingredientesMenu = txtIngredienteMenu.getText();
				double precioMenu = obtenerPrecioMenu(txtPrecioMenu.getText());

				// Construir la sentencia SQL para insertar el servicio
				String sentencia = "INSERT INTO servicios (nombreMenu, ingredienteMenu, precioMenu) VALUES ('" + nombreMenu + "', '"
						+ ingredientesMenu + "', " + precioMenu + ")";

				// Insertar el servicio en la base de datos
				int resultado = conexion.insertarServicios(sentencia);

				if (resultado == 0) {
                    lblMensajeAlta.setText("Alta servicio correcta");
                    dlgMensaje.setBackground(new Color(0, 102, 204));
                    conexion.apunteLog(usuario, sentencia);
                    
                    txtNombreMenu.setText("");
                    txtIngredienteMenu.setText("");
                    txtPrecioMenu.setText("");
                    
                    
                } else {
                    lblMensajeAlta.setText("Error en Alta");
                    dlgMensaje.setBackground(new Color(0, 51, 102));
                    conexion.apunteLog(usuario, "ERROR INSERT INTO SERVICIOS");
                }

				// Desconectar
				conexion.desconectar();

				// Características del diálogo
				dlgMensaje.setSize(180, 75);
				dlgMensaje.setLayout(new FlowLayout());
				dlgMensaje.setResizable(false);
				dlgMensaje.add(lblMensajeAlta);
				dlgMensaje.setLocationRelativeTo(null);

				dlgMensaje.setVisible(true);
				
				this.limpiar();
			}
		} else if (evento.getSource().equals(btnLimpiar)) {
			limpiar();
		}
	}

	// Nuevo método para obtener el precio como tipo double desde un String
	private double obtenerPrecioMenu(String precioStr) {
		try {
			return Double.parseDouble(precioStr);
		} catch (NumberFormatException e) {
			// Si ocurre un error al convertir el precio, se muestra un mensaje y se retorna 0.0
			System.out.println("Error al convertir el precio a número. Se establecerá el precio a 0.0");
			return 0.0;
		}
	}

	private void limpiar() {
		txtNombreMenu.selectAll();
		txtNombreMenu.setText("");
		txtIngredienteMenu.selectAll();
		txtIngredienteMenu.setText("");
		txtPrecioMenu.setText("");
		txtNombreMenu.requestFocus();
	}
}
