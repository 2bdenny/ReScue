package cn.edu.nju.moon.redos.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GUI extends JFrame{
	private static final long serialVersionUID = -9033026569950747184L;

	private JMenuBar menuBar = new JMenuBar();
	
	private JMenu files = new JMenu("File");
	private JMenuItem loadRegexLocal = new JMenuItem("Load regex file from local");
	private JMenuItem loadRegexGitHub = new JMenuItem("Load regex file from GitHub");
	private JMenuItem loadReScue = new JMenuItem("Load rescue ...");
	
	private JMenu runs = new JMenu("Run");
	private JMenuItem attack = new JMenuItem("Attack");
	private JMenuItem genReport = new JMenuItem("Generate report");
	
	private JMenu about = new JMenu("About");
	private JMenuItem license = new JMenuItem("License");
	private JMenuItem copyright = new JMenuItem("Copyright");
	
	private String[] regCols = {"Regex", "File", "Lineno"};
	DefaultTableModel model = new DefaultTableModel(10, regCols.length);
	private JTable regTable = new JTable(model);
	private JScrollPane regSPane = new JScrollPane(regTable);
	
	private JPanel right = new JPanel();
	private JLabel attacker = new JLabel("Attacker");
	private JTextArea consoler = new JTextArea("");
	
	public static void main(String[] args) {
		GUI gui = new GUI();
		gui.setVisible(true);
	}
	
	public GUI() {
		this.setSize(800, 600);
		this.setTitle("ReScue");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new GridLayout(1,2));
		
		files.add(loadRegexLocal);
		files.add(loadRegexGitHub);
		files.add(loadReScue);
		menuBar.add(files);
		
		runs.add(attack);
		runs.add(genReport);
		menuBar.add(runs);
		
		about.add(license);
		about.add(copyright);
		menuBar.add(about);
		
		this.setJMenuBar(menuBar);
		model.setColumnIdentifiers(regCols);
		regSPane.setBorder(BorderFactory.createTitledBorder("Extracted Regexes"));
		this.add(regSPane);
		
		right.setLayout(new BorderLayout());
		attacker.setBorder(BorderFactory.createTitledBorder("Selected Attacker"));
		right.add(attacker, BorderLayout.NORTH);
		consoler.setBorder(BorderFactory.createTitledBorder("Runtime Output"));
		right.add(consoler, BorderLayout.CENTER);
		this.add(right);
		
		license.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "https://github.com/2bdenny/ReScue/blob/master/LICENSE", "ReScue License", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		copyright.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "© 2018 INSTITUTE OF COMPUTER SOFTWARE, NANJING UNIVERSITY.", "ReScue Copyright", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		loadRegexLocal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser("./test/PUTs");
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int retVal = jfc.showDialog(null, "Load");
				if (retVal == JFileChooser.APPROVE_OPTION) {
					File f = jfc.getSelectedFile();
					System.out.println(f.getPath());
				}
			}
		});
		loadRegexGitHub.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String repo = JOptionPane.showInputDialog(null, "Paste the GitHub clone url (SSH or HTTPS)", "Input GitHub Repo", JOptionPane.DEFAULT_OPTION);
				System.out.println(repo);
			}
		});
		loadReScue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser("./release");
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int retVal = jfc.showDialog(null, "Load");
				if (retVal == JFileChooser.APPROVE_OPTION) {
					File f = jfc.getSelectedFile();
					attacker.setText(f.getName());
				}
			}
		});
	}
}
