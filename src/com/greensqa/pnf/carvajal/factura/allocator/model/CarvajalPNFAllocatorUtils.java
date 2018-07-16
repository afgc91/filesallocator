package com.greensqa.pnf.carvajal.factura.allocator.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class CarvajalPNFAllocatorUtils {

	/**
	 * Carga las rutas de los directorios del archivo de entrada.
	 * @throws IOException
	 */
	public static void loadPaths(String directoriesOutFilePath, ArrayList<String> directoriesOut) throws IOException {
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
	
	/**
	 * Concatena dos Arrays
	 * @param a Array 1
	 * @param b Array 2
	 * @return Array 1 + Array 2
	 */
	public static <T> T[] concatArrays(T[] a, T[] b) {
	    int aLen = a.length;
	    int bLen = b.length;

	    @SuppressWarnings("unchecked")
	    T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
	    System.arraycopy(a, 0, c, 0, aLen);
	    System.arraycopy(b, 0, c, aLen, bLen);

	    return c;
	}
}
