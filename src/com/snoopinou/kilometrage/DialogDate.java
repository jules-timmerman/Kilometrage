package com.snoopinou.kilometrage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class DialogDate extends JDialog{
	

	private JComboBox<Integer> jour = new JComboBox<Integer>();
	private JComboBox<String> mois = new JComboBox<String>();
	private JComboBox<Integer> annee = new JComboBox<Integer>();
	
	private final String[] tabMois = {"JANUARY","FEBRUARY","MARCH","APRIL","MAY","JUNE","JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER"};
	
	public DialogDate(JFrame parent, String title, boolean modal) {
		
		super(parent,title,modal);
		
		this.setSize(300,75);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		initComponent();
	}
	
	public LocalDate showDialog() {
		this.setVisible(true);	
		return LocalDate.of(((Integer)annee.getSelectedItem()), Month.valueOf((String)mois.getSelectedItem()), 1);
	}
	
	private void initComponent() {
		
		JPanel contentPane = new JPanel();	
		
		for(String m : tabMois) {
			mois.addItem(m);
		}
		
		LocalDateTime currentTime = LocalDateTime.now();
		for(int i = currentTime.getYear(); i >= 1970; i--) {
			annee.addItem(i);
		}
		
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);				
			}
			
		});
		
		contentPane.add(mois);
		contentPane.add(annee);
		contentPane.add(button);
		this.setContentPane(contentPane);
	}

}
