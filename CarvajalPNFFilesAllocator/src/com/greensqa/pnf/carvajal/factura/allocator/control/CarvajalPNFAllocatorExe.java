package com.greensqa.pnf.carvajal.factura.allocator.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.greensqa.pnf.carvajal.factura.allocator.model.FilesAllocator;
import com.greensqa.pnf.carvajal.factura.allocator.model.FilesGenerator;
import com.greensqa.pnf.carvajal.factura.allocator.view.CarvajalPNFAllocatorFrame;
import com.greensqa.pnf.carvajal.factura.allocator.view.CarvajalPNFAllocatorPanel;

public class CarvajalPNFAllocatorExe {

	private static CarvajalPNFAllocatorFrame frame;
	private static CarvajalPNFAllocatorPanel panel;
	
	public static void main(String[] args) {
		//createFiles();
		//moveFiles();
		//moveDataTestFiles();
		startApp();
	}
	
	public static void createFiles() {
		String baseFilePath = "D:\\factura\\archivo entrada\\face_f205057792910000003191.xml";
		String directoriesOutFilePath = "D:\\factura\\directorios salida\\directorios.txt";
		int filesPerDirectory = 1500000;
		int fileIndex = 0;
		try {
			FilesGenerator fg = new FilesGenerator(baseFilePath, directoriesOutFilePath, filesPerDirectory, fileIndex);
			fg.startFilesGeneration();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void startApp() {
		panel = new CarvajalPNFAllocatorPanel();
		frame = new CarvajalPNFAllocatorFrame("Mover Archivos Facturas", panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		listenFileLoaders();
		listenOk();
	}
	
	/**
	 * Handler del evento de hacer click sobre el botón Aceptar.
	 */
	public static void listenOk() {
		panel.getAccept().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!panel.isValidInput()) {
					JOptionPane.showMessageDialog(null, "Las entradas son inválidas. Verifíquelas.", "Entradas Inválidas", JOptionPane.WARNING_MESSAGE);
					return;
				}
				panel.getAccept().setEnabled(false);
				panel.getDirectoriesInButton().setEnabled(false);
				panel.getDirectoriesOutButton().setEnabled(false);
				panel.getAwsCredentialsButton().setEnabled(false);
				String directoriesInFilePath = panel.getDirectoriesInLabel().getText();
				String directoriesOutFilePath = panel.getDirectoriesOutLabel().getText();
				String awsCredentialsFilePath = panel.getAwsCredentialsLabel().getText();
				int filesPerDirectory = Integer.parseInt(panel.getFilesPerDirectoryField().getText());
				
				try {
					FilesAllocator fa = new FilesAllocator(directoriesInFilePath, directoriesOutFilePath, awsCredentialsFilePath, filesPerDirectory);
					fa.setThreads(1);
					updateStatus(fa);
					CarvajalPNFAllocatorThread thread = new CarvajalPNFAllocatorThread(fa, panel);
					thread.start();
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Se presentó un error " + e1.getMessage(), "Error moviendo los archivos", JOptionPane.ERROR);
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Se presentó un error " + e1.getMessage(), "Error moviendo los archivos", JOptionPane.ERROR);
				}
			}
		});
	}
	
	public static void updateStatus(FilesAllocator fa) {
		@SuppressWarnings("rawtypes")
		final SwingWorker worker = new SwingWorker() {

			@Override
			protected Object doInBackground() throws Exception {
				fa.loadPaths();
				//fa.moveFilesToDirectory();
				fa.moveFilesToBucket();
				return null;
			}
		};
		
		worker.execute();
	}
	
	public static void listenFileLoaders() {
		JFileChooser fcIn = panel.getDirectoriesInFC();
		JFileChooser fcOut = panel.getDirectoriesOutFC();
		JFileChooser fcCredentials = panel.getAwsCredentialsFC();
		
		panel.getDirectoriesInButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int option = fcIn.showOpenDialog(panel);
				if (option == JFileChooser.APPROVE_OPTION) {
					File file = fcIn.getSelectedFile();
					panel.getDirectoriesInLabel().setText(file.getAbsolutePath());
				}
			}
		});
		
		panel.getDirectoriesOutButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int option = fcOut.showOpenDialog(panel);
				if (option == JFileChooser.APPROVE_OPTION) {
					File file = fcOut.getSelectedFile();
					panel.getDirectoriesOutLabel().setText(file.getAbsolutePath());
				}
			}
		});
		
		panel.getAwsCredentialsButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int option = fcCredentials.showOpenDialog(panel);
				if (option == JFileChooser.APPROVE_OPTION) {
					File file = fcCredentials.getSelectedFile();
					panel.getAwsCredentialsLabel().setText(file.getAbsolutePath());
				}
			}
		});
	}
}
