package cn.edu.nju.moon.redos.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DefaultCaret;

import org.json.JSONArray;
import org.json.JSONObject;

public class GUI extends JFrame{
	private static final long serialVersionUID = -9033026569950747184L;

	private JMenuBar menuBar = new JMenuBar();
	
	private JMenu files = new JMenu("File");
	private JMenuItem loadRegexLocalItem = new JMenuItem("Load regex file from local");
	private JMenuItem loadRegexGitHubItem = new JMenuItem("Load regex file from GitHub");
	private JMenuItem loadReScueItem = new JMenuItem("Load rescue ...");
	
	private JMenu runs = new JMenu("Run");
	private JMenuItem attackAndCollectItem = new JMenuItem("Attack and Collect");
	private JMenuItem reportItem = new JMenuItem("Report");
	
	private JMenu about = new JMenu("About");
	private JMenuItem licenseItem = new JMenuItem("License");
	private JMenuItem copyrightItem = new JMenuItem("Copyright");
	
	private String[] regCols = {"Regex", "File", "Lineno", "Status", "Attack String"};
	DefaultTableModel model = new DefaultTableModel(10, regCols.length);
	private JTable regTable = new JTable(model);
	private JScrollPane regSPane = new JScrollPane(regTable);
	
	private JPanel right = new JPanel();
	private JPanel rightUp = new JPanel();
	private JTextField atkNameField = new JTextField("ReScue.jar");
	private JTextField projNameField = new JTextField("");
	private JTextArea consoler = new JTextArea("");
	private JScrollPane scrollConsoler = new JScrollPane(consoler);
	private JButton genReport = new JButton("Generate Report");
	
	private String projName;
	private String atkName = "ReScue.jar";
	private String txtName;
	private String logDir;
	private String dir = "./test/";
	
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
		
		Pattern p;
		Matcher m;
	
