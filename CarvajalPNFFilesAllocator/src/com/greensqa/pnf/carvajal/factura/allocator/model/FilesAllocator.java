package com.greensqa.pnf.carvajal.factura.allocator.model;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

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
	 * Ruta del archivo con las credenciales AWS.
	 */
	private String awsCredentialsFilePath;
	
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
	
	/**
	 * Mensaje cuando se han finalizado todas las tareas de la aplicación.
	 */
	public static String FINISHED_MSG = "Terminado: 100%";
	
	/**
	 * Credenciales de acceso a AWS S3 Bucket.
	 */
	private BasicAWSCredentials credentials;
	
	/**
	 * Cliente Amazon S3.
	 */
	private AmazonS3 s3Client;
	
	/**
	 * Nombre del bucket S3 de Amazon.
	 */
	private String bucketName;
	
	/**
	 * Base para calcular el progreso de la última etapa (la de mover los archivos).
	 */
	private int totalIterationsMovingFiles;
	
	/**
	 * Cantidad de hilos usados para subir los archivos al bucketS3.
	 */
	private int threads;

	public FilesAllocator(String directoriesInFilePath, String directoriesOutFilePath, String awsCredentialsFilePath, int filesPerDirectory) throws IOException, URISyntaxException {
		this.directoriesInFilePath = directoriesInFilePath;
		this.directoriesOutFilePath = directoriesOutFilePath;
		this.awsCredentialsFilePath = awsCredentialsFilePath;
		this.filesPerDirectory = filesPerDirectory;
		directoriesIn = new ArrayList<>();
		directoriesOut = new ArrayList<>();
		
		String[] creds = CarvajalPNFAllocatorUtils.getCredentialsFromFile(awsCredentialsFilePath);
		
		this.credentials = new BasicAWSCredentials(creds[0], creds[1]);
		this.s3Client = AmazonS3Client.builder().withRegion(creds[3]).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		this.bucketName = creds[2];
	}
	
	/**
	 * Se cargan las rutas de los directorios de entrada y de salida.
	 * @throws IOException si ocurre algún error de lectura/escritura.
	 */
	public void loadPaths() throws IOException {
		currentTaskMessage = "";
		currentStage = 1;
		currentStageMessage = "Listando directorios de entrada (" + currentStage + "/" + STAGES + ")...";
		currentTaskMessage = "";
		CarvajalPNFAllocatorUtils.loadDirectoriesFromFile(directoriesOutFilePath, directoriesOut);
		currentStage = 2;
		currentStageMessage = "Listando directorios de salida (" + currentStage + "/" + STAGES + ")...";
		currentTaskMessage = "";
		CarvajalPNFAllocatorUtils.loadDirectoriesFromFile(directoriesInFilePath, directoriesIn);
		loadTotalIterationsMovingFiles();
	}
	
	/**
	 * Mueve los archivos de los directorios de entrada a los directorios de salida. En cada directorio de salida
	 * queda la cantidad de archivos indicada por el usuario.
	 * @throws Exception si ocurre un error moviendo los archivos.
	 */
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
				//fPath = fPath.replace('\\', '/');
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
	
	public void moveFilesToBucket() {
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
			directory = directory.replace('\\', '/');
			for (int j = 0; j < filesPerDirectory && filesInIndex < filesIn.length; j++) {
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
				//fPath = fPath.replace('\\', '/');
				File file = new File(fPath);
				String fileName = file.getName();
				String newFilePath = directory + "/" + fileName;
				currentStageProgress = (filesInIndex + 1) * 100 / filesIn.length;
				currentTaskMessage = "Moviendo archivo\nOrigen: \"" + fPath + "\"\nDestino: \"" + newFilePath + "\"\n" + currentStageProgress + "%";
				//file.renameTo(new File(newFilePath));
				//FileUtils.moveFile(file, new File(newFilePath));
				moveFileToS3Bucket(newFilePath, file);
				//moveFileToS3Bucket(fileName, file);
				//System.out.println("Progress: " + ((filesInIndex + 0.0) / (filesIn.length + 0.0) * 100.0) + "%");
				filesInIndex += 1;
			}
		}
		currentStageMessage = FINISHED_MSG;
		currentTaskMessage = "";
	}
	
	/**
	 * Permite enviar un archivo hacia el bucket S3.
	 * @param keyName Nombre del archivo dentro del bucket S3 (los delimitadores "/" se usan como estructuras de
	 * directorios dentro del bucket.
	 * @param file Archivo local que se enviará al bucket S3.
	 */
	private void moveFileToS3Bucket(String keyName, File file) {
		try {
			s3Client.putObject(new PutObjectRequest(bucketName, keyName, file));	
		} catch (SdkClientException ex) {
			ex.printStackTrace();
		}
	}
	
	private void loadTotalIterationsMovingFiles() {
		int sizeOfDirectoriesOut = directoriesOut.size();
		int sizeOfFilesIn = filesIn.length;
		
		setTotalIterationsMovingFiles(Math.min(sizeOfDirectoriesOut * filesPerDirectory, sizeOfFilesIn));
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

	public String getAwsCredentialsFilePath() {
		return awsCredentialsFilePath;
	}

	public void setAwsCredentialsFilePath(String awsCredentialsFilePath) {
		this.awsCredentialsFilePath = awsCredentialsFilePath;
	}

	public int getTotalIterationsMovingFiles() {
		return totalIterationsMovingFiles;
	}

	public void setTotalIterationsMovingFiles(int totalIterationsMovingFiles) {
		this.totalIterationsMovingFiles = totalIterationsMovingFiles;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}
}
