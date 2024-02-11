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


public class ModificacionServicio implements WindowListener, ActionListener 
{
	Frame ventana = new Frame("Modificación de Servicio");
	Label lblCabecera = new Label("Elegir el servicio a modificar:");
	Choice choServicios = new Choice();
	Button btnEditar = new Button("Editar");

	Dialog dlgEditar = new Dialog(ventana, "Edición Servicio", true);
	Label lblCabecera2 = new Label("-------Editando el Servicio nº -------");
	Label lblId = new Label(" Id Servicio: ");
	Label lblMenu = new Label("Nombre del Menú:");
	Label lblIngrediente = new Label("Ingredientes del Menú:");
	Label lblPrecio = new Label("Precio del Menú:");

	TextField txtId = new TextField(20);
	TextField txtMenu = new TextField(20);
	TextField txtIngrediente = new TextField(20);
	TextField txtPrecio = new TextField(20);

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

	public ModificacionServicio(String u) 
	{
		usuario = u;
		// bd.apunteLogUser("Modificación de Servicios");
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
		ventana.setSize(380, 130);
		ventana.setResizable(false);// No Permitir redimensionar

		ventana.setLayout(new FlowLayout());
		ventana.add(lblCabecera);
		rellenarChoiceServicios();//implementado en esta clase
		ventana.add(choServicios);
		ventana.add(btnEditar);
		ventana.add(btnCancelar);
		ventana.setBackground(new Color(255, 204, 153));
		ventana.setLocationRelativeTo(null);//fijar que la ventana salga siempre en el medio.
		ventana.setVisible(true);// Mostrarla
	}

	private int calcularAlturaVentana(int numItems) {
	    // Calcula la altura mínima de la ventana (por ejemplo, para 4 elementos)
	    int alturaMinima = 400;
	    // Calcula la altura adicional necesaria por cada elemento adicional en el menú
	    int alturaPorItem = 25;
	    // Calcula y devuelve la altura total de la ventana
	    return alturaMinima + (numItems - 4) * alturaPorItem;
	}

	private void rellenarChoiceServicios() 
	{
		choServicios.removeAll();
		// Rellenar Choice con un primer elemento
		choServicios.add("Elegir Servicio...");
		// Conectar a la BD
		conexion.conectar();
		// Sacar los datos de la tabla servicios
		rs = conexion.rellenarServicios();//implementado en clase Conexion.
		// Registro a registro, meterlos en el Choice.

		try 
		{
			while (rs.next()) 
			{
				choServicios.add(rs.getInt("idServicio")+"-"+
						rs.getString("nombreMenu")+ " " +
						rs.getString("ingredienteMenu")+ " " +
						rs.getString("precioMenu"));
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		// Desconectar de la BD
		conexion.desconectar();
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

	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}

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
		    int numItems = choServicios.getItemCount();
		    int alturaVentana = calcularAlturaVentana(numItems);
		    
		    // Configura el tamaño de la ventana con la altura calculada
		    dlgEditar.setSize(250, alturaVentana);
		    
			// Si NO está seleccionada la primera opción, entonces confirmamos edito
			if (choServicios.getSelectedIndex() != 0) 
			{
				String[] seleccionado = choServicios.getSelectedItem().split("-");// hacemos un array con los servicios separados por el - mediante el split		
				// Conectar a la BD y sacar los datos del servicio seleccionado
				conexion.conectar();
				// hacemos una consulta a la tabla servicios para rellenar la pantalla de modificacion y metemos en el array el id cortado por el split
				rs = conexion.consultaModificacionServicios(seleccionado[0]);

				try 
				{
					rs.next();
					txtMenu.setText(rs.getString("nombreMenu"));
					txtIngrediente.setText(rs.getString("ingredienteMenu"));
					txtPrecio.setText(rs.getString("precioMenu"));
					//txtPrecio.setText(String.valueOf(precio));	
				} 
				catch (SQLException e) 
				{
					e.printStackTrace();
				}

				conexion.desconectar();

				// Muestro los datos del servicio elegido en la pantalla de edición
				dlgEditar.setSize(250, 340);
				dlgEditar.setResizable(false);// No Permitir redimensionar

				dlgEditar.setLayout(new FlowLayout());
				dlgEditar.add(lblCabecera2);
				dlgEditar.add(lblId);
				txtId.setEnabled(false);
				txtId.setText(seleccionado[0]); // rs.getInt("idServicio"); id selecionado en el choice con el split
				dlgEditar.add(txtId);

				dlgEditar.add(lblMenu);
				dlgEditar.add(txtMenu);

				dlgEditar.add(lblIngrediente);
				dlgEditar.add(txtIngrediente);

				dlgEditar.add(lblPrecio);
				dlgEditar.add(txtPrecio);

				dlgEditar.add(btnModificar);
				dlgEditar.add(btnVolver);

				dlgEditar.setLocationRelativeTo(null);
				dlgEditar.setBackground(new Color(255, 204, 153));
				dlgEditar.setVisible(true);
			}
		} else if (txtMenu.getText().length() == 0 || txtIngrediente.getText().length() == 0
				|| txtPrecio.getText().length() == 0) 
		{
			dlgModificacion2.setSize(180, 75);
			dlgModificacion2.setLayout(new FlowLayout());
			dlgModificacion2.add(lblModificacion2);
			dlgModificacion2.setBackground(new Color(255, 140, 0));
			dlgModificacion2.setLocationRelativeTo(null);
			dlgModificacion2.setVisible(true);
		} 
		else if (evento.getSource().equals(btnModificar)) 
		{
			conexion.conectar();

			//metodo actualizarServicios con el update en actualizarServicios mostramos los datos que podemos modificar, mandamos la sentencia al metodo actualizarServicios
			int resultadoServicios = conexion.actualizarServicios(txtId.getText(), txtMenu.getText(), txtIngrediente.getText(),txtPrecio.getText());

			conexion.desconectar();

			rellenarChoiceServicios();

			if(resultadoServicios == 0)
			{

				dlgModificacion.setSize(180, 75);
				dlgModificacion.setLayout(new FlowLayout());
				lblModificacion.setText("Modificación Correcta");
				conexion.apunteLog( usuario, "UPDATE  SERVICIO " + txtMenu );	
				dlgModificacion.add(lblModificacion);
				dlgModificacion.setBackground(new Color(255, 99, 71));
				dlgModificacion.setLocationRelativeTo(null);
				dlgModificacion.setVisible(true);
			}
		}
	}}
