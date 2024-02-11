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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ModificacionPedido implements WindowListener, ActionListener {
	Frame ventana = new Frame("Modificación de Pedido");
	Label lblCabecera = new Label("Elegir el Pedido a modificar:");
	Choice choPedidos = new Choice();
	Button btnEditar = new Button("Editar");

	Dialog dlgEditar = new Dialog(ventana, "Edición Pedido", true);

	Label lblCabecera2 = new Label("-------Modificando el Pedido nº -------");

	Label lblId = new Label("Id Pedido:");
	Label lblServicio = new Label("Servicio:");
	Label lblCliente = new Label("Cliente:");
	Label lblFechaPreparacionPedido = new Label("Fecha Preparación:");
	TextField txtId = new TextField(20);
	TextField txtServicio = new TextField(20);
	TextField txtCliente = new TextField(20);
	TextField txtFechaPreparacionPedido = new TextField(20);
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

	public ModificacionPedido(String u) {
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
		
		// características ventana
		ventana.setSize(280, 130);
		ventana.setResizable(false);// No Permitir redimensionar
		// Layout
		ventana.setLayout(new FlowLayout());
		
		// rellenamos la ventana por orden de aparicion
		ventana.add(lblCabecera);
		rellenarPedidos();
		ventana.add(choPedidos);
		ventana.add(btnEditar);
		ventana.add(btnCancelar);
		ventana.setBackground(new Color(255, 204, 153));
		ventana.setLocationRelativeTo(null); // fijar que la ventana salga siempre en el medio
		ventana.setVisible(true);// Mostrarla
	}

	private int calcularAlturaVentana(int numItems) {
	    // Calcula la altura mínima de la ventana (por ejemplo, para 4 elementos)
	    int alturaMinima = 360;
	    // Calcula la altura adicional necesaria por cada elemento adicional en el menú
	    int alturaPorItem = 25;
	    // Calcula y devuelve la altura total de la ventana
	    return alturaMinima + (numItems - 4) * alturaPorItem;
	}

	private void rellenarPedidos() {
		choPedidos.removeAll();
		// Rellenar Choice con un primer elemento
		choPedidos.add("Elegir pedido...");
		// Conectar a la BD
		conexion.conectar();
		// Sacar los datos de la tabla pedidos
		rs = conexion.rellenarPedidos();// implementado en clase BaseDatos
		// Registro a registro, meterlos en el Choice

		try 
		{
			while (rs.next()) 
			{
				// Recorremos el ResultSet y vamos obteniendo los valores de cada columna por su nombre
				int idPedido = rs.getInt("idPedido");
				String servicio = rs.getString("Menú");
				String cliente = rs.getString("Cliente");
				String fechaPreparacionPedido = rs.getString("Preparación"); //función DATE_FORMAT de MySQL para convertirlo en una cadena con el formato "dd/MM/yyyy

				String pedido = idPedido + "-" + servicio + "-" + cliente + " - " + fechaPreparacionPedido;

				// Agregar el pedido al Choice
				choPedidos.add(pedido);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Desconectar de la BD
		conexion.desconectar();
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (dlgMensaje.isActive()) {
			dlgMensaje.setVisible(false);
		} else if (dlgEditar.isActive()) {
			dlgEditar.setVisible(false);
		} else if (dlgModificacion.isActive()) {
			dlgModificacion.setVisible(false);
		} else if (dlgModificacion2.isActive()) {
			dlgModificacion2.setVisible(false);
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
			// Calcula la altura necesaria en función del número de elementos en el menú desplegable
		    int numItems = choPedidos.getItemCount();
		    int alturaVentana = calcularAlturaVentana(numItems);
		    
		    // Configura el tamaño de la ventana con la altura calculada
		    dlgEditar.setSize(250, alturaVentana);
		    
			// Si NO está seleccionada la primera opción, entonces confirmamos edito
			if (choPedidos.getSelectedIndex() != 0) 
			{
				String[] seleccionado = choPedidos.getSelectedItem().split("-");

				conexion.conectar();
				rs = conexion.consultaModificacionPedidos(seleccionado[0]);

				try {
					rs.next();
					int idPedido = rs.getInt(1);
					String servicio = rs.getString(2);
	                String cliente = rs.getString(3) + " " + rs.getString(4);
	                String fechaPreparacionPedido = rs.getString(5);
	               	               
	                txtId.setText(String.valueOf(idPedido));
	                txtServicio.setText(servicio);
	                txtCliente.setText(cliente);
	                txtFechaPreparacionPedido.setText(fechaPreparacionPedido);
	                
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				conexion.desconectar();

				dlgEditar.setSize(250, 360);
				dlgEditar.setResizable(false);
				dlgEditar.setLayout(new FlowLayout());
				
				dlgEditar.add(lblCabecera2);
				dlgEditar.add(lblId);
				txtId.setEnabled(false);
				txtServicio.setEnabled(false);
	            txtCliente.setEnabled(false);
	            txtFechaPreparacionPedido.setEnabled(true);
	            dlgEditar.add(txtId);
	            dlgEditar.add(lblServicio);
	            dlgEditar.add(txtServicio);
	            dlgEditar.add(lblCliente);
	            dlgEditar.add(txtCliente);
	            dlgEditar.add(lblFechaPreparacionPedido);
	            dlgEditar.add(txtFechaPreparacionPedido);
	            dlgEditar.add(btnModificar);
	            dlgEditar.add(btnVolver);
	            dlgEditar.setLocationRelativeTo(null);
	            dlgEditar.setBackground(new Color(255, 204, 153));
	            dlgEditar.setVisible(true);
			}
		} else if (evento.getSource().equals(btnModificar)) {
			if (txtFechaPreparacionPedido.getText().length() == 0) {
				dlgModificacion2.setSize(180, 75);
				dlgModificacion2.setLayout(new FlowLayout());
				dlgModificacion2.add(lblModificacion2);
				dlgModificacion2.setBackground(new Color(255, 140, 0));
				dlgModificacion2.setLocationRelativeTo(null);
				dlgModificacion2.setVisible(true);
			} else {
				conexion.conectar();
				int idPedido = Integer.parseInt(txtId.getText());
				String servicio = txtServicio.getText();
				String cliente = txtCliente.getText();
				String fechaPreparacionPedido = txtFechaPreparacionPedido.getText();

				SimpleDateFormat formatoFechaEU = new SimpleDateFormat("dd/MM/yyyy");

				try {
					Date fechaPrepPedFormat = formatoFechaEU.parse(fechaPreparacionPedido);	
					java.sql.Date sqlDate = new java.sql.Date(fechaPrepPedFormat.getTime());

					conexion.actualizarPedidos(idPedido, sqlDate);
				
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				conexion.desconectar();
				conexion.apunteLog(usuario, "UPDATE PEDIDO " + txtId.getText());

				dlgModificacion.setSize(180, 75);
				dlgModificacion.setLayout(new FlowLayout());
				dlgModificacion.add(lblModificacion);
				dlgModificacion.setBackground(new Color(255, 99, 71));
				dlgModificacion.setLocationRelativeTo(null);
				dlgModificacion.setVisible(true);

				dlgEditar.setVisible(false);
				ventana.setVisible(true);
				limpiarCampos();
				actualizarPedidos();
			}
		}
	}

	private void actualizarPedidos() {
		choPedidos.removeAll();
		rellenarPedidos();
	}

	private void limpiarCampos() {
		txtId.setText("");
		txtServicio.setText("");
		txtCliente.setText("");
		txtFechaPreparacionPedido.setText("");
	}
}
