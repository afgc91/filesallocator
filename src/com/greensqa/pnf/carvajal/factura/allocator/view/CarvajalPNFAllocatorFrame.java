package com.greensqa.pnf.carvajal.factura.allocator.view;

import java.awt.Dimension;

import javax.swing.JFrame;

public class CarvajalPNFAllocatorFrame extends JFrame {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = 4216508262497451172L;
	
	/**
	 * Panel principal.
	 */
	private CarvajalPNFAllocatorPanel panel;
	
	public CarvajalPNFAllocatorFrame(String name, CarvajalPNFAllocatorPanel panel) {
		super(name);
		this.setPanel(panel);
		this.add(panel);
		this.setResizable(false);
		this.setSize(new Dimension(500, 380));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public CarvajalPNFAllocatorPanel getPanel() {
		return panel;
	}

	public void setPanel(CarvajalPNFAllocatorPanel panel) {
		this.panel = panel;
	}

}
