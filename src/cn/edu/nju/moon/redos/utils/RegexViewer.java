package cn.edu.nju.moon.redos.utils;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.edu.nju.moon.redos.Trace;
import cn.edu.nju.moon.redos.regex.ReScueMatcher;
import cn.edu.nju.moon.redos.regex.ReScuePattern;
import cn.edu.nju.moon.redos.regex.ReScuePattern.Node;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

class AutoMatchPlayer extends Thread{
	JButton abtn;
	public AutoMatchPlayer(JButton btn) {
		this.abtn = btn;
	}
	
	@Override
    public void run() {
		while (abtn.isEnabled()) {
			abtn.doClick();
			
			try {
				Thread.currentThread().sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
}

/**
 * The regex structure viewer, its GUI is based on prefuse.jar
 */
public class RegexViewer {
	static Graph g;
	static Visualization vis;
	static int curStep = 0;
    /**
     * Assistant method, print the data in the table
     * @param t
     */
    public static void printTable(Table t) {
    	for (int j = 0; j < t.getColumnCount(); j++) {
    		System.out.print(t.getSchema().getColumnName(j) + "(" + t.getSchema().getColumnType(j) + ")\t");
		}
    	System.out.println();
    	
    	for (int i = 0; i < t.getRowCount(); i++) {
    		for (int j = 0; j < t.getColumnCount(); j++) {
    			System.out.print(t.get(i, j) + "\t");
    		}
    		System.out.println();
    	}
    }
    
    /**
     * Draw the regex on the JFrame
     * @param pattern
     * @param nodes
     * @param edges
     * @param directed
     * @param from
     * @param to
     * @param nodeName
     */
    public static void paintRegex(ReScuePattern pattern, Table nodes, Table edges, boolean directed, String from, String to, String nodeName) {
    	g = new Graph(nodes, edges, directed, from, to);
    	
    	vis = new Visualization();
    	vis.add("graph", g);

        LabelRenderer label = new LabelRenderer(nodeName);
        label.setRoundedCorner(10, 10);
        vis.setRendererFactory(new DefaultRendererFactory(label));
        ColorAction node_fill = new ColorAction("graph.nodes", VisualItem.FILLCOLOR, ColorLib.rgb(200, 200, 200));
        ColorAction node_text = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0));
        ColorAction node_other = new ColorAction("graph.nodes", VisualItem.STROKECOLOR, 0);
        ColorAction edge_text = new ColorAction("graph.edges", VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0));
        ColorAction edge_fill = new ColorAction("graph.edges", VisualItem.FILLCOLOR, ColorLib.rgb(20, 100, 100));
        ColorAction edge_other = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.rgb(20, 100, 100));
        
        ActionList color = new ActionList();
        color.add(node_fill);
        color.add(node_text);
        color.add(node_other);
        color.add(edge_text);
        color.add(edge_fill);
        color.add(edge_other);
        
        ActionList layout = new ActionList(Activity.INFINITY);
        layout.add(new ForceDirectedLayout("graph"));
        layout.add(new RepaintAction());
        
        vis.putAction("color", color);
        vis.putAction("layout", layout);
        
        Display display = new Display(vis);
        display.setSize(1200, 950);
        display.pan(250, 250);
        display.addControlListener(new DragControl());
        display.addControlListener(new PanControl());
        display.addControlListener(new ZoomControl());
        display.addControlListener(new WheelZoomControl());
        display.addControlListener(new FocusControl(1));
        display.addControlListener(new ZoomToFitControl());
        
        JFrame jf = new JFrame();
        JPanel inputPanel = new JPanel();
        JTextField input = new JTextField(30);
        JButton btn = new JButton("Start");
        JButton btnn = new JButton("Next");
        JButton abtn = new JButton("AutoPlay");
        JLabel stepsName = new JLabel("Steps: ");
        JLabel stepsNum = new JLabel("0");
        btn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String inputStr = input.getText();
				ReScueMatcher m = pattern.matcher(inputStr, new Trace(1e4, true));
				Trace t = m.find();
				curStep = 0;
				btnn.setEnabled(true);
				stepsNum.setText(t.getMatchSteps() + "");
				paintLog(pattern, t.getLogNode(), t.getLogIdx(), ColorLib.rgb(200, 150, 150));
		}});
        btnn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String inputStr = input.getText();
				ReScueMatcher m = pattern.matcher(inputStr, new Trace(1e4, true));
				Trace t = m.find();
				paintLog(pattern, t.getLogNode(), t.getLogIdx());
				curStep++;
				stepsNum.setText("" + (t.getMatchSteps() - curStep));
				if (curStep == t.getMatchSteps()) {
					btnn.setEnabled(false);
				}
		}});
        abtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread tmp = new AutoMatchPlayer(btnn);
				tmp.start();
			}
        });
        
        inputPanel.add(stepsName);
        inputPanel.add(stepsNum);
        inputPanel.add(input);
        inputPanel.add(btn);
        inputPanel.add(btnn);
        inputPanel.add(abtn);
        
        JPanel drawing = new JPanel();
        drawing.add(display);
        
        // Launching the visualization        
        jf.setLayout(new BorderLayout());
        jf.setTitle(pattern.pattern());
        jf.setSize(1400, 1000);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        jf.add(inputPanel, BorderLayout.NORTH);
        jf.add(drawing, BorderLayout.CENTER);
        
        vis.run("color");
        vis.run("layout");
        jf.setVisible(true);
    }
    
    public static void paintLog(ReScuePattern p, List<Node> logNode, List<Integer> logIdx, int color) {
    	// Iterate over VisualItems in Visualization vis
		Iterator<NodeItem> v_it = vis.items("graph.nodes");
    	while(v_it.hasNext()) {
    	    NodeItem item = v_it.next();
    	    String text = (String) item.get("Node");
    	    int idx = Integer.parseInt(text.split(": ")[0]);
    	    if (p.nodeMap.get(idx) == logNode.get(curStep)) item.setFillColor(color);
    	    else item.setFillColor(ColorLib.rgb(200, 200, 200));
    	}
    	vis.repaint();
    }
    
    public static void paintLog(ReScuePattern p, List<Node> logNode, List<Integer> logIdx) {
    	paintLog(p, logNode, logIdx, ColorLib.rgb(150, 150, 200));
    }
}
