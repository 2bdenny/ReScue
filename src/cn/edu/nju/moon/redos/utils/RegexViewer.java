package cn.edu.nju.moon.redos.utils;

import javax.swing.JFrame;

import cn.edu.nju.moon.redos.regex.ReScuePattern;
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
import prefuse.visual.VisualItem;

/**
 * The regex structure viewer, its GUI is based on prefuse.jar
 */
public class RegexViewer {
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
    	Graph g = new Graph(nodes, edges, directed, from, to);
    	
    	Visualization vis = new Visualization();
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
        display.setSize(800, 600);
        display.pan(250, 250);        display.addControlListener(new DragControl());
        display.addControlListener(new PanControl());
        display.addControlListener(new ZoomControl());
        display.addControlListener(new WheelZoomControl());
        display.addControlListener(new FocusControl(1));
        display.addControlListener(new ZoomToFitControl());
        
        // Launching the visualization        
        JFrame jf = new JFrame();
        jf.setTitle(pattern.pattern());
        jf.setSize(800, 600);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.add(display);
        vis.run("color");
        vis.run("layout");
        jf.setVisible(true);
    }
}
