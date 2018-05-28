package com.snoopinou.kilometrage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class DialogCustom extends JDialog {
	
	
	private NumberFormat format = NumberFormat.getIntegerInstance();
	private JFormattedTextField jtf = new JFormattedTextField(format);
	private JButton button = new JButton("OK");
	
	public DialogCustom(JFrame parent, String title, boolean modal) {
		super(parent,title,modal);
		this.setSize(300,150);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		initComponent();	
	}
	
	public int showDialog() {
		this.setVisible(true);
		return Integer.parseInt(Long.toString((long) jtf.getValue()));
		
	}
	
	private void initComponent() {
		
		JPanel contentPane = new JPanel();
		
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
			
		});
		jtf.setValue(0);
		
		contentPane.setLayout(new MigLayout());
		
		contentPane.add(new JLabel("Distance : "),"wrap, center");
		contentPane.add(jtf, "wrap, growx, pushx");
		contentPane.add(button,"wrap, growx, pushx");
		
		this.setContentPane(contentPane);
		
	}
	
}
