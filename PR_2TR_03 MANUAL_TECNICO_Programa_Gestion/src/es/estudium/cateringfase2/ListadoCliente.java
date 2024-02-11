package es.estudium.cateringfase2;

import java.awt.Button;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ListadoCliente implements WindowListener, ActionListener 
{
	private static final String HEADER = "id\t\tNombre\t\tApellidos\t\tDireccion\t\tTeléfono";
	private static final String DEST = "Listado de Clientes.pdf";
	private static final String DEST_EXCEL = "Listado de Clientes.xlsx";

	Frame ventana = new Frame("Listado de Clientes");
	TextArea txaListado = new TextArea(10, 40);
	Button btnPdf = new Button("PDF");
	Button btnExcel = new Button("Excel");
	Conexion conexion = new Conexion();
	String usuario="";

	public ListadoCliente(String u) 
	{ 
		usuario= u;

		// Listener
		ventana.addWindowListener(this);
		btnPdf.addActionListener(this);
		btnExcel.addActionListener(this);
		// Pantalla
		ventana.setSize(460, 250); 
		ventana.setResizable(false); // No Permitir redimensionar

		ventana.setLayout(new FlowLayout());
		// Rellenar TextArea con la información de la conexión
		// Conectar
		conexion.conectar();
		// Sacar la información con el método consultarClientes y metemos la información en el TextArea
		//txaListado.append(HEADER+"\n");
		txaListado.setText(conexion.consultarClientes());
		conexion.apunteLog(usuario, "SELECT * FROM CLIENTES");   
		// Desconectar
		conexion.desconectar();
		ventana.add(txaListado);
		ventana.add(btnPdf);
		ventana.add(btnExcel);

		ventana.setBackground(new Color(144, 238, 144));
		ventana.setLocationRelativeTo(null); //fijar que la ventana salga siempre en el medio
		ventana.setVisible(true); // Mostrarla
	}

	public void windowActivated(WindowEvent we) {}
	public void windowClosed(WindowEvent we) {}
	public void windowClosing(WindowEvent we) 
	{
		ventana.setVisible(false);
	}
	public void windowDeactivated(WindowEvent we) {}
	public void windowDeiconified(WindowEvent we) {}
	public void windowIconified(WindowEvent we) {}
	public void windowOpened(WindowEvent we) {}

	@Override
	public void actionPerformed(ActionEvent evento) 
	{
		if (evento.getSource().equals(btnPdf)) 
		{
			try {
				// Inicializar PDF writer
				PdfWriter writer = new PdfWriter(DEST);

				// Inicializar PDF document
				PdfDocument pdf = new PdfDocument(writer);

				// Inicializar document
				Document document = new Document(pdf, PageSize.A4.rotate());
				document.setMargins(20, 20, 20, 20);

				PdfFont font = PdfFontFactory.createFont(StandardFonts.COURIER);
				PdfFont bold = PdfFontFactory.createFont(StandardFonts.COURIER_BOLD);
				//  añadir Paragraph
				document.add(new Paragraph("Listado de Clientes").setFont(bold).setTextAlignment(TextAlignment.CENTER).setFontSize(22f));
				Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 3, 3, 4})).useAllAvailableWidth();

				// leer Headers
				String line = HEADER;
				process(table, line, bold, true);

				conexion.conectar();
				String[] listado = conexion.consultarClientes().split("\n");
				conexion.desconectar();

				for (String lineN : listado) {
					process(table, lineN, font, false);
				}

				document.add(table);
				conexion.apunteLog(usuario, "PRINT TO PDF CLIENTES");
				// cerrar documento
				document.close();

				// Abrir el nuevo archivo de pdf
				Desktop.getDesktop().open(new File(DEST));
			} 
			catch (IOException ioe) 
			{
				ioe.printStackTrace();
			}
		}

		//boton excel
		else if (evento.getSource().equals(btnExcel)) {
			try {
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("Listado de Clientes");

				// Crear la fila de encabezado
				XSSFRow headerRow = sheet.createRow(0);
				String[] headers = HEADER.split("\t\t");
				for (int i = 0; i < headers.length; i++) {
					XSSFCell cell = headerRow.createCell(i);
					cell.setCellValue(headers[i]);
					CellStyle style = workbook.createCellStyle();
					XSSFFont font = workbook.createFont();
					font.setBold(true);
					style.setFont(font);
					cell.setCellStyle(style);
				}

				conexion.conectar();
				String[] listado = conexion.consultarClientes().split("\n");
				conexion.desconectar();

				// Agregar los datos al archivo de Excel
				int rowNum = 1;
				for (String lineN : listado) {
					XSSFRow row = sheet.createRow(rowNum++);
					StringTokenizer tokenizer = new StringTokenizer(lineN, "\t\t");
					int colNum = 0;
					while (tokenizer.hasMoreTokens()) {
						String value = tokenizer.nextToken();
						XSSFCell cell = row.createCell(colNum++);
						cell.setCellValue(value);
					}
				}
				// Ajustar el ancho de las columnas
				for (int i = 0; i < headers.length; i++) {
					sheet.autoSizeColumn(i);
				}
				// Guardar el archivo de Excel
				FileOutputStream outputStream = new FileOutputStream(DEST_EXCEL);
				workbook.write(outputStream);
				workbook.close();
				outputStream.close();

				// Abrir el nuevo archivo de Excel
				Desktop.getDesktop().open(new File(DEST_EXCEL));
				conexion.apunteLog(usuario, "LISTADO EXCEL CLIENTES");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void process(Table table, String line, PdfFont font, boolean isHeader) {
		StringTokenizer tokenizer = new StringTokenizer(line, "\t\t");
		while (tokenizer.hasMoreTokens()) {
			if (isHeader) {
				table.addHeaderCell(new Cell().add(new Paragraph(tokenizer.nextToken()).setFont(font)));
			} else {
				table.addCell(new Cell().add(new Paragraph(tokenizer.nextToken()).setFont(font)));
			}
		}
	}
}
