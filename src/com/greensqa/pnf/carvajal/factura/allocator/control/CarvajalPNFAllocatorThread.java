package com.greensqa.pnf.carvajal.factura.allocator.control;

import com.greensqa.pnf.carvajal.factura.allocator.model.FilesAllocator;
import com.greensqa.pnf.carvajal.factura.allocator.view.CarvajalPNFAllocatorPanel;

public class CarvajalPNFAllocatorThread extends Thread {

	private FilesAllocator fa;
	private CarvajalPNFAllocatorPanel panel;
	
	public CarvajalPNFAllocatorThread(FilesAllocator fa, CarvajalPNFAllocatorPanel panel) {
		this.fa = fa;
		this.panel = panel;
	}
	
	public void run() {
		while(! fa.getCurrentStageMessage().equals(FilesAllocator.FINISHED_MSG)) {
			panel.getTasksArea().setText(fa.getCurrentStageMessage() + "\n" + fa.getCurrentTaskMessage());
		}
	}
}
