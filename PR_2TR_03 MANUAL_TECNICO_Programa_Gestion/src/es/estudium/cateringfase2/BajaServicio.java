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


public class BajaServicio implements WindowListener, ActionListener  
{
	// Componentes
	Frame ventana = new Frame("Baja de Servicio");
	Label lblCabecera = new Label("---- Elegir el Servicio a Eliminar ----");
	Choice choServicios = new Choice();
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
	String usuario="";

	// Constructor
	public BajaServicio(String u)
	{

		usuario= u;
		// bd.apunteLogUser("Baja de Servicio");
		// Listener
		ventana.addWindowListener(this);
		btnEliminar.addActionListener(this);
		btnCancelar.addActionListener(this);

		// caracteristicas ventana
		ventana.setSize(250, 150); 
		ventana.setResizable(false); // No Permitir redimensionar
		ventana.setBackground(new Color(200, 160, 255));
		ventana.setLayout(new FlowLayout());
		ventana.add(lblCabecera);
		// añadimos un primer elemento al Choice
		choServicios.add("Elegir un Servicio para eliminar...");
		// Conectar a la BD
		conexion.conectar();
		// antes de añadir el choice, hay que rellenarlo con los elementos de la tabla servicios mediante el método rellenarServicios
		rs=conexion.rellenarServicios();
		// Registro a registro, meterlos en el Choice
		try 
		{
			while(rs.next())
			{	// para rellenar recorremos el resultset con el next y en el choice vamos metiendo lo que saquemos del resultset.
				// mandamos la variable del resultset al metodo rellenarServicios. En resultset hacemos el select from para rellenar el choice
				choServicios.add(rs.getInt("idServicio")+"-"+
						rs.getString("nombreMenu"));
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Desconectar de la BD
		conexion.desconectar();
		
		ventana.add(choServicios);
		ventana.add(btnEliminar);
		ventana.add(btnCancelar);

		ventana.setLocationRelativeTo(null); //fijar que la ventana salga siempre en el medio
		ventana.setVisible(true); // Mostrarla
	}
	public void windowActivated(WindowEvent we) {}
	public void windowClosed(WindowEvent we) {}
	public void windowClosing(WindowEvent we)
	{
		if(dlgMensaje.isActive())
		{
			dlgMensaje.setVisible(false);
		}
		if(dlgConfirmacion.isActive())
		{
			dlgConfirmacion.setVisible(false);
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
		if(evento.getSource().equals(btnCancelar))
		{
			ventana.setVisible(false);
		}

		else if(evento.getSource().equals(btnEliminar))
		{
			// Si NO est�áseleccionada la primera opción, entonces confirmamos eliminación.
			if(choServicios.getSelectedIndex()!=0)
			{
				dlgConfirmacion.setLayout(new FlowLayout());
				dlgConfirmacion.setSize(250,100);
				dlgConfirmacion.addWindowListener(this);

				lblConfirmacion.setText("----- ¿Eliminar '" + choServicios.getSelectedItem() + "'? -----");//confirmar eliminación + el socio elegido
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

		else if(evento.getSource().equals(btnNo))
		{
			dlgConfirmacion.setVisible(false);
		}
		else if(evento.getSource().equals(btnSi)) // eliminar
		{
			// Conectar
			conexion.conectar();
			// Hacer el DELETE
			String[] array = choServicios.getSelectedItem().split("-"); // hacemos un array con los servicios separados por el - mediante el split
			// Hacer el DELETE con el método borrarServicios
			// el array recibe el id a eliminar y lo mandamos al método borrarServicios. Metemos el resultado en la variable resultado, que recibe un 0 o un 1
			int resultado = conexion.borrarServicios(Integer.parseInt(array[0]));
			if(resultado==0)
			{
				lblMensaje.setText("Borrado con éxito");
				dlgMensaje.setBackground(new Color(100, 80, 130)); 
				conexion.apunteLog( usuario, "DELETE FROM SERVICIOS " + array[0] );	
			}
			else
			{
				lblMensaje.setText("Error en borrado");
				dlgMensaje.setBackground(new Color(220, 20, 60));
				conexion.apunteLog( usuario, "ERROR DELETE FROM SERVICIOS" );	
			}
			// Desconectar
			conexion.desconectar();

			dlgMensaje.addWindowListener(this);

			// Pantalla
			dlgMensaje.setSize(150, 80); 
			dlgMensaje.setResizable(false); // No Permitir redimensionar

			dlgMensaje.setLayout(new FlowLayout());
			dlgMensaje.add(lblMensaje);

			dlgMensaje.setLocationRelativeTo(null); //fijar que la ventana salga siempre en el medio
			dlgMensaje.setVisible(true);
			dlgConfirmacion.setVisible(false);
		}
	}
}
