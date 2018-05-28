package com.snoopinou.kilometrage;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.BadLocationException;

import net.miginfocom.swing.MigLayout;

public class Fenetre extends JFrame{
	
	
	public Map<String, Integer> mapNom = new HashMap<String, Integer>(); //Noms avec index pour tableau distances
	public int[][] tabDistances; // Distances entre Studios
	private int distancesTot = 0; // Distance du jour
	private int totMois= 0; // Distance total du mois
	private ArrayList<String> listChemin = new ArrayList<String>(); // Liste destinations successives
	private ArrayList<Integer> histoDist = new ArrayList<Integer>(); // Historique pour reculer
	
	private Path noms = Paths.get("resources/Noms.txt");
	private Path distances = Paths.get("resources/Distances.txt");
	

	
	
	private JTabbedPane tabPane = new JTabbedPane();
	private JPanel panSaisie = new JPanel();
	private JTextArea lblChemin = new JTextArea();
	private LocalDate date;
	private JLabel lblDate = new JLabel();
	private JButton buttonArr = new JButton("<--");
	private JLabel lblKilo = new JLabel("0");
	private JPanel panBoutons = new JPanel();
	private JButton buttonCustom = new JButton("Custom");
	private JButton buttonClear = new JButton("Clear");
	private JButton buttonNext = new JButton("Next");
	
	private JPanel panMois = new JPanel();
	private JTextArea recap = new JTextArea();
	
	private JPanel panAnnee = new JPanel();
	private JTextArea recapAnnee = new JTextArea();
	
	Font font = new Font("Arial", Font.PLAIN, 16);
	
	
	public Fenetre() {
		
		DialogDate dd = new DialogDate(null, "Date", true);
		date = dd.showDialog(); 
		
	
		initTab();
		initComp();
		
		this.setTitle("Kilometrage");
		this.setSize(400, 400);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		this.setVisible(true);
	}
	