		return result;
	}
	
	public GUI() {
		this.setSize(1200, 600);
		this.setTitle("ReScue");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new GridLayout(1,2));
		
		// File items in menu bar
		files.add(loadRegexLocalItem);
		files.add(loadRegexGitHubItem);
		files.add(loadReScueItem);
		menuBar.add(files);
		
		// Operation items in menu bar
		runs.add(attackAndCollectItem);
		runs.add(reportItem);
		menuBar.add(runs);
		
		// Information in menu bar
		about.add(licenseItem);
		about.add(copyrightItem);
		menuBar.add(about);
		
		// Need a menu bar
		this.setJMenuBar(menuBar);
		
		// Extracted regexes in left
		model.setColumnIdentifiers(regCols);
		regSPane.setBorder(BorderFactory.createTitledBorder("Extracted Regexes"));
		this.add(regSPane);
		
		// Informations in right up corner
		rightUp.setLayout(new GridLayout(1, 2));
		atkNameField.setBorder(BorderFactory.createTitledBorder("Attacker"));
		atkNameField.setEditable(false);
		rightUp.add(atkNameField);
		projNameField.setBorder(BorderFactory.createTitledBorder("Project"));
		projNameField.setEditable(false);
		rightUp.add(projNameField);
		right.setLayout(new BorderLayout());
		rightUp.setBorder(BorderFactory.createTitledBorder("Selected Attacker"));
		right.add(rightUp, BorderLayout.NORTH);
		
		// Consoler in right down corner
		consoler.setLineWrap(true);
		scrollConsoler.setBorder(BorderFactory.createTitledBorder("Runtime Output"));
		DefaultCaret caret = (DefaultCaret) consoler.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		right.add(scrollConsoler, BorderLayout.CENTER);
		right.add(genReport, BorderLayout.SOUTH);
		
		// The whole right
		this.add(right);
		
		licenseItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(
					null, 
					"https://github.com/2bdenny/ReScue/blob/master/LICENSE", "ReScue License", 
					JOptionPane.INFORMATION_MESSAGE
				);
			}
		});
		copyrightItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(
					null, 
					"© 2018 INSTITUTE OF COMPUTER SOFTWARE, NANJING UNIVERSITY.", "ReScue Copyright", 
					JOptionPane.INFORMATION_MESSAGE
				);
			}
		});
		loadRegexLocalItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser("./test/PUTs");
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int retVal = jfc.showDialog(null, "Load");
				if (retVal == JFileChooser.APPROVE_OPTION) {
					File f = jfc.getSelectedFile();
					guiMsg("Input local dir:" + f.getPath());
					projNameField.setText(f.getName());
					
					String[] cmd = {"python3", "guitester.py", "-local", "-projDir", f.getPath()};
					String result = executeCmd(cmd, dir);
					txtName = getTxtNameFromLog(result);
					guiMsg("Load project successfully");
					
					loadRegex(txtName);
				}
			}
		});
	
		loadRegexGitHubItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String repo = JOptionPane.showInputDialog(null, "Paste the GitHub clone url (SSH or HTTPS)", "Input GitHub Repo", JOptionPane.DEFAULT_OPTION);
				guiMsg(repo);
				if (repo != null && repo.length() > 0 && (repo.startsWith("https") || repo.startsWith("git@")) && repo.endsWith(".git")) {
					guiMsg("Input url: " + repo);
					projName = getProjectNameFromUrl(repo);
					projNameField.setText(projName);
					
					String[] cmd = {"python3", "guitester.py", "-down", "-url", repo};
					String result = executeCmd(cmd, dir);
//					guiMsg(result);
					txtName = getTxtNameFromLog(result);
					guiMsg("Load project successfully");
					
					loadRegex(txtName);
				} else {
					guiMsg("Illegal repo: repo url must end with '.git'");
				}
			}
		});
		loadReScueItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser("./release");
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int retVal = jfc.showDialog(null, "Load");
				if (retVal == JFileChooser.APPROVE_OPTION) {
					File f = jfc.getSelectedFile();
					guiMsg(f.getAbsolutePath());
					atkNameField.setText(f.getName());
				}
			}
		});
		genReport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 测试报告写入文件
				try {
					String test_report_dir = Paths.get("./test/", logDir, "test_report.txt").toString();
					BufferedWriter bw = new BufferedWriter(new FileWriter(test_report_dir));
					bw.write("No,Regex,File,Lineno,Status,AttackString,\n");
					for (int i = 0; i < model.getRowCount(); i++) {
						bw.write(i+1+",");
						for (int j = 0; j < 5; j++) {
							bw.write(model.getValueAt(i, j)+",");
						}
						bw.write("\n");
					}
					bw.close();
					guiMsg("Test report is genereted in: " + test_report_dir);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		attackAndCollectItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (txtName == null || txtName.length() == 0) {
					guiMsg("Error: Illegal txtName");
				} else if (atkName == null || atkName.length() == 0) {
					guiMsg("Error: Illegal atkName");
				} else {
					String total_cmd = "python3 batchtester.py -a -reg " + txtName + ".txt -atk " + atkName;
					guiMsg(total_cmd);
					String[] cmd = total_cmd.split(" ");
					String result = executeCmd(cmd, dir);
					guiMsg(result + "\n");
					logDir = getLogDirFromLog(result);
//					logDir = "./test/logs/ReScue.jar/local_meteor-devel.txt/1553748560530";
					
					String total_collect_cmd = "python3 batchtester.py -c -reg " + txtName + ".txt -logDir " + logDir;
					guiMsg(total_collect_cmd);
					String[] collectCmd = total_collect_cmd.split(" ");
					result = executeCmd(collectCmd, dir);
					guiMsg(result + "\n");
					loadCollectSummary(logDir);
				}
			}
		});
		
		regTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			private static final long serialVersionUID = 2959137775367628479L;

			@Override
		    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		    {
		        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		        if (column == 3) {
		        	Object val = table.getValueAt(row, column);
		        	if (val != null) {
				        String res = val.toString();
				        if (res.equalsIgnoreCase("safe")) {
				        	c.setBackground(Color.GREEN);
				        } else if (res.equalsIgnoreCase("dangerous")) {
				        	c.setBackground(Color.RED);
				        } else if (res.equalsIgnoreCase("TODO")) {
				        	c.setBackground(Color.WHITE);
				        }
		        	}
		        }
		        return c;
		    }
		});
	}
	
	private void loadCollectSummary(String logDir) {
		try {
			List<String> sums = Files.readAllLines(Paths.get("./test/", logDir, "collect_summary.txt"));
			Pattern p = Pattern.compile("^(\\d+) : (.+?) : .*$");
			for (String sum : sums) {
				Matcher m = p.matcher(sum);
				if (m.find()) {
					int rno = Integer.parseInt(m.group(1));
					String res = m.group(2);
					if (res.equalsIgnoreCase("success")) {
						model.setValueAt("Dangerous", rno - 1, 3);
						Path atk_file = Paths.get(logDir, rno + "_atk.txt");
//						Path atk_file = Paths.get("./test/", logDir, rno + "_atk.txt");
						String atk_str = new String(Files.readAllBytes(atk_file), "UTF-8");
						model.setValueAt(atk_str, rno - 1, 4);
					} else {
						model.setValueAt("Safe", rno - 1, 3);
					}
					regTable.repaint();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getLogDirFromLog(String result) {
		String[] lines = result.split("\n");
		Pattern p = Pattern.compile("^Current log dir: (.*)$");
		for (String line : lines) {
			if (line.startsWith("Current log dir: ")) {
				Matcher m = p.matcher(line);
				if (m.find()) return m.group(1);
			}
		}
		this.guiMsg("Error: Illegal log format");
		return "";
	}
	
	private void loadRegex(String txtName) {
		try {
			Path jsonRegs = Paths.get(dir, "data", txtName + ".json");
			String content = new String(Files.readAllBytes(jsonRegs), "UTF-8");
			JSONArray regs = new JSONArray(content);
			model.setRowCount(regs.length());
			for (int i = 0; i < regs.length(); i++) {
				JSONObject jo = regs.getJSONObject(i);
				model.setValueAt(jo.getString("reg"), i, 0);
				model.setValueAt(jo.getString("file"), i, 1);
				model.setValueAt(jo.getString("lineno"), i, 2);
				model.setValueAt("TODO", i, 3);
				model.setValueAt("", i, 4);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getProjectNameFromUrl(String url) {
		Pattern pn = Pattern.compile("^.*/(.*)\\.git");
		Matcher mn = pn.matcher(url);
		if (mn.find()) return mn.group(1);
		this.guiMsg("Get project name from url: " + url + " failed");
		return "";
	}
	
	private String getTxtNameFromLog(String log) {
		String[] lines = log.split("\n");
		String lastLine = lines[lines.length - 1];
		
		Pattern p = Pattern.compile("^txtName: (.*)$");
		Matcher m = p.matcher(lastLine);
		if (m.find()) return m.group(1);
		this.guiMsg("Get txtName from Log failed\n Log is: " + log);
		return "";
	}
}
