package com.greensqa.pnf.carvajal.factura.allocator.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Genera archivos de prueba de Carvajal y los distribuye en una lista de directorios.
 * @author Andrés Fernando Gasca
 *
 */
public class FilesGenerator {

	/**
	 * Ruta del archivo con la lista de directorios de salida (donde deben quedar guardados los archivos).
	 */
	private String directoriesOutFilePath;
	
	/**
	 * Lista de los directorios de salida.
	 */
	private ArrayList<String> directoriesOut;
	
	/**
	 * Ruta del archivo base.
	 */
	private String baseFilePath;
	
	/**
	 * Número a partir del cual se empiezan a enumerar los archivos.
	 */
	private int startIndex;
	
	/**
	 * Número de archivos que debe haber en cada directorio.
	 */
	private int filesPerDirectory;
	
	/**
	 * Contenido del archivo base en cadena de texto.
	 */
	private String fileContent;
	
	public FilesGenerator(String baseFilePath, String directoriesOutFilePath, int startIndex, int filesPerDirectory) throws IOException {
		this.baseFilePath = baseFilePath;
		this.directoriesOutFilePath = directoriesOutFilePath;
		this.startIndex = startIndex;
		this.filesPerDirectory = filesPerDirectory;
		
		loadPaths();
		loadBaseFileContent();
	}
	
	/**
	 * Carga las rutas de los directorios donde deben quedar los archivos.
	 * @throws IOException
	 */
	private void loadPaths() throws IOException {
		try (FileReader fr = new FileReader(directoriesOutFilePath);
				BufferedReader br = new BufferedReader(fr);) {
			String str = "";
			
			while (true) {
				str = br.readLine();
				if (str == null || str.equals("")) {
					break;
				}
				directoriesOut.add(str);
			}
		}
	}
	
	private void loadBaseFileContent() {
	    StringBuilder contentBuilder = new StringBuilder();
	    try (Stream<String> stream = Files.lines( Paths.get(baseFilePath), StandardCharsets.UTF_8)) {
	        stream.forEach(s -> contentBuilder.append(s).append("\n"));
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	    fileContent = contentBuilder.toString();
	}

	public String getDirectoriesOutFilePath() {
		return directoriesOutFilePath;
	}

	public void setDirectoriesOutFilePath(String directoriesOutFilePath) {
		this.directoriesOutFilePath = directoriesOutFilePath;
	}

	public ArrayList<String> getDirectoriesOut() {
		return directoriesOut;
	}

	public void setDirectoriesOut(ArrayList<String> directoriesOut) {
		this.directoriesOut = directoriesOut;
	}

	public String getBaseFilePath() {
		return baseFilePath;
	}

	public void setBaseFilePath(String baseFilePath) {
		this.baseFilePath = baseFilePath;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getFilesPerDirectory() {
		return filesPerDirectory;
	}

	public void setFilesPerDirectory(int filesPerDirectory) {
		this.filesPerDirectory = filesPerDirectory;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}
}
