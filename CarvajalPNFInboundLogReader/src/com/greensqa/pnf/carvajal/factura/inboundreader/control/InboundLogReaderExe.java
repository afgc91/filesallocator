package com.greensqa.pnf.carvajal.factura.inboundreader.control;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import com.greensqa.pnf.carvajal.factura.inboundreader.model.InboundLogReader;

public class InboundLogReaderExe {

	public static void main(String[] args) {
		//testListFiles();
		try {
			startApp();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testListFiles() {
		String logsDirectory = "C:\\Users\\andy-\\OneDrive - Carvajal S.A\\Documents\\Logs-Inbound-Prueba3\\Logs Server 186\\Inbound1";
		File directory = new File(logsDirectory);
		File[] files = directory.listFiles();
		Arrays.sort(files);
		for (int i = 0; i < files.length; i++) {
			System.out.println(files[i].getName());
		}
	}
	
	public static void startApp() throws IOException, ParseException {
		String logsDirectory = "C:\\Users\\andy-\\OneDrive - Carvajal S.A\\Documents\\Logs-Inbound-Prueba2\\Log Server 186\\Inbound1";
		InboundLogReader logReaderTest2Inbound1 = new InboundLogReader(logsDirectory, "D:\\inbound\\test2-186-inbound1.txt");
		logReaderTest2Inbound1.writeResults();
		
		logsDirectory = "C:\\Users\\andy-\\OneDrive - Carvajal S.A\\Documents\\Logs-Inbound-Prueba2\\Log Server 186\\Inbound2";
		InboundLogReader logReaderTest2Inbound2 = new InboundLogReader(logsDirectory, "D:\\inbound\\test2-186-inbound2.txt");
		logReaderTest2Inbound2.writeResults();
		
		logsDirectory = "C:\\Users\\andy-\\OneDrive - Carvajal S.A\\Documents\\Logs-Inbound-Prueba3\\Logs Server 186\\Inbound1";
		InboundLogReader logReaderTest3Inbound1 = new InboundLogReader(logsDirectory, "D:\\inbound\\test3-186-inbound1.txt");
		logReaderTest3Inbound1.writeResults();
		
		logsDirectory = "C:\\Users\\andy-\\OneDrive - Carvajal S.A\\Documents\\Logs-Inbound-Prueba3\\Logs Server 186\\Inbound2";
		InboundLogReader logReaderTest3Inbound2 = new InboundLogReader(logsDirectory, "D:\\inbound\\test3-186-inbound2.txt");
		logReaderTest3Inbound2.writeResults();
		
		logsDirectory = "C:\\Users\\andy-\\OneDrive - Carvajal S.A\\Documents\\Logs-Inbound-Prueba3\\Logs Server 25\\Inbound1";
		InboundLogReader logReaderTest3Inbound3 = new InboundLogReader(logsDirectory, "D:\\inbound\\test3-25-inbound1.txt");
		logReaderTest3Inbound3.writeResults();
		
		logsDirectory = "C:\\Users\\andy-\\OneDrive - Carvajal S.A\\Documents\\Logs-Inbound-Prueba3\\Logs Server 25\\Inbound2";
		InboundLogReader logReaderTest3Inbound4 = new InboundLogReader(logsDirectory, "D:\\inbound\\test3-25-inbound2.txt");
		logReaderTest3Inbound4.writeResults();
	}

}