	private void initComp() {
		
		tabPane.addTab("Saisie", panSaisie);
		panSaisie.setLayout(new MigLayout("","[][][][][]","[][][][]")); // COLUMN INTO ROWS
		
		lblChemin.setEditable(false);
		lblChemin.setLineWrap(true);
		lblChemin.setWrapStyleWord(true);
		lblChemin.setFont(font);
		panSaisie.add(lblChemin,"growx, spanx, wrap, wmin 10");
		
		
		buttonArr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				totMois -= histoDist.get(histoDist.size()-1);
				histoDist.remove(histoDist.size()-1);
				date = date.minusDays(1);
				buttonClear.doClick();
				lblDate.setText(date.getDayOfMonth()+" "+date.getMonth()+" "+date.getYear());
				try {
					recap.setText(recap.getText().substring(0, recap.getLineStartOffset(recap.getLineCount()-2)));
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				System.out.println(totMois);
			}
		});
		lblDate.setText(date.getDayOfMonth()+" "+date.getMonth()+" "+date.getYear());
		lblDate.setFont(font.deriveFont(10));
		buttonArr.setFont(font.deriveFont(10));
		lblKilo.setFont(font.deriveFont(10));
		panSaisie.add(lblDate, "center");
		panSaisie.add(buttonArr, "right");
		panSaisie.add(lblKilo, "right, wrap");
		
		
		panBoutons.setLayout(new GridLayout(0,2));
		for(int i = 0; i < mapNom.size();i++) {
			JButton b = new JButton((String)mapNom.keySet().toArray()[i]);
			b.setFont(font);
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					lblChemin.setText(lblChemin.getText()+b.getText()+" -> ");
					listChemin.add(b.getText());
					actKilo();
				}				
			});
			panBoutons.add(b);
		}
		buttonCustom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DialogCustom dg = new DialogCustom(null, "Custom",true);
				int dist = dg.showDialog();
				lblChemin.setText(lblChemin.getText()+dist+" -> ");
				listChemin.add("!"+dist+"!");
				actKilo();				
			}
		});
		buttonCustom.setFont(font);
		panBoutons.add(buttonCustom);
		
		buttonClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				listChemin.clear();
				distancesTot = 0;
				lblKilo.setText("0");
				lblChemin.setText("");
			}
		});
		
		panSaisie.add(panBoutons, "spanx 2, spany 2");
		buttonClear.setFont(font);
		panSaisie.add(buttonClear,"wrap, top");
		
		buttonNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				LocalDate dateAtm = date;
				recap.append(date.getDayOfMonth()+" "+date.getMonth()+" "+date.getYear()+" : "+distancesTot+"\n");
				totMois += distancesTot;
				histoDist.add(distancesTot);
				date = date.plusDays(1);
				lblDate.setText(date.getDayOfMonth()+" "+date.getMonth()+" "+date.getYear());
				
				if(date.getMonth() != dateAtm.getMonth()) { // Si changement de mois
					JOptionPane.showMessageDialog(null, "Total du mois de "+dateAtm.getMonth()+" : "+totMois, "Fin du mois", JOptionPane.INFORMATION_MESSAGE);
					recapAnnee.append(dateAtm.getMonth()+" : "+String.valueOf(totMois));	
					recap.setText("");
					
					
					if(Files.notExists(Paths.get(dateAtm.getYear()+".txt"))) {
						try {
						Files.createFile(Paths.get(dateAtm.getYear()+".txt"));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					
					Path save = Paths.get(dateAtm.getYear()+".txt");
					String toWrite = "";
					try(BufferedReader br = Files.newBufferedReader(save, StandardCharsets.UTF_8)){
						while(br.ready()) {
							toWrite += br.readLine(); // On reprends contenu Fichier
							toWrite += "\n";
						}
					
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					try(BufferedWriter bw = Files.newBufferedWriter(save, StandardCharsets.UTF_8)){
						bw.write(toWrite+dateAtm.getMonth()+" : "+totMois); // On reecrit en ajoutant le nouveau
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					totMois = 0;
					histoDist.clear();
				}
				
				buttonClear.doClick(); // Reinitialise
				
				
			}
		});
		buttonNext.setFont(font);
		panSaisie.add(buttonNext,"bottom");
		
		
		tabPane.addTab("Mois", panMois);
		
		recap.setEditable(false);
		recap.setFont(font);
		recap.setLineWrap(true);
		recap.setWrapStyleWord(true);
		panMois.setLayout(new BorderLayout());
		panMois.add(new JScrollPane(recap), BorderLayout.CENTER);
		
		
		tabPane.addTab("Annee", panAnnee);
		
		recapAnnee.setEditable(false);
		recapAnnee.setFont(font);
		recapAnnee.setLineWrap(true);
		recapAnnee.setWrapStyleWord(true);
		panAnnee.setLayout(new BorderLayout());
		panAnnee.add(new JScrollPane(recapAnnee), BorderLayout.CENTER);
		
		this.setContentPane(tabPane);
	}
	
	private void initTab() {
		
		try(BufferedReader br = Files.newBufferedReader(noms)){
			
			int i = 0;
			while(br.ready()) {
				mapNom.put(br.readLine(), i); // Noms + Index (1 nom par ligne)
				i++;
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		try(BufferedReader br = Files.newBufferedReader(distances)){
		
			tabDistances = new int[mapNom.size()][mapNom.size()]; // taille nombre studio different
			
			int i = 0;
			
			while(br.ready()) {
				int j = 0;
				int pos = 0;
				
				String ligne = br.readLine(); // Ligne de tableau 
				
				while(pos+1 != ligne.length()) {
					int from = ligne.indexOf("|", pos)+1; // Debut substring = caractere inclu donc +1 place pour pas avoir la barre
					int to = ligne.indexOf("|", pos+1); // Fin substring = non inclu ajoute 1 pour pas avoir meme caractere
					
					String str = ligne.substring(from,to);
					tabDistances[i][j] = Integer.parseInt(str);
					pos = to;
					j++;
				}
			i++;	
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	private void actKilo() { // Actualise Kilometrage actuelle 
		distancesTot = 0;
		if(listChemin.size() > 1) {
			for(int i = 1; i < listChemin.size();i++) { // Si plus d'une valeur
				if(listChemin.get(i).substring(0, 1).equals("!")) {
					System.out.println("KAKAKAKKAKA");
					String val = listChemin.get(i).substring(1, listChemin.get(i).indexOf("!", 1));		
					distancesTot += Integer.valueOf(val);
				}else if(listChemin.get(i-1).substring(0, 1).equals("!")){
					System.out.println("KAKA");
					String val = listChemin.get(i).substring(1, listChemin.get(i-1).indexOf("!", 1));
					distancesTot += Integer.valueOf(val);
				}else {
					
					System.out.println("KAKAKAKKAKAKAKKIAKAAKA");
					int depart = mapNom.get(listChemin.get(i-1));
					int arrive = mapNom.get(listChemin.get(i));
					
					distancesTot += tabDistances[depart][arrive];				
				}
			}
		}else { // Peut pas faire de calcul si 1 valeur
			distancesTot = 0;
		}
		
		lblKilo.setText(String.valueOf(distancesTot));
	}	
	
	
}
