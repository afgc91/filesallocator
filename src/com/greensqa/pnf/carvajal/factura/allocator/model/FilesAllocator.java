package com.greensqa.pnf.carvajal.factura.allocator.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

public class FilesAllocator {

	/**
	 * Ruta del archivo que contiene los folders dentro de los cuales están los archivos que hay que mover.
	 */
	private String directoriesInFilePath;
	
	/**
	 * Ruta del archivo con la lista de directorios de salida (donde deben quedar guardados los archivos).
	 */
	private String directoriesOutFilePath;
	
	/**
	 * Cantidad máxima de archivos que debe haber por cada directorio.
	 */
	private int filesPerDirectory;
	
	/**
	 * Lista de directorios de entrada.
	 */
	private ArrayList<String> directoriesIn;
	
	/**
	 * Lista de directorios de salida.
	 */
	private ArrayList<String> directoriesOut;
	
	/**
	 * Todos los archivos de entrada, leídos de los directorios de entrada.
	 */
	private String[] filesIn;
	
	/**
	 * Número de etapas que tiene el pograma.
	 * 1. Listando directorios de entrada.
	 * 2. Listando directorios de salida.
	 * 3. Chequeando archivos de entrada.
	 * 4. Moviendo archivos.
	 */
	public static final int STAGES = 4;
	
	/**
	 * Etapa actual en la que se encuentra el programa.
	 */
	private int currentStage = 1;
	
	/**
	 * Progreso porcentual de la etapa actual en la que se encuentra el programa.
	 */
	private int currentStageProgress = 0;
	
	/**
	 * Mensaje de la tarea que está ejecutándose.
	 */
	private String currentTaskMessage = "";
	
	/**
	 * Mensaje de la etapa en la que se encuentra el programa.
	 */
	private String currentStageMessage = "";
	
	public static String FINISHED_MSG = "Terminado: 100%";

	public FilesAllocator(String directoriesInFilePath, String directoriesOutFilePath, int filesPerDirectory) throws IOException {
		this.directoriesInFilePath = directoriesInFilePath;
		this.directoriesOutFilePath = directoriesOutFilePath;
		this.filesPerDirectory = filesPerDirectory;
		directoriesIn = new ArrayList<>();
		directoriesOut = new ArrayList<>();
	}
	
	public void loadPaths() throws IOException {
		currentTaskMessage = "";
		currentStage = 1;
		currentStageMessage = "Listando directorios de entrada (" + currentStage + "/" + STAGES + ")...";
		currentTaskMessage = "";
		CarvajalPNFAllocatorUtils.loadPaths(directoriesOutFilePath, directoriesOut);
		currentStage = 2;
		currentStageMessage = "Listando directorios de salida (" + currentStage + "/" + STAGES + ")...";
		currentTaskMessage = "";
		CarvajalPNFAllocatorUtils.loadPaths(directoriesInFilePath, directoriesIn);
	}
	
