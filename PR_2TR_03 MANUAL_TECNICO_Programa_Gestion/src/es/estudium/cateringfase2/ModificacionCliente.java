package es.estudium.cateringfase2;

import java.awt.Button;
import java.awt.Choice;
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
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModificacionCliente implements WindowListener, ActionListener {
	Frame ventana = new Frame("Modificación de Cliente");
	Label lblCabecera = new Label("Elegir el Cliente a Editar:");
	Choice choClientes = new Choice();
	Button btnEditar = new Button("Editar");

	Dialog dlgEditar = new Dialog(ventana, "Edición Cliente", true);

	Label lblCabecera2 = new Label("-------Editando el Cliente nº -------");

	Label lblId = new Label("Id Cliente:");
	Label lblNombre = new Label("Nombre:");
	Label lblApellidos = new Label("Apellidos:");
	Label lblDireccion = new Label("Dirección:");
	Label lblTelefono = new Label("Teléfono:");

	TextField txtId = new TextField(20);
	TextField txtNombre = new TextField(20);
	TextField txtApellidos = new TextField(20);
	TextField txtDireccion = new TextField(20);
	TextField txtTelefono = new TextField(20);
	
	Button btnModificar = new Button("Modificar");
	Button btnCancelar = new Button("Cancelar");
	Button btnVolver = new Button("Volver");

	Dialog dlgMensaje = new Dialog(ventana, "Mensaje", true);
	Label lblMensaje = new Label("XXXXXXXXXXXXXX");

	Dialog dlgModificacion = new Dialog(ventana, "Modificación", true);
	Label lblModificacion = new Label("Modificación Correcta");

	Dialog dlgModificacion2 = new Dialog(ventana, "Advertencia", true);
	Label lblModificacion2 = new Label("Error, campos vacíos");

	Conexion conexion = new Conexion();
	ResultSet rs = null;
	String usuario = "";
	
	//public 
	ModificacionCliente(String u) 
	{
		usuario = u;
		// bd.apunteLogUser("Modificaci�n de Socios");
		// Listener
		ventana.addWindowListener(this);
		btnEditar.addActionListener(this);
		btnModificar.addActionListener(this);
		btnCancelar.addActionListener(this);
		btnVolver.addActionListener(this);
		dlgEditar.addWindowListener(this);
		dlgMensaje.addWindowListener(this);
		dlgModificacion.addWindowListener(this);
		dlgModificacion2.addWindowListener(this);

		// caracteristicas ventana
		ventana.setSize(390, 130);
		ventana.setResizable(false);
		// Layout
		ventana.setLayout(new FlowLayout());
		//rellenamos la ventana por orden de aparición
		ventana.add(lblCabecera);
		rellenarChoiceClientes();//implementado en esta clase
		ventana.add(choClientes);
		ventana.add(btnEditar);
		ventana.add(btnCancelar);

		ventana.setBackground(new Color(255, 204, 153));
		ventana.setLocationRelativeTo(null); //fijar que la ventana salga siempre en el medio
		ventana.setVisible(true);// Mostrarla
	}

	private void rellenarChoiceClientes() {
		choClientes.removeAll();// actualizar lor elementos del choice
		// Rellenar Choice con un primer elemento
		choClientes.add("Elegir Clientes ...");
		// Conectar a la BD
		conexion.conectar();
		// Sacar los datos de la tabla socios
		rs = conexion.rellenarClientes();//implementado en clase BaseDatos
		
		// Registro a registro, meterlos en el Choice
		try 
		{
			while (rs.next()) 
			{
				choClientes.add(rs.getInt("idCliente") + "-" +
						rs.getString("nombreCliente") + " " +
						rs.getString("apellidosCliente") + " " +
						rs.getString("direccionCliente") + "-" +
						rs.getString("telefonoCliente"));
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		// Desconectar de la BD
		conexion.desconectar();
	}

	public void windowActivated(WindowEvent we) {}
	public void windowClosed(WindowEvent we) {}
	public void windowClosing(WindowEvent we) 
	{
		if (dlgMensaje.isActive()) 
		{
			dlgMensaje.setVisible(false);
		} 
		else if (dlgEditar.isActive()) 
		{
			dlgEditar.setVisible(false);
		} 
		else if (dlgModificacion.isActive()) 
		{
			dlgModificacion.setVisible(false);
		} 
		else if (dlgModificacion2.isActive()) 
		{
			dlgModificacion2.setVisible(false);
		} 
		else 
		{
			ventana.setVisible(false);
		}
	}
	public void windowDeactivated(WindowEvent we) {}
	public void windowDeiconified(WindowEvent we) {}
	public void windowIconified(WindowEvent we) {}
	public void windowOpened(WindowEvent we) {}

	@Override
	public void actionPerformed(ActionEvent evento) 
	{
		if (evento.getSource().equals(btnCancelar)) 
		{
			ventana.setVisible(false);
		}
		else if (evento.getSource().equals(btnVolver)) 
		{
			dlgEditar.setVisible(false);
		} 
		else if (evento.getSource().equals(btnEditar)) 
		{
			// Si NO está seleccionada la primera opción, entonces confirmamos edito
			if (choClientes.getSelectedIndex() != 0) 
			{
				String[] seleccionado = choClientes.getSelectedItem().split("-");// hacemos un array con los clientes separados por el - mediante el split
				// Conectar a la BD y sacar los datos del cliente seleccionado
				conexion.conectar();
				// hacemos una consulta a la tabla cliente para rellenar la pantalla de modificacion y metemos en el array el id cortado por el split
				rs = conexion.consultaModificacionClientes(seleccionado[0]);
				try 
				{// sacamos un cliente a editar y  vamos metiendo los datos que nos interesen del cliente elegido
					rs.next();
					txtNombre.setText(rs.getString("nombreCliente"));
					txtApellidos.setText(rs.getString("apellidosCliente"));
					txtDireccion.setText(rs.getString("direccionCliente"));
					txtTelefono.setText(rs.getString("telefonoCliente"));

				} 
				catch (SQLException sqle) 
				{
					sqle.printStackTrace();
				}
				conexion.desconectar();

				// Muestro los datos del cliente elegido en la pantalla de edición
				dlgEditar.setSize(250, 400);
				dlgEditar.setResizable(false);
				
				dlgEditar.setLayout(new FlowLayout());	
				dlgEditar.add(lblCabecera2);
				dlgEditar.add(lblId);
				txtId.setEnabled(false);
				txtId.setText(seleccionado[0]); // rs.getInt("idCliente"); id selecionado en el choice con el split
				dlgEditar.add(txtId);
				
				dlgEditar.add(lblNombre);
				dlgEditar.add(txtNombre);
				
				dlgEditar.add(lblApellidos);
				dlgEditar.add(txtApellidos);
				
				dlgEditar.add(lblDireccion);
				dlgEditar.add(txtDireccion);
				
				dlgEditar.add(lblTelefono);
				dlgEditar.add(txtTelefono);
				
				dlgEditar.add(btnModificar);
				dlgEditar.add(btnVolver);
				dlgEditar.setLocationRelativeTo(null);
				dlgEditar.setBackground(new Color(255, 204, 153));
				dlgEditar.setVisible(true);
			}
		} else if (txtNombre.getText().length() == 0 || txtApellidos.getText().length() == 0 
				|| txtDireccion.getText().length() == 0 || txtTelefono.getText().length() == 0) 
		{
			dlgModificacion2.setSize(180, 75);
			dlgModificacion2.setLayout(new FlowLayout());
			dlgModificacion2.add(lblModificacion2);
			dlgModificacion2.setBackground(new Color(255, 140, 0));
			dlgModificacion2.setLocationRelativeTo(null);
			dlgModificacion2.setVisible(true);
		} else if (evento.getSource().equals(btnModificar)) {
			conexion.conectar();
			int resultado = conexion.actualizarClientes(txtId.getText(), txtNombre.getText(), txtApellidos.getText(), txtDireccion.getText(), txtTelefono.getText());
			conexion.desconectar();
			rellenarChoiceClientes();
			if (resultado == 0) {
				dlgModificacion.setSize(180, 75);
				dlgModificacion.setLayout(new FlowLayout());
				lblModificacion.setText("Modificación Correcta");
				dlgModificacion.add(lblModificacion);
				conexion.apunteLog(usuario, "UPDATE Cliente " + txtId.getText());
				dlgModificacion.setBackground(new Color(255, 99, 71));
				dlgModificacion.setLocationRelativeTo(null);
				dlgModificacion.setVisible(true);
			}
		}
	}
}
