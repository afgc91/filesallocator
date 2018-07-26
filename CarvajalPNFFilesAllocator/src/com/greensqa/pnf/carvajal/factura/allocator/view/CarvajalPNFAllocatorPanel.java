package com.greensqa.pnf.carvajal.factura.allocator.view;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class CarvajalPNFAllocatorPanel extends JPanel {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = 5329744886376389459L;
	
	/**
	 * Etiqueta para mostrar el archivo de directorios de entrada seleccionado.
	 */
	private JLabel directoriesInLabel;
	
	/**
	 * Etiqueta para mostrar el archivo de directorios de salida seleccionado.
	 */
	private JLabel directoriesOutLabel;
	
	/**
	 * Etiqueta para mostrar el archivo de las credenciales AWS.
	 */
	private JLabel awsCredentialsLabel;
	
	/**
	 * Botón para seleccionar archivo de directorios de entrada.
	 */
	private JButton directoriesInButton;
	
	/**
	 * Botón para seleccionar archivo de directorios de salida.
	 */
	private JButton directoriesOutButton;
	
	/**
	 * Botón para seleccionar archivo de credenciales AWS.
	 */
	private JButton awsCredentialsButton;
	
	/**
	 * Seleccionador de archivo de directorios de entrada.
	 */
	private JFileChooser directoriesInFC;
	
	/**
	 * Seleccionador de archivo de directorios de salida.
	 */
	private JFileChooser directoriesOutFC;
	
	/**
	 * Seleccionador de archivo de credenciales AWS.
	 */
	private JFileChooser awsCredentialsFC;
	
	/**
	 * Área para mostrar las actividades que se van ejecutando en la aplicación.
	 */
	private JTextArea tasksArea;
	
	/**
	 * Etiqueta de la cantidad de archivos por folder.
	 */
	private JLabel filesPerDirectoryLabel;
	
	/**
	 * Campo para ingresar número de archivos por folder;
	 */
	private JTextField filesPerDirectoryField;
	
	/**
	 * Botón para iniciar la ejecución.
	 */
	private JButton accept;
	
	/**
	 * Texto cuando no se ha seleccionado nada.
	 */
	private static final String DIR_SELECTED_DEFAULT = "<<Seleccione archivo...>>";
	
	public CarvajalPNFAllocatorPanel() {
		this.setLayout(null);
		initializeComponents();
	}
	
	/**
	 * Inicia los componentes de la ventana, los ajusta y los ubica dentro del panel.
	 */
	private void initializeComponents() {
		//Inicializar componentes.
		directoriesInButton = new JButton("Archivo Directorios Entrada");
		directoriesOutButton = new JButton("Archivo Directorios Salida");
		awsCredentialsButton = new JButton("Archivo Credenciales AWS");
		directoriesInLabel = new JLabel(DIR_SELECTED_DEFAULT);
		directoriesOutLabel = new JLabel(DIR_SELECTED_DEFAULT);
		awsCredentialsLabel = new JLabel(DIR_SELECTED_DEFAULT);
		directoriesInFC = new JFileChooser();
		directoriesOutFC = new JFileChooser();
		awsCredentialsFC =  new JFileChooser();
		tasksArea = new JTextArea();
		filesPerDirectoryLabel = new JLabel("No. de Archivos por Directorio");
		filesPerDirectoryField = new JTextField("20000");
		accept = new JButton("Aceptar");
		
		//Ajustar componentes.
		int widthLabel = 1000, heightLabel = 20;
		directoriesInLabel.setSize(widthLabel, heightLabel);
		directoriesOutLabel.setSize(widthLabel, heightLabel);
		awsCredentialsLabel.setSize(widthLabel, heightLabel);
		directoriesInButton.setSize(directoriesInButton.getPreferredSize());
		directoriesOutButton.setSize(directoriesInButton.getPreferredSize());
		awsCredentialsButton.setSize(directoriesInButton.getPreferredSize());
		tasksArea.setSize(415, 100);
		tasksArea.setEditable(false);
		filesPerDirectoryLabel.setSize(filesPerDirectoryLabel.getPreferredSize());
		filesPerDirectoryField.setSize(50, 20);
		accept.setSize(accept.getPreferredSize());
		
		//Ubicar componentes en el panel.
		int x = 30, y = 30, d = 40;
		directoriesInButton.setLocation(x, y);
		y += d;
		directoriesOutButton.setLocation(x, y);
		y += d;
		awsCredentialsButton.setLocation(x, y);
		y += d;
		filesPerDirectoryLabel.setLocation(x, y);
		x += directoriesInButton.getWidth() + d / 2; y = 30;
		directoriesInLabel.setLocation(x, y);
		y += d;
		directoriesOutLabel.setLocation(x, y);
		y += d;
		awsCredentialsLabel.setLocation(x, y);
		y += d;
		filesPerDirectoryField.setLocation(x, y);
		y += d; x -= 200;
		tasksArea.setLocation(x, y);
		y += 3 * d; x += 165;
		accept.setLocation(x, y);
		
		//Agregar componentes al panel.
		this.add(directoriesInButton);
		this.add(directoriesOutButton);
		this.add(awsCredentialsButton);
		this.add(directoriesInLabel);
		this.add(directoriesOutLabel);
		this.add(awsCredentialsLabel);
		this.add(filesPerDirectoryField);
		this.add(filesPerDirectoryLabel);
		this.add(tasksArea);
		this.add(accept);
	}
	
	/**
	 * Verifica si las entradas son válidas.
	 * @return true si se seleccionaron archivos y si la cantidad de archivos por folder es numérica.
	 * fase si alguna condición no se cumple.
	 */
	public boolean isValidInput() {
		boolean directoriesInFlag = ! directoriesInLabel.getText().equals(DIR_SELECTED_DEFAULT) &&
				! directoriesInLabel.getText().equals(directoriesOutLabel.getText()) &&
				! awsCredentialsLabel.getText().equals(directoriesOutLabel.getText());
		boolean directoriesOutFlag = ! directoriesOutLabel.getText().equals(DIR_SELECTED_DEFAULT);
		boolean awsCredentialsFlag = ! awsCredentialsLabel.getText().equals(DIR_SELECTED_DEFAULT);
		boolean filesPerDirectoryFlag = filesPerDirectoryField.getText().matches("\\d+");
		return directoriesInFlag && directoriesOutFlag && filesPerDirectoryFlag && awsCredentialsFlag;
	}

	public JLabel getAwsCredentialsLabel() {
		return awsCredentialsLabel;
	}

	public void setAwsCredentialsLabel(JLabel awsCredentialsLabel) {
		this.awsCredentialsLabel = awsCredentialsLabel;
	}

	public JButton getAwsCredentialsButton() {
		return awsCredentialsButton;
	}

	public void setAwsCredentialsButton(JButton awsCredentialsButton) {
		this.awsCredentialsButton = awsCredentialsButton;
	}

	public JFileChooser getAwsCredentialsFC() {
		return awsCredentialsFC;
	}

	public void setAwsCredentialsFC(JFileChooser awsCredentialsFC) {
		this.awsCredentialsFC = awsCredentialsFC;
	}

	public JLabel getDirectoriesInLabel() {
		return directoriesInLabel;
	}

	public JLabel getFilesPerDirectoryLabel() {
		return filesPerDirectoryLabel;
	}

	public void setFilesPerDirectoryLabel(JLabel filesPerDirectoryLabel) {
		this.filesPerDirectoryLabel = filesPerDirectoryLabel;
	}

	public JTextField getFilesPerDirectoryField() {
		return filesPerDirectoryField;
	}

	public void setFilesPerDirectoryField(JTextField filesPerDirectoryField) {
		this.filesPerDirectoryField = filesPerDirectoryField;
	}

	public JButton getAccept() {
		return accept;
	}

	public void setAccept(JButton accept) {
		this.accept = accept;
	}

	public void setDirectoriesInLabel(JLabel directoriesInLabel) {
		this.directoriesInLabel = directoriesInLabel;
	}

	public JLabel getDirectoriesOutLabel() {
		return directoriesOutLabel;
	}

	public void setDirectoriesOutLabel(JLabel directoriesOutLabel) {
		this.directoriesOutLabel = directoriesOutLabel;
	}

	public JButton getDirectoriesInButton() {
		return directoriesInButton;
	}

	public void setDirectoriesInButton(JButton directoriesInButton) {
		this.directoriesInButton = directoriesInButton;
	}

	public JButton getDirectoriesOutButton() {
		return directoriesOutButton;
	}

	public void setDirectoriesOutButton(JButton directoriesOutButton) {
		this.directoriesOutButton = directoriesOutButton;
	}

	public JFileChooser getDirectoriesInFC() {
		return directoriesInFC;
	}

	public void setDirectoriesInFC(JFileChooser directoriesInFC) {
		this.directoriesInFC = directoriesInFC;
	}

	public JFileChooser getDirectoriesOutFC() {
		return directoriesOutFC;
	}

	public void setDirectoriesOutFC(JFileChooser directoriesOutFC) {
		this.directoriesOutFC = directoriesOutFC;
	}

	public JTextArea getTasksArea() {
		return tasksArea;
	}

	public void setTasksArea(JTextArea tasksArea) {
		this.tasksArea = tasksArea;
	}

}