	public void moveFilesToDirectory() throws Exception {
		currentStage = 3;
		currentStageMessage = "Chequeando archivos de entrada (" + currentStage + "/" + STAGES + ")...";
		currentTaskMessage = "";
		String dirPrefix = "VTQhQc62";
		String originalDirectory = "";
		for (int i = 0; i < directoriesIn.size(); i++) {
			String directory = directoriesIn.get(i);
			currentStageProgress = (i + 1) * 100 / directoriesIn.size();
			currentTaskMessage = "Leyendo archivos de " + directory + ": " + currentStageProgress + "%";
			File dir = new File(directory);
			if (i == 0) {
				filesIn = CarvajalPNFAllocatorUtils.concatArrays(new String[]{dirPrefix + directory}, dir.list());
			} else {
				this.setFilesIn(CarvajalPNFAllocatorUtils.concatArrays(filesIn, new String[]{dirPrefix + directory}));
				this.setFilesIn(CarvajalPNFAllocatorUtils.concatArrays(filesIn, dir.list()));
			}
		}
		currentStageProgress = 100;
		
		int filesInIndex = 0;
		currentStage = 4;
		currentStageMessage = "Moviendo archivos (" + currentStage + "/" + STAGES + ")...";
		currentTaskMessage = "";
		for (int i = 0; i < directoriesOut.size() && filesInIndex < filesIn.length; i++) {
			currentStageProgress = (filesInIndex + 1) * 100 / filesIn.length;
			String directory = directoriesOut.get(i);
			File dir = new File(directory);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			for (int numberOfFiles = dir.list().length; numberOfFiles < filesPerDirectory && filesInIndex < filesIn.length; numberOfFiles++) {
				String fileItem = filesIn[filesInIndex];
				String fPath = "";
				for (; filesInIndex < filesIn.length && fileItem.contains(dirPrefix);) {
					originalDirectory = fileItem.substring(dirPrefix.length(), fileItem.length()) + "\\";
					filesInIndex += 1;
					fileItem = filesIn[filesInIndex];
				}
				if (filesInIndex == filesIn.length) {
					break;
				}
				fPath = originalDirectory + filesIn[filesInIndex];
				File file = new File(fPath);
				String fileName = file.getName();
				String newFilePath = directory + "\\" + fileName;
				currentStageProgress = (filesInIndex + 1) * 100 / filesIn.length;
				currentTaskMessage = "Moviendo archivo\nOrigen: \"" + fPath + "\"\nDestino: \"" + newFilePath + "\"\n" + currentStageProgress + "%";
				//file.renameTo(new File(newFilePath));
				FileUtils.moveFile(file, new File(newFilePath));
				//System.out.println("Progress: " + ((filesInIndex + 0.0) / (filesIn.length + 0.0) * 100.0) + "%");
				filesInIndex += 1;
			}
		}
		currentStageMessage = FINISHED_MSG;
		currentTaskMessage = "";
	}

	public String getDirectoriesInFilePath() {
		return directoriesInFilePath;
	}

	public void setDirectoriesInFilePath(String directoriesInFilePath) {
		this.directoriesInFilePath = directoriesInFilePath;
	}

	public String getDirectoriesOutFilePath() {
		return directoriesOutFilePath;
	}

	public void setDirectoriesOutFilePath(String directoriesOutFilePath) {
		this.directoriesOutFilePath = directoriesOutFilePath;
	}

	public int getFilesPerDirectory() {
		return filesPerDirectory;
	}

	public void setFilesPerDirectory(int filesPerDirectory) {
		this.filesPerDirectory = filesPerDirectory;
	}

	public ArrayList<String> getDirectoriesIn() {
		return directoriesIn;
	}

	public void setDirectoriesIn(ArrayList<String> directoriesIn) {
		this.directoriesIn = directoriesIn;
	}

	public ArrayList<String> getDirectoriesOut() {
		return directoriesOut;
	}

	public void setDirectoriesOut(ArrayList<String> directoriesOut) {
		this.directoriesOut = directoriesOut;
	}
	
	public String[] getFilesIn() {
		return this.filesIn;
	}
	
	public void setFilesIn(String[] filesIn) {
		this.filesIn = filesIn;
	}
	
	public int getCurrentStage() {
		return currentStage;
	}

	public void setCurrentStage(int currentStage) {
		this.currentStage = currentStage;
	}

	public int getCurrentStageProgress() {
		return currentStageProgress;
	}

	public void setCurrentStageProgress(int currentStageProgress) {
		this.currentStageProgress = currentStageProgress;
	}

	public String getCurrentTaskMessage() {
		return currentTaskMessage;
	}

	public void setCurrentTaskMessage(String currentTaskMessage) {
		this.currentTaskMessage = currentTaskMessage;
	}

	public String getCurrentStageMessage() {
		return currentStageMessage;
	}

	public void setCurrentStageMessage(String currentStageMessage) {
		this.currentStageMessage = currentStageMessage;
	}
}
