package es.estudium.cateringfase2;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;

import java.awt.Label;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BajaCliente implements WindowListener, ActionListener {
	// Componentes
	Frame ventana = new Frame("Baja de Cliente");
	Label lblCabecera = new Label("----- ELEGIR EL CLIENTE A ELIMINAR -----");
	Choice choClientes = new Choice();
	Button btnEliminar = new Button("Eliminar");
	Button btnCancelar = new Button("Cancelar");

	Dialog dlgConfirmacion = new Dialog(ventana, "Confirmación", true);
	Label lblConfirmacion = new Label("XXXXXXXXXXXXXXXXXXXXXXX");
	Button btnSi = new Button("Sí");
	Button btnNo = new Button("No");

	Dialog dlgMensaje = new Dialog(ventana, "Mensaje", true);
	Label lblMensaje = new Label("XXXXXXXXXXXXXX");

	Conexion conexion = new Conexion();
	ResultSet rs = null;
	String usuario = "";

	// Constructor
	public BajaCliente(String u) {
		usuario = u;
		//conexion.apunteLogUser("Baja de cliente");
		// Listener
		ventana.addWindowListener(this);
		btnEliminar.addActionListener(this);
		btnCancelar.addActionListener(this);

		ventana.setSize(270, 130);
		ventana.setResizable(false);
		ventana.setBackground(new Color(200, 160, 255));
		ventana.setLayout(new FlowLayout());

		ventana.add(lblCabecera);
		
		// añadimos un primer elemento al Choice
		choClientes.add("Elegir un cliente para eliminar...");
		
		// Conectar a la BD
		conexion.conectar();
		// antes de añadir el choice, hay que rellenarlo con los elementos de la tabla clientes mediante el método rellenarClientes
		rs = conexion.rellenarClientes();

		// Registro a registro, meterlos en el Choice
		try 
		{
			while (rs.next()) 
			{// para rellenar recorremos el resultset con el next y en el choice vamos metiendo lo que saquemos del resultset.
			 // mandamos la variable del resultset al metodo rellenarClientes. En resultset hacemos el select from para rellenar el choice
				choClientes.add(rs.getInt("idCliente") + "-" + rs.getString("nombreCliente") + "-"
						+ rs.getString("apellidosCliente"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// desconectar de la conexión a la BD
		conexion.desconectar();

		ventana.add(choClientes);
		ventana.add(btnEliminar);
		ventana.add(btnCancelar);

		ventana.setLocationRelativeTo(null); // fijar que la ventana salga siempre en el medio
		ventana.setVisible(true); // Mostrarla
	}

	@Override
	public void windowOpened(WindowEvent e) {}
	@Override
	public void windowClosing(WindowEvent e)
	{
		if (dlgMensaje.isActive()) 
		{
			dlgMensaje.setVisible(false);
		}
		if (dlgConfirmacion.isActive()) 
		{
			dlgConfirmacion.setVisible(false);
		} 
		else 
		{
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
	public void actionPerformed(ActionEvent evento) 
	{
		if (evento.getSource().equals(btnCancelar)) 
		{
			ventana.setVisible(false);
		} 
		else if (evento.getSource().equals(btnEliminar)) 
		{
			// Si NO está seleccionada la primera opción, entonces confirmamos eliminación
			if (choClientes.getSelectedIndex()!= 0) 
			{
				dlgConfirmacion.setLayout(new FlowLayout());
				dlgConfirmacion.setSize(250, 100);
				dlgConfirmacion.addWindowListener(this);

				lblConfirmacion.setText("--- ¿Eliminar '" + choClientes.getSelectedItem() + "'? ---");//confirmar eliminación + el socio elegido
				dlgConfirmacion.add(lblConfirmacion);
				btnSi.addActionListener(this);
				dlgConfirmacion.add(btnSi);
				btnNo.addActionListener(this);
				dlgConfirmacion.add(btnNo);
				dlgConfirmacion.setBackground(new Color(215, 175, 255));
				dlgConfirmacion.setResizable(false);
				dlgConfirmacion.setLocationRelativeTo(null);
				dlgConfirmacion.setVisible(true);
			}
		} 
		else if (evento.getSource().equals(btnNo)) 
		{
			dlgConfirmacion.setVisible(false);
		} 
		else if (evento.getSource().equals(btnSi)) 
		{
			conexion.conectar();
			
			// hacemos un array con los socios separados por el - mediante el split
			String[] array = choClientes.getSelectedItem().split("-");
			
			// Hacer el DELETE con el método borrarCliente.
			// El array recibe el id a eliminar y lo mandamos al método borrarCliente. 
			// Metemos el resultado en la vaiable resultado, que recibe un 0 o un 1.
			int resultado = conexion.borrarClientes(Integer.parseInt(array[0]));
			if (resultado == 0) {
				lblMensaje.setText("Borrado con éxito");
				dlgMensaje.setBackground(new Color(100, 80, 130));
				conexion.apunteLog(usuario, "DELETE FROM CLIENTES " + array[0]);
			} else {
				lblMensaje.setText("Error en borrado");
				dlgMensaje.setBackground(new Color(220, 20, 60));
				conexion.apunteLog(usuario, "ERROR DELETE FROM CLIENTES");
			}
			conexion.desconectar();

			dlgMensaje.addWindowListener(this);
			dlgMensaje.setSize(150, 80);
			dlgMensaje.setResizable(false);
			dlgMensaje.setLayout(new FlowLayout());
			dlgMensaje.add(lblMensaje);
			dlgMensaje.setLocationRelativeTo(null);
			dlgMensaje.setVisible(true);
			dlgConfirmacion.setVisible(false);
		}
	}
}
