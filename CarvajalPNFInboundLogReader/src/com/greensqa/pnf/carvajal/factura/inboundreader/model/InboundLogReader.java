package com.greensqa.pnf.carvajal.factura.inboundreader.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InboundLogReader {

	/**
	 * Directorio con los logs del Inbound de la prueba ejecutada.
	 */
	private String logsDirectory;
	
	/**
	 * Lista de archivos para buscarlos en filesRead y filesProcessed.
	 */
	private ArrayList<String> files;
	
	/**
	 * Archivos que han sido leídos por el Inbound, con fecha y hora en la que fueron leídos. Línea de ejemplo
	 * dentro del log:
	 * 018-07-24 19:06:36.264 [INFO ] [ValidateManagerImpl:219] - Validacion de metadadata de archivo: FacturaA1344542.xml peso: 39560
	 */
	private HashMap<String, String> readFiles;
	
	/**
	 * Archivos que han sido movidos al post-office y la fecha y hora en la que fueron movidos. Línea de ejemplo dentro del log:
	 * 2018-07-24 19:06:36.235 [INFO ] [TransformMessageProcessor:36] - Mensaje enviado al post-office: {
  	 * "file_name": "FacturaA1344507.xml",
  	 * "file_size": "39560",
  	 * "account_type": "SENDER",
  	 * "factoring_filetype": "",
  	 * "reception_date": "2018-07-24T19:06:36.235",
  	 * "subdirectory": "",
  	 * "document_type": "",
  	 * "transaction_id": "2014b6beda5e4cce8c78462657c12f3c",
  	 * "company": "811007832",
  	 * "account": "811007832_03",
  	 * "country": "CO",
  	 * "document_storage_id": "b8bdf90b30d2445ab5fcbdbd712cd96f",
  	 * "channel": "SFTP_AWS"
	 * }
	 */
	private HashMap<String, String> processedFiles;
	
	/**
	 * Cantidad de archivos leídos.
	 */
	private int readFilesNum;
	
	/**
	 * Cantidad de archivos enviados al post-office.
	 */
	private int processedFilesNum;
	
	/**
	 * Path del archivo donde se escribirán los resultados.
	 */
	private String resultsFilePath;
	
	public InboundLogReader(String logsDirectory, String resultsFilePath) throws FileNotFoundException, IOException, ParseException {
		this.logsDirectory = logsDirectory;
		this.resultsFilePath = resultsFilePath;
		files = new ArrayList<>();
		readFiles = new HashMap<>();
		processedFiles = new HashMap<>();
		readFilesNum = 0;
		processedFilesNum = 0;	
		
		File directory = new File(logsDirectory);
		File[] filesArray = directory.listFiles();
		String fileStr = "";
		String timestamp = null;
		
		Pattern filePattern = Pattern.compile("[A-Za-z0-9]*.xml");
		Pattern timestampPattern = Pattern.compile("[0-9]+-[0-9]+-[0-9]+ [0-9]+:[0-9]+:[0-9]+.[0-9]+");
		Matcher fileMatcher = null;
		Matcher timestampMatcher = null;
		boolean isProcessed = false;
		
		for (int i = 0; i < filesArray.length; i++) {
			try (FileReader fr = new FileReader(filesArray[i]);
					BufferedReader br = new BufferedReader(fr)) {
				while (true) {
					String str = br.readLine();
					if (str == null) {
						break;
					}
					
					if (str.contains("Validacion de metadadata de archivo")) {
						fileMatcher = filePattern.matcher(str);
						if (fileMatcher.find()) {
							fileStr = fileMatcher.group();
						}
						timestampMatcher = timestampPattern.matcher(str);
						if (timestampMatcher.find()) {
							timestamp = timestampMatcher.group();
						}
						readFiles.put(fileStr, timestamp);
						files.add(fileStr);
						readFilesNum += 1;
					} else if (str.contains("Mensaje enviado al post-office")) {
						isProcessed = true;
						timestampMatcher = timestampPattern.matcher(str);
						if (timestampMatcher.find()) {
							timestamp = timestampMatcher.group();
						}
					} else if (str.contains("file_name") && isProcessed) {
						isProcessed = false;
						fileMatcher = filePattern.matcher(str);
						if (fileMatcher.find()) {
							fileStr = fileMatcher.group();
						}
						processedFiles.put(fileStr, timestamp);
						processedFilesNum += 1;
					}
				}
				System.out.println("Reading files: " + (i * 100 /filesArray.length) + "%");
			}
		}
		System.out.println("Reading files: 100%");
	}
	
	public void writeResults() throws IOException {
		File file = new File(resultsFilePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		try (FileWriter fw = new FileWriter(file, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter pw = new PrintWriter(bw)) {
			pw.write("Leídos: " + readFilesNum + "\r\n");
			pw.write("Procesados: " + processedFilesNum + "\r\n-----------------------------------------------\r\n");
			pw.write("Nombre Archivo\tFecha Leído\tFecha Procesado\r\n");
			for (int i = 0; i < files.size(); i++) {
				String line = "";
				String fileName = files.get(i);
				String timestampRead = readFiles.get(fileName);
				String timestampReadStr = timestampRead == null ? "null" : timestampRead.toString();
				String timestampProcessed = processedFiles.get(fileName);
				String timestampProcessedStr = timestampProcessed == null ? "null" : timestampProcessed.toString();
				line = fileName + "\t" + timestampReadStr + "\t" + timestampProcessedStr;
				pw.write(line + "\r\n");
				System.out.println("Writing results: " + (i * 100 / files.size()) + "%");
			}
			System.out.println("Writing results: 100%");
		}
	}

	public String getLogsDirectory() {
		return logsDirectory;
	}

	public void setLogsDirectory(String logsDirectory) {
		this.logsDirectory = logsDirectory;
	}

	public ArrayList<String> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<String> files) {
		this.files = files;
	}

	public HashMap<String, String> getReadFiles() {
		return readFiles;
	}

	public void setReadFiles(HashMap<String, String> readFiles) {
		this.readFiles = readFiles;
	}

	public HashMap<String, String> getProcessedFiles() {
		return processedFiles;
	}

	public void setProcessedFiles(HashMap<String, String> processedFiles) {
		this.processedFiles = processedFiles;
	}

	public int getReadFilesNum() {
		return readFilesNum;
	}

	public void setReadFilesNum(int readFilesNum) {
		this.readFilesNum = readFilesNum;
	}

	public int getProcessedFilesNum() {
		return processedFilesNum;
	}

	public void setProcessedFilesNum(int processedFilesNum) {
		this.processedFilesNum = processedFilesNum;
	}

	public String getResultsFilePath() {
		return resultsFilePath;
	}

	public void setResultsFilePath(String resultsFilePath) {
		this.resultsFilePath = resultsFilePath;
	}
}
