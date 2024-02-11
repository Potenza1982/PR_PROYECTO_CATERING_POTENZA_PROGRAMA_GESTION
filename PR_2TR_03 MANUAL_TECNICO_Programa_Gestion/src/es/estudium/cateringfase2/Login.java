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

public class Login implements WindowListener, ActionListener {

	// Diseño de la ventana: Componentes
	Frame ventanaLogin = new Frame("Login");
	Dialog dlgMensaje = new Dialog(ventanaLogin, "Advertencia", true);

	Label lblUsuario = new Label("Usuario:");
	Label lblClave = new Label("Clave :");
	Label lblMensaje = new Label("Los datos ingresados no son válidos.");

	TextField txtUsuario = new TextField(20);
	TextField txtClave = new TextField(20);

	Button btnAceptar = new Button("Acceder");
	Button btnCancelar = new Button("Cancelar");
	Conexion conexion = new Conexion();

	// Miguel "USUARIO ADMINISTRADOR" -> Contraseña: Miguel
	// Juan "USUARIO BÁSICO" ----------> Contraseña: Juan
	Login() {
		// WindowListener y ActionListener
		ventanaLogin.addWindowListener(this);
		dlgMensaje.addWindowListener(this);
		btnAceptar.addActionListener(this);
		btnCancelar.addActionListener(this);

		// Layout
		ventanaLogin.setLayout(new FlowLayout());

		// Añadimos los componentes a la ventana por orden de aparición
		ventanaLogin.add(lblUsuario);
		ventanaLogin.add(txtUsuario);
		ventanaLogin.add(lblClave);
		txtClave.setEchoChar('*'); // Para que la contraseña aparezca como *
		ventanaLogin.add(txtClave);
		ventanaLogin.add(btnAceptar);
		ventanaLogin.add(btnCancelar);

		// Características de la ventana
		ventanaLogin.setSize(320, 130);
		ventanaLogin.setResizable(false);
		ventanaLogin.setLocationRelativeTo(null);
		ventanaLogin.setBackground(new Color(127, 255, 212));
		ventanaLogin.setVisible(true);
	}

	public static void main(String[] args) {
		new Login();
	}

	public void actionPerformed(ActionEvent ae)
	{
		//si pulsamos bot�n cancelar, se cierra ventana login
		if (ae.getSource().equals(btnCancelar))
		{
			ventanaLogin.setVisible(false);
			//si pulsamos bot�n aceptar
		} else if (ae.getSource().equals(btnAceptar))
		{
			// Creamos dos cadenas y metemos dentro los textos de los textfield: usuario y clave
			String usuario = txtUsuario.getText();
			String clave = txtClave.getText();

			// Conectar a la base de datos
			conexion.conectar();
			
			int resultado = conexion.consultar("SELECT * FROM usuarios WHERE nombreUsuario = '" + usuario +
			        "' AND claveUsuario = SHA2('" + clave + "', 256);", usuario);


			// Condicion if-else: Si los datos NO son iguales que los de la tabla usuarios: Caso negativo (-1): Mostrar Mensaje Advertencia
			if (resultado == -1) {
				// Características del diálogo
				dlgMensaje.setSize(250, 75);
				dlgMensaje.setLayout(new FlowLayout());
				dlgMensaje.setResizable(false);
				lblMensaje.setText("Los datos ingresados no son válidos.");
				dlgMensaje.add(lblMensaje);
				dlgMensaje.setBackground(new Color(0, 139, 139));
				dlgMensaje.setLocationRelativeTo(null);
				dlgMensaje.setVisible(true);
			} else {
				// Si los datos son iguales: Caso afirmativo (0-1): mostramos objeto de la clase MenúPrincipal
				new MenuPrincipal(resultado, usuario);
				ventanaLogin.setVisible(false);
			}

			// Desconectar de la base de datos
			conexion.desconectar();
		}
	}

	public void windowActivated(WindowEvent we) {}

	public void windowClosed(WindowEvent we) {}

	public void windowClosing(WindowEvent we) {
		if (dlgMensaje.isActive()) { // Si el diálogo está activo
			dlgMensaje.setVisible(false); // Lo hacemos invisible al cerrar el diálogo en la X
		} else {
			System.exit(0); // Si lo que estaba activo era el login, lo cerramos
		}
	}

	public void windowDeactivated(WindowEvent we) {}

	public void windowDeiconified(WindowEvent we) {}

	public void windowIconified(WindowEvent we) {}

	public void windowOpened(WindowEvent we) {}
}
