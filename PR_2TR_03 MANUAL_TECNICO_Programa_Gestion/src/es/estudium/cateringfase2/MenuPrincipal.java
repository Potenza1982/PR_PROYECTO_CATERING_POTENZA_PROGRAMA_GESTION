package es.estudium.cateringfase2;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
//import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MenuPrincipal extends Frame implements WindowListener, ActionListener {

	private static final long serialVersionUID = 1L;

	MenuBar barraMenu = new MenuBar(); // Barra del menú
	Menu mnuClientes = new Menu("Clientes");
	Menu mnuServicios = new Menu("Servicios");
	Menu mnuPedidos = new Menu("Pedidos");
	Menu mnuAyuda = new Menu("Ayuda");

	MenuItem mniClientesNuevo = new MenuItem("Nuevo");
	MenuItem mniClientesBaja = new MenuItem("Baja");
	MenuItem mniClientesListado = new MenuItem("Listado");
	MenuItem mniClientesModificacion = new MenuItem("Modificación");

	MenuItem mniServiciosNuevo = new MenuItem("Nuevo");
	MenuItem mniServiciosBaja = new MenuItem("Baja");
	MenuItem mniServiciosListado = new MenuItem("Listado");
	MenuItem mniServiciosModificacion = new MenuItem("Modificación");

	MenuItem mniPedidosNuevo = new MenuItem("Nuevo");
	MenuItem mniPedidosListado = new MenuItem("Listado");
	MenuItem mniPedidosModificacion = new MenuItem("Modificación");

	MenuItem mniAyuda = new MenuItem("Ayuda");

	Conexion conexion = new Conexion();
	String usuario;
	Image cocina;
	Toolkit herramienta; 

	public MenuPrincipal(int tipoUsuario, String u)
	{
		usuario =u;
		setLayout(new FlowLayout()); // establecemos el layout a la ventana
		setBackground(new Color(127, 255, 212));

		setMenuBar(barraMenu);//añadimos la barra de menú a la ventana

		// Agregar los listener a los menuitems para poder cerrar los diálogos
		mniClientesNuevo.addActionListener(this);
		mniClientesBaja.addActionListener(this);
		mniClientesListado.addActionListener(this);
		mniClientesModificacion.addActionListener(this);

		mniServiciosNuevo.addActionListener(this);
		mniServiciosBaja.addActionListener(this);
		mniServiciosListado.addActionListener(this);
		mniServiciosModificacion.addActionListener(this);

		mniPedidosNuevo.addActionListener(this);
		mniPedidosListado.addActionListener(this);
		mniPedidosModificacion.addActionListener(this);

		mniAyuda.addActionListener(this);

		addWindowListener(this);// añadimos el listener a la ventana

		//características ventana
		setTitle("Menú Principal");
		setResizable(false);// para que la ventana no se pueda distorsionar
		setLocationRelativeTo(null); // para que la ventana salga centrada
		setSize(350,200);// tamaño de la ventana

		herramienta = getToolkit();
		cocina = herramienta.getImage("cocina.png");

		// Al menú Clientes añadimos sus Menuitem
		mnuClientes.add(mniClientesNuevo);
		if (tipoUsuario == 0) // Añadimos opciones al administrador
		{
			mnuClientes.add(mniClientesBaja);
			mnuClientes.add(mniClientesListado);
			mnuClientes.add(mniClientesModificacion);
		}

		// Al menú servicios añadimos sus Menuitem
		mnuServicios.add(mniServiciosNuevo);
		if (tipoUsuario == 0) {
			mnuServicios.add(mniServiciosBaja);
			mnuServicios.add(mniServiciosListado);
			mnuServicios.add(mniServiciosModificacion);
		}

		// Al menú pedidos añadimos sus Menuitem
		mnuPedidos.add(mniPedidosNuevo);
		if (tipoUsuario == 0) {
			mnuPedidos.add(mniPedidosListado);
			mnuPedidos.add(mniPedidosModificacion);
		}

		mnuAyuda.add(mniAyuda);

		// Añadimos a la barra de menú los menus Clientes, servicios y pedidos
		barraMenu.add(mnuClientes);
		barraMenu.add(mnuServicios);
		barraMenu.add(mnuPedidos);
		barraMenu.add(mnuAyuda);

		setVisible(true);// para que la ventana sea visible
	}
	
	 public void paint(Graphics g) {
	        super.paint(g);
	        if (cocina != null) {
	            g.drawImage(cocina, 0, 0, getWidth(), getHeight(), this);
	        }
	    }
	

	public void windowActivated(WindowEvent we) {}
	public void windowClosed(WindowEvent we) {}
	public void windowClosing(WindowEvent we)
	{
		System.exit(0);
	}
	public void windowDeactivated(WindowEvent we) {}
	public void windowDeiconified(WindowEvent we) {}
	public void windowIconified(WindowEvent we) {}
	public void windowOpened(WindowEvent we) {}

	@Override
	public void actionPerformed(ActionEvent e) 
	{// si pulsamos menutiten socioListado, entramos en la ventana de ConsultaCliente-
		if(e.getSource().equals(mniClientesListado))
		{
			new ListadoCliente( usuario );

		}
		else if(e.getSource().equals(mniClientesNuevo))
		{
			new AltaCliente(usuario);
		}
		else if(e.getSource().equals(mniClientesBaja))
		{
			new BajaCliente(usuario);
		}
		else if(e.getSource().equals(mniClientesModificacion))
		{
			new ModificacionCliente(usuario);
		}
		else if(e.getSource().equals(mniServiciosNuevo))
		{
			new AltaServicio(usuario);
		}
		else if(e.getSource().equals(mniServiciosListado))
		{
			new ListadoServicio(usuario);
		}
		else if(e.getSource().equals(mniServiciosBaja))
		{
			new BajaServicio(usuario);
		}
		else if(e.getSource().equals(mniServiciosModificacion))
		{
			new ModificacionServicio(usuario);
		}
		else if(e.getSource().equals(mniPedidosNuevo))
		{
			new AltaPedido(usuario);
		}
		else if(e.getSource().equals(mniPedidosListado))
		{
			new ListadoPedido(usuario);
		}
		else if(e.getSource().equals(mniPedidosModificacion))
		{
			new ModificacionPedido(usuario);
		}
		else if(e.getSource().equals(mniAyuda))
		{
			new Ayuda(usuario);
		}
		//VentanaMenuPrincipal.setVisible(false);

		/*Esta instrucción sirve para que cuando abramos un menuiten se cierre la ventana del menú principal. 
		 * nosotros la dejamos visible, por eso la instrucción esta comentada.*/
	}
}