package es.estudium.cateringfase2;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AltaPedido implements WindowListener, ActionListener {
	// Diseño de la ventana: componentes
	Frame ventana = new Frame("Alta de pedido");
	Dialog dlgMensaje = new Dialog(ventana, "Mensaje", true);

	Label lblTitulo = new Label("-------------------- Alta de pedido -------------------");
	Choice choClientes = new Choice();
	Choice choServicios = new Choice();

	Label lblMensajeAlta = new Label("Alta de Usuario Correcta");
	Label lblMensajeError = new Label("Los campos están vacíos");
	Label lblChoiceClientes = new Label("Elige un cliente:");
	Label lblChoiceServicios = new Label("Elige un servicio:");

	Dialog dlgMensajeError = new Dialog(ventana, "Mensaje", true);

	Button btnAceptar = new Button("Aceptar");
	Button btnLimpiar = new Button("Limpiar");
	Label lblFechaPreparacionPedidos = new Label("Fecha de preparación del pedido:");

	Dialog dlgFeedback = new Dialog(ventana, "Mensaje", true);
	Label lblMensaje = new Label("XXXXXXXXXXXXXXX");

	TextField txtFechaPreparacionPedidos = new TextField(20);
	Conexion conexion = new Conexion();
	ResultSet rs = null;

	int idClienteFK;
	int idServicioFK;

	String usuario = "";

	// Constructor
	public AltaPedido(String u) {
		usuario = u;

		ventana.setLayout(new FlowLayout());
		ventana.setSize(300, 280);

		// Listener
		ventana.addWindowListener(this);
		dlgMensaje.addWindowListener(this);
		dlgMensajeError.addWindowListener(this);
		btnAceptar.addActionListener(this);
		btnLimpiar.addActionListener(this);

		// añadimos los componentes a la ventana segun su orden de aparición
		ventana.add(lblTitulo);

		ventana.add(lblChoiceClientes);
		ventana.add(choClientes);
		ventana.add(lblChoiceServicios);
		ventana.add(choServicios);

		choServicios.setPreferredSize(new Dimension(240, 20));
		choClientes.setPreferredSize(new Dimension(240, 20));

		ventana.add(lblFechaPreparacionPedidos);
		ventana.add(txtFechaPreparacionPedidos);
		txtFechaPreparacionPedidos.setText(this.dameFechaHoy());
		txtFechaPreparacionPedidos.setEnabled(true);
		ventana.add(btnAceptar);
		ventana.add(btnLimpiar);

		ventana.setBackground(new Color(173, 216, 230));
		ventana.setVisible(true);
		conexion.conectar();

		// Sacar los datos de la tabla clientes
		rs = conexion.rellenarClientes();

		// Agregar opción "Elegir cliente..." al Choice choCliente
		choClientes.add("Elegir cliente...");

		// Meter en el Choice
		try {
			while (rs.next()) {
				choClientes.add(rs.getString("idCliente") + "-" + rs.getString("nombreCliente") + " " +rs.getString("apellidosCliente"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Sacar los datos de la tabla servicios
		rs = conexion.rellenarServicios();
		// Agregar opción "Elegir servicio..." al Choice choServicios
		choServicios.add("Elegir servicio...");
		// Registro a registro, meterlos en el Choice
		try
		{
			while (rs.next())
			{ // para rellenar recorremos el resultset con el next y en el choice vamos
				// metiendo lo que saquemos del resultset.
				// mandamos la variable del resultset al metodo rellenarLibros. En resultset
				// hacemos el select from para rellenar el choice
				choServicios.add(rs.getInt("idServicio") + "-" + rs.getString("nombreMenu"));
			}
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// caracteristicas ventana
		ventana.setResizable(false);
		ventana.setLocationRelativeTo(null);
		ventana.setBackground(new Color(173, 216, 230));
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		if (dlgMensaje.isActive())
		{
			dlgMensaje.setVisible(false);
		} else
		{
			ventana.setVisible(false);
		}
		if (dlgMensajeError.isActive())
		{
			dlgMensajeError.setVisible(false);
		} else
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
		// Dejamos el método vacío, no necesita realizar ninguna acción
	}

	@Override
	public void actionPerformed(ActionEvent evento) 
	{
		{
			// Nuevo pedido

			if (evento.getSource().equals(btnAceptar)) 
			{
				dlgMensaje.setLayout(new FlowLayout());
				dlgMensaje.setSize(170, 200);
				dlgMensaje.addWindowListener(this);

				// condición: si los campos están vacíos, nos sale un error
				if (txtFechaPreparacionPedidos.getText().length() == 0 
						|| choClientes.getSelectedIndex() == 0 
						|| choServicios.getSelectedIndex() == 0)
				{
					txtFechaPreparacionPedidos.setEditable(false);
					// características del dialogo de error
					System.out.println("Error, campos vacios");
					dlgMensajeError.add(lblMensajeError);
					dlgMensajeError.setSize(180, 75);
					dlgMensajeError.setLayout(new FlowLayout());
					dlgMensajeError.setResizable(false);
					dlgMensajeError.add(lblMensajeAlta);
					dlgMensajeError.setLocationRelativeTo(null);
					dlgMensajeError.setBackground(new Color(30, 144, 255));
					dlgMensajeError.setVisible(true);
				}
				else // condición: si los campos están bien rellenos: insert into
				{
					// Conectar
					conexion.conectar();

					// creamos cadenas y metemos dentro de las cadenas los datos del formulario
					String[] clientes = choClientes.getSelectedItem().split("-");
					idClienteFK = Integer.parseInt(clientes[0]);
					String[] servicios = choServicios.getSelectedItem().split("-");
					idServicioFK = Integer.parseInt(servicios[0]);


					// parseamos las fechas para que tenga formato europeo
					String fechaPreparacionPedidos = formatDate(txtFechaPreparacionPedidos.getText());

					/*	String sentencia = "INSERT INTO pedidos (idClienteFK, idServicioFK, fechaPreparacionPedidos) 
					 * VALUES (" + idClienteFK + ", " + idServicioFK + ", '" + fechaPreparacionPedidos + "') 
					 * WHERE idClienteFK = " + clientes[0] + " AND idServicioFK = " + servicios[0];
					 */
					String sentencia = "INSERT INTO pedidos (idClienteFK, idServicioFK, fechaPreparacionPedidos) "
							+ "VALUES (" + idClienteFK + ", " + idServicioFK + ", '" + fechaPreparacionPedidos + "')";

					int resultado = conexion.insertarPedidos(sentencia);

					if (resultado == 0) 
					{
						lblMensajeAlta.setText("Alta pedido correcta");
						dlgMensaje.setBackground(new Color(0, 102, 204));
						conexion.apunteLog( usuario, sentencia );	
						
						txtFechaPreparacionPedidos.setText("");
					} 
					else 
					{
						lblMensajeAlta.setText("Error en alta");
						dlgMensaje.setBackground(new Color(0, 51, 102));
						conexion.apunteLog(usuario, "ERROR INSERT INTO PEDIDO" );
					}

					// Desconectar
					conexion.desconectar();

					dlgMensaje.setSize(180, 75);
					dlgMensaje.setLayout(new FlowLayout());
					dlgMensaje.setResizable(false);
					dlgMensaje.add(lblMensajeAlta);
					dlgMensaje.setLocationRelativeTo(null);
					dlgMensaje.setVisible(true);
				}
			}

			// Limpiar
			else if (evento.getSource().equals(btnLimpiar))
			{
				limpiar();
			}
		}
	}
	private String formatDate(String dateStr) {
		try {
			SimpleDateFormat europeo = new SimpleDateFormat("dd/MM/yyyy");// Se define un objeto SimpleDateFormat llamado europeo con el formato dd/MM/yyyy
			Date date = europeo.parse(dateStr); // convertimos el objeto SimpleDateFormat en un objeto date
			SimpleDateFormat americano = new SimpleDateFormat("yyyy/MM/dd"); //definimos otro objeto SimpleDateFormat llamado americano con el formato yyyy/MM/dd
			return americano.format(date); //se formatea el objeto americano al formato deseaDo , el europeo (date = dd/MM/yyyy)
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}
	private void limpiar()
	{

		txtFechaPreparacionPedidos.selectAll();
		txtFechaPreparacionPedidos.setText("");

		txtFechaPreparacionPedidos.requestFocus();
	}
	
	private String dameFechaHoy() {
		
		Date currentDate = new Date();

        // Define el formato deseado para la cadena de fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Convierte la fecha en formato de cadena
        return sdf.format(currentDate);
		
	}

}




