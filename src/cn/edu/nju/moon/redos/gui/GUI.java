package cn.edu.nju.moon.redos.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

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
	
	private String[] regCols = {"Regex", "File", "Lineno", "Status", "Attack String"};
	DefaultTableModel model = new DefaultTableModel(10, regCols.length);
	private JTable regTable = new JTable(model);
	private JScrollPane regSPane = new JScrollPane(regTable);
	
	private JPanel right = new JPanel();
	private JPanel rightUp = new JPanel();
	private JTextField atkName = new JTextField("");
	private JTextField projName = new JTextField("");
	private JTextArea consoler = new JTextArea("");
	private JScrollPane scrollConsoler = new JScrollPane(consoler);
	
	public static void main(String[] args) {
		GUI gui = new GUI();
		gui.setVisible(true);
	}
	
	private void guiMsg(String msg) {
		consoler.append(msg + "\n");
//		consoler.repaint();
	}
	
	private String executeCmd(String[] cmd, String dir) {
		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.directory(new File(dir));
		String result = "";
		try {
			Process p = pb.start();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

	        StringJoiner sj = new StringJoiner(System.getProperty("line.separator"));
	        reader.lines().iterator().forEachRemaining(sj::add);
	        result = sj.toString();
	        p.waitFor();
	        p.destroy();
		} catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
		}
		
		return result;
	}
	
	public GUI() {
		this.setSize(1200, 600);
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
		
		rightUp.setLayout(new GridLayout(1, 2));
		atkName.setBorder(BorderFactory.createTitledBorder("Attacker"));
		atkName.setEditable(false);
		rightUp.add(atkName);
		projName.setBorder(BorderFactory.createTitledBorder("Project"));
		projName.setEditable(false);
		rightUp.add(projName);
		right.setLayout(new BorderLayout());
		rightUp.setBorder(BorderFactory.createTitledBorder("Selected Attacker"));
		right.add(rightUp, BorderLayout.NORTH);
		consoler.setLineWrap(true);
		scrollConsoler.setBorder(BorderFactory.createTitledBorder("Runtime Output"));
		DefaultCaret caret = (DefaultCaret) consoler.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		right.add(scrollConsoler, BorderLayout.CENTER);
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
					guiMsg(f.getPath());
				}
			}
		});
	
		loadRegexGitHub.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String repo = JOptionPane.showInputDialog(null, "Paste the GitHub clone url (SSH or HTTPS)", "Input GitHub Repo", JOptionPane.DEFAULT_OPTION);
				if (repo != null && repo.length() > 0 && (repo.startsWith("https") || repo.startsWith("git@")) && repo.endsWith(".git")) {
					guiMsg("Input url: " + repo);
					String project_name = getProjectNameFromUrl(repo);
					projName.setText(project_name);
					
					String[] cmd = {"python3", "guitester.py", "-down", "-url", repo};
					String dir = "./test/";
					String result = executeCmd(cmd, dir);
					String txtName = getTxtNameFromLog(result);
					guiMsg("Load project successfully");
				}
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
					guiMsg(f.getAbsolutePath());
					atkName.setText(f.getName());
				}
			}
		});
	}
	
	private String getProjectNameFromUrl(String url) {
		Pattern pn = Pattern.compile("^.*/(.*)\\.git");
		Matcher mn = pn.matcher(url);
		if (mn.find()) return mn.group(1);
		guiMsg("Get project name from url: " + url + " failed");
		return "";
	}
	
	private String getTxtNameFromLog(String log) {
		String[] lines = log.split("\n");
		String lastLine = lines[lines.length - 1];
		
		Pattern p = Pattern.compile("^txtName: (.*)$");
		Matcher m = p.matcher(lastLine);
		if (m.find()) return m.group(1);
		guiMsg("Get txtName from Log failed\n Log is: " + log);
		return "";
	}
}
