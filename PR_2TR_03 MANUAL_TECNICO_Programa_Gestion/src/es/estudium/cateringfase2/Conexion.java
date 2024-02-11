package es.estudium.cateringfase2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class Conexion 
{
	String driver = "com.mysql.cj.jdbc.Driver"; // Driver Nativo Tipo 4 de MySQL
	String url = "jdbc:mysql://localhost:3306/cateringfase2"; // Ubicación y Nombre de la base de datos
	String login = "jefeCatering"; // Usuario para conectar a MySQL
	String password = "Studium2023"; // Clave del usuario 
	String sentencia = ""; // objeto para conectar
	Connection connection = null; // objeto para lanzar sentencias SQL
	Statement statement = null;

	/*
	 * El statement asociado a la conexión (statement =
	 * connection.createStatement();) es el objeto que me va a permitir lanzar la
	 * sentencia (insert into/ select * from) hacia la base de datos
	 */

	String usuario = "";
	int tipoUsuario = -1;

	//***********************************************************************************************
	// A) CONECTAR
	// El método conectar() establece una conexión con la base de datos MySQL utilizando las credenciales proporcionadas.
	public void conectar()
	{
		try
		{
			// Cargar los controladores para el acceso a la BD
			Class.forName(driver);
			// Establecer la conexiónn con la BD Catering
			connection = DriverManager.getConnection(url, login, password);
			statement = connection.createStatement();
		} 
		catch (ClassNotFoundException cnfe) 
		{
			System.out.println("Error al cargar los controladores: " + cnfe.getMessage());
		} 
		catch (SQLException sqle) 
		{
			System.out.println("Error al establecer la conexión: " + sqle.getMessage());
		}
	}

	//***********************************************************************************************
	/* para comprobar las credenciales de los usuarios en mysql y para filtrar por tipo de usuario
	   se comprueba las credenciales mediante la sentencia escrita en la clase login */
	//***********************************************************************************************

	//***********************************************************************************************
	// B) CONSULTAR
	/* El método consultar() se utiliza para autenticar a los usuarios en función de sus credenciales 
	 * almacenadas en la base de datos. */

	public int consultar(String sentencia, String usuario)
	{
		int resultado = -1;
		ResultSet rs = null;

		try
		{
			// Crear una sentencia
			statement = connection.createStatement();
			// Crear un objeto ResultSet para guardar lo obtenido
			// y ejecutar la sentencia SQL
			rs = statement.executeQuery(sentencia);
			if (rs.next()) // Si hay, al menos uno
			{

				resultado = rs.getInt("tipoUsuario");

				apunteLog("[Login de '" + usuario + "']", sentencia);
			}
		} catch (SQLException sqle)
		{

			String aux = "Error: " + sqle.getMessage();
			System.out.println(aux);
			// apunteLog(aux);

		}
		return resultado;
	}

	//***********************************************************************************************
	// 1º ALTA DE CLIENTES (clase "AltaCliente")
	// Método para el alta de clientes. Recibe la sentencia que hemos declarado en la clase "AltaEmpleado"

	public int insertarClientes(String sentencia)
	{
		int resultado = 0;
		try
		{
			// Crear una sentencia
			statement = connection.createStatement();
			// Ejecutar el INSERT (se usa el executeUpdate)
			statement.executeUpdate(sentencia);
			// apunteLogUser(sentencia);
		} catch (SQLException sqle)
		{
			resultado = -1; // Error

			String aux = "Error: " + sqle.getMessage();
			System.out.println(aux);
			// apunteLogUser(aux);
		}
		return (resultado);
	}

	//***********************************************************************************************

	// 2º BAJA DE CLIENTES (clase "BajaCliente")
	// Método para borrar clientes.

	/*public int borrarClientes(int idCliente) // recibe el idCliente cortado por el split
	{
		int resultado = 0;
		// Devolver un 0 --> Borrado con éxito
		// Devolver un -1 --> Borrado con error
		try
		{
			// Crear una sentencia
			statement = connection.createStatement();
			// Ejecutar la sentencia con el DELETE + el id a eliminar
			String sentencia = "DELETE FROM clientes WHERE idCliente=" + idCliente;
			statement.executeUpdate(sentencia); // lanza la sentencia a mysql
			//		apunteLogUser(sentencia);
		} catch (SQLException sqle)
		{
			resultado = -1; // Error

			String aux = "Error: " + sqle.getMessage();
			System.out.println(aux);
			//		apunteLogUser(aux);
		}
		return (resultado);
	}
	*/
	public int borrarClientes(int idCliente) {
	    int resultado = 0; // Inicializar con éxito
	    String sentencia = "DELETE FROM clientes WHERE idCliente=?";
	    
	    try (PreparedStatement statement = connection.prepareStatement(sentencia)) {
	        statement.setInt(1, idCliente);
	        int filasAfectadas = statement.executeUpdate();
	        
	        if (filasAfectadas == 0) {
	            resultado = -1; // No se encontró el cliente a borrar
	        }
	    } catch (SQLException sqle) {
	        resultado = -1; // Error
	        String aux = "Error: " + sqle.getMessage();
	        System.out.println(aux);
	        // apunteLog(aux);
	    }
	    
	    return resultado;
	}

	
	
	//***********************************************************************************************

	// 3º CONSULTAR CLIENTES (clase "ListadoCliente")
	// Método para consultar los clientes. 

	public String consultarClientes()
	{
		String contenido = "";
		ResultSet rs = null; // Crear un objeto ResultSet para guardar los datos obtenidos en la base de datos

		try
		{
			// Crear una sentencia
			statement = connection.createStatement();

			// y ejecutar la sentencia SQL . Para ejecutar el SELECT (se usa el executeQuery)
			rs = statement.executeQuery("SELECT * FROM clientes");
			// punteLogUser(sentencia);

			while (rs.next())
			{ /* Recorremos el resultset con el next y en la cadena contenido vamos metiendo el contenido 
				 más lo que saquemos del resultset */

				contenido = contenido + rs.getInt("idCliente") + "\t" + rs.getString("nombreCliente") + "\t"
						+ rs.getString("apellidosCliente") + "\t" + rs.getString("direccionCliente") + "\t"
						+ rs.getString("telefonoCliente") + "\n";
			}

		} 
		catch (SQLException sqle)
		{

			String aux = "Error: " + sqle.getMessage();
			System.out.println(aux);
			// apunteLogUser(aux);
		}
		return (contenido);
	}
	//***********************************************************************************************

	// 4º ACTUALIZAR CLIENTES (clase "ModificacionCliente")
	// Método para hacer la modificación de los clientes

	public int actualizarClientes(String idCliente, String nombreCliente, String apellidosCliente,
			String direccionCliente, String telefonoCliente)
	{
		int resultado = 0;
		// sentencia para hacer la modificacion
		String sentencia = "UPDATE clientes SET nombreCliente = '" + nombreCliente + "', apellidosCliente = '"
				+ apellidosCliente + "' , direccionCliente = '" + direccionCliente + "' ,telefonoCliente = '"
				+ telefonoCliente + "' WHERE idCliente = " + idCliente;
		// Devolver un 0 --> Modificación con éxito
		// Devolver un -1 --> Modificación con error

		try
		{
			// Crear una sentencia
			statement = connection.createStatement();
			// Ejecutar el UPDATE
			statement.executeUpdate(sentencia); // mandamos la sentencia a mysql
			// apunteLogUser(sentencia);
		} catch (SQLException sqle)
		{
			resultado = -1; // Error
			String aux = "Error: " + sqle.getMessage();
			System.out.println(aux);
			//		apunteLogUser(aux);	
		}
		return (resultado);
	}
	//***********************************************************************************************
	// 5º CONSULTAR MODIFICACION CLIENTES - RESULTSET "consultaModificacionClientes"
	// Método para consultar los clientes que queremos modificar.

	public ResultSet consultaModificacionClientes(String idCliente)
	{
		ResultSet rs = null;
		try
		{
			// Crear una sentencia
			statement = connection.createStatement();
			// Ejecutar el SELECT
			rs = statement.executeQuery("SELECT * FROM clientes WHERE idCliente = " + idCliente);
			//		apunteLogUser(sentencia);
		} 
		catch (SQLException sqle)
		{
			System.out.println(sqle.getMessage());

			String aux = "Error: " + sqle.getMessage();
			System.out.println(aux);
			//		apunteLogUser(aux);
		}
		return (rs);
	}

	//***********************************************************************************************
	// 6º RELLENAR clientes - RESULTSET 
	// Método para rellenar el choice de los clientes.

	public ResultSet rellenarClientes()
	{
		ResultSet rs = null; // Crear un objeto ResultSet para guardar los datos obtenidos en la base de datos

		try
		{
			// Crear una sentencia
			statement = connection.createStatement();
			// en el resultset metemos la Ejecucion del SELECT
			rs = statement.executeQuery("SELECT * FROM clientes");
			// apunteLogUser(sentencia);
		} catch (SQLException sqle)
		{
			String aux = "Error: " + sqle.getMessage();
			System.out.println(aux);
			// apunteLogUser(aux);
		}
		return (rs);
	}

	//****************************** HASTA AQUI clientes *******************************************
	//***********************************************************************************************
	
	// 1º ALTA DE LOS SERVICIOS (clase "AltaServicios")
	// Método para el alta de los servicios. Recibe la sentencia que hemos declarado en la clase "AltaEmpleado".

	public int insertarServicios(String sentencia2)
	{
		int resultado2 = 0; // Éxito
		try
		{
			// Crear una sentencia
			statement = connection.createStatement();
			// Ejecutar el INSERT (se usa el executeUpdate)
			statement.executeUpdate(sentencia2);
			//		apunteLogUser(sentencia);
		} catch (SQLException sqle)
		{
			resultado2 = -1; // Error

			String aux = "Error: " + sqle.getMessage();
			System.out.println(aux);
			// apunteLogUser(aux);
		}
		return (resultado2);
	}	

	//***********************************************************************************************
	// 2º BAJA DE LOS SERVICIOS (clase "BajaServicio")
	// Método para borrar los servicios.

	public int borrarServicios(int idServicio)
	{
	    int resultado = 0;
	    // Devolver un 0 --> Borrado éxito
	    // Devolver un -1 --> Borrado error
	    try
	    {
	        // Crear una sentencia
	        statement = connection.createStatement();
	        // Ejecutar la sentencia con el DELETE + el id a eliminar
	        String sentencia = "DELETE FROM servicios WHERE idServicio=" + idServicio;
	        statement.executeUpdate(sentencia); // lanza la sentencia a mysql
	        // apunteLogUser(sentencia);
	    } 
	    catch (SQLException sqle)
	    {
	        resultado = -1; // Error

	        String aux = "Error: " + sqle.getMessage();
	        System.out.println(aux);
	        // apunteLogUser(aux);
	    }
	    return (resultado);
	}


	//***********************************************************************************************
	// 3º CONSULTAR SERVICIOS (clase "ListadoServicio")
	// Método para consultar los servicios. 

	public String consultarServicios()
	{
		String contenidoServicios = "";
		ResultSet rsServicio = null; // Crear un objeto ResultSet para guardar los datos obtenidos en la base de datos

		try
		{
			// Crear una sentencia
			statement = connection.createStatement();
			// y ejecutar la sentencia SQL . Para ejecutar el SELECT (se usa el
			// executeQuery)
			rsServicio = statement.executeQuery("SELECT * FROM servicios");
			// apunteLogUser(sentencia);

			/*
			 * while(rs.next()) { // recorremos el resultset con el next y en la cadena
			 * contenido vamos metiendo el contenido + lo que saquemos del resultset
			 * 
			 * contenido = contenido + rs.getInt("idCliente")
			 * +"\t"+rs.getString("nombreCliente") + "\t" +
			 * rs.getString("apellidosCliente") + "\t"+
			 * rs.getString("direccionCliente") + "\t" +
			 * rs.getString("telefonoCliente") +"\n";
			 * }
			 */

			while (rsServicio.next()) // Si hay, al menos uno
			{// recorremos el resultset con el next y en la cadena contenido vamos metiendo
				// el contenido más lo que saquemos del resultset.
				contenidoServicios = contenidoServicios + rsServicio.getInt("idServicio") + "\t" + rsServicio.getString("nombreMenu")
				+ "\t" + rsServicio.getString("ingredienteMenu") + "\t" + rsServicio.getString("precioMenu") + "\n";
			}
		} catch (SQLException sqle)
		{

			String aux = "Error: " + sqle.getMessage();
			System.out.println(aux);
			//		apunteLogUser(aux);
		}
		return (contenidoServicios);
	}

	//***********************************************************************************************
	// 4º ACTUALIZAR SERVICIOS (clase "ModificacionServicio")
	// Método para hacer la modificación de los servicios.

	public int actualizarServicios(String idServicio, String menuNuevo, String ingredienteMenuNuevo, String precioMenuNuevo)
	{

		int resultadoServicios = 0;
		// sentencia para hacer la modificacion
		String sentencia = "UPDATE servicios SET nombreMenu = '" + menuNuevo 
				+ "', ingredienteMenu ='" + ingredienteMenuNuevo 
				+ "', precioMenu ='" + precioMenuNuevo 
				+"' WHERE idServicio = " + idServicio;
		// Devolver un 0 --> Modificación con éxito
		// Devolver un -1 --> Modificación error
		try
		{
			// Crear una sentencia
			statement = connection.createStatement();
			// Ejecutar el UPDATE
			statement.executeUpdate(sentencia); // mandamos la sentencia a mysql

		} 
		catch (SQLException sqle)
		{
			resultadoServicios = -1; // Error

			String aux = "Error: " + sqle.getMessage();
			System.out.println(aux);
			// apunteLogUser(aux);
		}
		return (resultadoServicios);
	}

	//***********************************************************************************************
	// 5º CONSULTAR MODIFICACION SERVICIOS- RESULTSET "consultaModificacionServicios"
	// Método para consultar los servicios que queremos modificar

	public ResultSet consultaModificacionServicios(String idServicio)
	{
		ResultSet rs = null;
		try
		{
			// Crear una sentencia
			statement = connection.createStatement();
			// Ejecutar el SELECT
			rs = statement.executeQuery("SELECT * FROM servicios WHERE idServicio = " + idServicio);
			// apunteLogUser(sentencia);
		} catch (SQLException sqle)
		{
			System.out.println(sqle.getMessage());

			String aux = "Error: " + sqle.getMessage();
			System.out.println(aux);
			// apunteLogUser(aux);
		}
		return (rs);
	}
	//***********************************************************************************************
	// 6º RELLENAR SERVICIOS - RESULTSET 
	// Método para rellenar el choice de los servicios.

	public ResultSet rellenarServicios() 
	{
		ResultSet rs = null;
		try 
		{
			String consulta = "SELECT idServicio, nombreMenu, ingredienteMenu, precioMenu FROM servicios";
			PreparedStatement ps = connection.prepareStatement(consulta);
			rs = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	//****************************** HASTA AQUI SERVICIOS *******************************************

	//***********************************************************************************************
	// 1º ALTA DE PEDIDOS (clase "AltaPedido")
	// Método para el alta de los pedidos. Recibe la sentencia que hemos declarado en la clase "AltaPedido".

	public int insertarPedidos(String sentencia)
	{

		int resultado = 0; // éxito
		try
		{
			// Crear una sentencia
			statement = connection.createStatement();
			// Ejecutar el INSERT (se usa el executeUpdate)
			statement.executeUpdate(sentencia);
			// apunteLogUser(sentencia);
		} 
		catch (SQLException sqle)
		{
			resultado = -1; // Error

			String aux = "Error: " + sqle.getMessage();
			System.out.println(aux);
			// apunteLogUser(aux);
		}
		return (resultado);
	}

	//***********************************************************************************************
	// 2º CONSULTAR PEDIDOS (clase "ListadoPedido")
	// Método para consultar los pedidos. 

	public String consultarPedidos()
	{
		String contenido = "";
		ResultSet rs = null; // Crear un objeto ResultSet para guardar los datos obtenidos en la base de
		// datos
		try
		{
			// Crear una sentencia
			statement = connection.createStatement();

			// y ejecutar la sentencia SQL . Para ejecutar el SELECT (se usa el executeQuery)
			// rs = statement.executeQuery("SELECT * FROM pedidos");
			rs = statement.executeQuery("SELECT idPedido, nombreMenu AS 'Menú', nombreCliente AS 'Cliente', "
					+ "	DATE_FORMAT(fechaPreparacionPedidos, '%d/%m/%Y') AS 'Preparación' "
					+ "	FROM cateringfase2.pedidos pedidos "
					+ "	JOIN cateringfase2.clientes clientes ON pedidos.idClienteFK = clientes.idCliente "
					+ "	JOIN cateringfase2.servicios servicios ON pedidos.idServicioFK = servicios.idServicio"
					+ " ORDER BY fechaPreparacionPedidos DESC");;
			
			while (rs.next())
			{
				// Recorremos el ResultSet y vamos obteniendo los valores de cada columna por su nombre.
				int idPedido = rs.getInt("idPedido");
				String nombreMenu = rs.getString("Menú");
				String nombreCliente = rs.getString("Cliente");
				String fechaPreparacionPedidos = rs.getString("Preparación");

				// Agregamos los valores obtenidos a la cadena de contenido.
				contenido += idPedido + "\t" + nombreMenu + "\t" + nombreCliente + "\t" + fechaPreparacionPedidos + "\n";
			}
		} 
		catch (SQLException sqle)
		{
			String aux = "Error: " + sqle.getMessage();
			System.out.println(aux);
			//	apunteLogUser(aux);
		}
		return (contenido);
	}

	//***********************************************************************************************
	// 3º ACTUALIZAR PEDIDOS (clase "ModificacionPedido")
	// Método para actualizar los pedidos que queremos modificar.	

	public int actualizarPedidos(int idPedido, java.sql.Date sqlDate) 
	{
		int resultado = 0;

		// Sentencia para hacer la modificación
		String sentencia = "UPDATE pedidos SET fechaPreparacionPedidos = ? WHERE idPedido = ?";

		try {
			// Crear una sentencia preparada
			PreparedStatement statement = connection.prepareStatement(sentencia);
			// Establecer los parámetros de la sentencia
			statement.setDate(1, sqlDate);
			statement.setInt(2, idPedido);
			
			// Ejecutar el UPDATE
			statement.executeUpdate();
			
		} catch (SQLException sqle) {
			resultado = -1; // Error
			String aux = "Error: " + sqle.getMessage();
			System.out.println(aux);
		}
		return resultado;
	}

	//***********************************************************************************************
	// 4º CONSULTAR MODIFICACION PEDIDOS - RESULTSET "consultaModificacionPedidos"
	// Método para consultar los pedidos que queremos modificar.

	public ResultSet consultaModificacionPedidos(String idPedido)
	{

		ResultSet rs = null;
		try
		{
			// Crear una sentencia
			statement = connection.createStatement();
			// Ejecutar el SELECT
			rs = statement.executeQuery("SELECT pedidos.idPedido, servicios.nombreMenu, clientes.nombreCliente, clientes.apellidosCliente, "
					+ "	DATE_FORMAT(fechaPreparacionPedidos, '%d/%m/%Y') AS 'fechaPreparacion' "
					+ "	FROM cateringfase2.pedidos pedidos "
					+ "	JOIN cateringfase2.clientes clientes ON pedidos.idClienteFK = clientes.idCliente "
					+ "	JOIN cateringfase2.servicios servicios ON pedidos.idServicioFK = servicios.idServicio"
					+ " WHERE idPedido = "+ idPedido);
			// apunteLogUser(sentencia);
		} 
		catch (SQLException sqle)
		{
			System.out.println(sqle.getMessage());

			String aux = "Error: " + sqle.getMessage();
			System.out.println(aux);
			// apunteLogUser(aux);
		}
		return (rs);
	}

	//***********************************************************************************************
	// 5º RELLENAR PEDIDOS - RESULTSET 
	// Método para rellenar el choice de los pedidos.

	public ResultSet rellenarPedidos()
	{
		ResultSet rs = null; // Crear un objeto ResultSet para guardar los datos obtenidos en la base de datos.
		try
		{
			// Crear una sentencia
			statement = connection.createStatement();
			// en el resultset metemos la Ejecucion del SELECT			
			rs = statement.executeQuery("SELECT idPedido, nombreMenu AS 'Menú', nombreCliente AS 'Cliente', "
					+ "	DATE_FORMAT(fechaPreparacionPedidos, '%d/%m/%Y') AS 'Preparación' "
					+ "	FROM cateringfase2.pedidos pedidos "
					+ "	JOIN cateringfase2.clientes clientes ON pedidos.idClienteFK = clientes.idCliente "
					+ "	JOIN cateringfase2.servicios servicios ON pedidos.idServicioFK = servicios.idServicio");
			
			// apunteLogUser(sentencia);
		} 
		catch (SQLException sqle)
		{

			String aux = "Error: " + sqle.getMessage();
			System.out.println(aux);
			// apunteLogUser(aux);

		}
		return (rs);
	}

	
	//****************************** HASTA AQUI PEDIDOS *******************************************

	//***********************************************************************************************
	// C) DESCONECTAR
	public void desconectar()
	{
		try
		{
			if (connection != null)
			{
				connection.close();
			}
		} catch (SQLException e)
		{
		}
	}

	//***********************************************************************************************
	// D) MÉTODO "apunteLog"
	// Este método recibe dos parametros : usuario logado y un mensaje que registraremos en el log.

	public void apunteLog(String usuario, String mensaje)
	{
		try
		{
			// Para abrir  y crear un fichero, "true" indica que el contenido se agregará al final del archivo en lugar de sobreescribirlo.
			FileWriter fw = new FileWriter("historico.txt", true);

			/* El "BufferWriter" está asociado al "FileWriter", el "Buffer" es una memoria intermedia para el intercambio 
			 * de información entre dos sistemas que tienen distintas velocidades y así no se pierde información. */

			BufferedWriter bw = new BufferedWriter(fw);

			// Objeto para poder dibujar / pintar datos en el fichero. El "PrintWriter" está asociado al "BufferedWriter".
			PrintWriter salida = new PrintWriter(bw);
			// Trabajar con el fichero (contenido).

			// salida.println esta asociado al objeto salida que es un PrintWriter.
			// salida es lo que se registra: fecha actual (new Date().toString(), el usuario y el mensaje).
			salida.println(new Date().toString() + " +| " + usuario + " +| " + mensaje);
			System.out.println("Información guardada...");

			// Cerrar el fichero
			salida.close();
			bw.close();
			fw.close();
		} 
		catch (IOException ioe)
		{
			System.out.println("Error en el fichero...");
		}
	}

}









