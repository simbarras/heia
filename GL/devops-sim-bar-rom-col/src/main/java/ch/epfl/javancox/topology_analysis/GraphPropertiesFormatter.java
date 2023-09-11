package ch.epfl.javancox.topology_analysis;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import ch.epfl.general_libraries.charts.HistogramProvider;
import ch.epfl.general_libraries.clazzes.ClassLister;
import ch.epfl.general_libraries.math.Rounder;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.io.JavancoFile;



public class GraphPropertiesFormatter {
	
	public final static String GRAPH_FORMATTER_WEB_PAGE = "web/generators_online/properties.html";
	public final static String GRAPH_FORMATTER_PROPERTIES_FILE = "div/graphProperties.properties";	
	
	private String voidPage;

	private TreeMap<String, MonoComputerProxy> monoProxies = new TreeMap<String, MonoComputerProxy>();
	private TreeMap<String, MultiComputerProxy> multiProxies = new TreeMap<String, MultiComputerProxy>();	
	
	public static void main(String[] args) {
		if (args.length <= 0) {
			System.out.println("Args are required : prefixes");
			System.exit(0);
		}
		Javanco.initJavancoUnsafe();
		List<String> prefixes = (List<String>)java.util.Arrays.asList(args);
		ClassLister<AbstractTopologyAnalyser> cl = new ClassLister<AbstractTopologyAnalyser>(prefixes, AbstractTopologyAnalyser.class);
		try {
			FileWriter fw = new FileWriter(GRAPH_FORMATTER_PROPERTIES_FILE);
			ArrayList<String> genList = new ArrayList<String>();
			for (Class<AbstractTopologyAnalyser> c : cl.getSortedClasses()) {
				genList.add(c.getName() + "\r\n");
			}			
			Collections.sort(genList);
			for (String s : genList) {
				fw.append(s);
			}
			fw.close();			
			
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}	
		
	public GraphPropertiesFormatter() {
		this(true);
	}
	
	public GraphPropertiesFormatter(boolean web) {
		try {
			if (web) {
				File f = JavancoFile.findFile(GRAPH_FORMATTER_WEB_PAGE);
				System.out.println(f);
				FileReader fr = new FileReader(f);
				StringWriter sw = new StringWriter();
				
			    char[] buf=new char[1024];
			    int count=-1;		
				while(((count=fr.read(buf))>0)) {
					sw.write(buf,0,count);
				}
				voidPage = sw.toString();
				fr.close();
			}
			try {
				ClassLister<AbstractTopologyAnalyser> cl = new ClassLister<AbstractTopologyAnalyser>(
						Javanco.getProperty(Javanco.JAVANCO_DEFAULT_CLASSPATH_PREFIXES_PROPERTY).split(";"), AbstractTopologyAnalyser.class);
				for (Class<AbstractTopologyAnalyser> c : cl.getSortedClasses()) {
					registerComputer(c);
				}
			}
			catch (Error e) {
				URL url = JavancoFile.findRessource(GRAPH_FORMATTER_PROPERTIES_FILE);
				BufferedReader read = new BufferedReader(new InputStreamReader(url.openStream()));
				String line;
				while ((line = read.readLine()) != null) {
					Class<?> c = Class.forName(line);
					if (AbstractTopologyAnalyser.class.isAssignableFrom(c)) {
						registerComputer(c);
					}
				}
			}
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}							
	}
	
	public void registerComputer(Class<?> c) throws Exception {
		try {
			if (MonoMetricComputer.class.isAssignableFrom(c)) {
				MonoComputerProxy proxy = new MonoComputerProxy();
				proxy.comp = (MonoMetricComputer)c.getConstructor(new Class[]{}).newInstance(new Object[]{});
				proxy.computerName = proxy.comp.getResultName();
				monoProxies.put(proxy.computerName, proxy);	
			}
			if (MultiMetricComputer.class.isAssignableFrom(c)) {
				MultiComputerProxy proxy = new MultiComputerProxy();
				proxy.comp = (MultiMetricComputer)c.getConstructor(new Class[]{}).newInstance(new Object[]{});
				proxy.computerName = proxy.comp.getResultName();
				multiProxies.put(proxy.computerName, proxy);					
			}
		}
		catch (Exception e) {
			System.out.println("Failed to register class " + c.getSimpleName());
		}
	}
	
	public JPanel getPanel(AbstractGraphHandler agh, final Frame f) {
		JPanel pane = new JPanel();
		pane.setLayout(new GridLayout(2,1));
		JPanel mono = new JPanel();
		mono.setLayout(new GridLayout(3+monoProxies.size(),2));
		mono.add(new JLabel("Metric"));
		mono.add(new JLabel("Value"));
		mono.add(new JLabel("Number of nodes"));
		mono.add(new JLabel(agh.getNodeContainers().size()+""));
		mono.add(new JLabel("Number of links"));
		mono.add(new JLabel(agh.getLinkContainers().size()+""));
		for (MonoComputerProxy proxy : monoProxies.values()) {
			proxy.comp.setAGH(agh);
			proxy.getLabels(mono);
		}
		
		pane.add(mono);
		
		JPanel border = new JPanel();
		//border.setLayout(new GridLayout(multiProxies.size(), 1));
		
		JButton[] buts = new JButton[multiProxies.size()];
		Object[][] obj = new Object[multiProxies.size()][7];
		Object[] titles = new String[]{"Metric","Mean","Min","Max","Med","Var","Most central"};	
		int index = 0;
		for (MultiComputerProxy proxy : multiProxies.values()) {
			final MultiComputerProxy p1 = proxy;
			try {
				proxy.comp.setAGH(agh);
				proxy.getLabels(obj[index]);
			}
			catch (Throwable e) {}
			buts[index] = new JButton("Show histogram");
			buts[index].setSize(120, 16);
			buts[index].setPreferredSize(new Dimension(120, 16));
			buts[index].setBorder(BorderFactory.createEmptyBorder());
			border.add(buts[index]);
			buts[index].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JFreeChart jFreeChart = HistogramProvider.getHistogram(p1.getValues(), "Histogram");
					ChartPanel chartPanel = new ChartPanel(null, 150, 150, 150, 150, 1600, 1400,
							false, true, // properties
							true, // save
							true, // print
							true, // zoom
							false); // tooltips
					chartPanel.setChart(jFreeChart);
					JDialog f2 = new JDialog(f, true);
					f2.setSize(400, 400);
					f2.setContentPane(chartPanel);
					f2.setVisible(true);
				}
			});
			index++;
		}
		
		final MyTable tab = new MyTable(obj, titles);
		
		ArrayList<MouseListener> list = new ArrayList<MouseListener>();
		for (MouseListener m : tab.getMouseListeners()) {
			list.add(m);
		}
		for (MouseListener m : list) {
			tab.removeMouseListener(m);
		}
		
		border.setSize(new Dimension(100, multiProxies.size() * 16));
		border.setPreferredSize(new Dimension(100, multiProxies.size() * 16));
		
		
		JPanel pp = new JPanel();
		pp.setLayout(new BorderLayout());
		pp.add(tab.getTableHeader(), BorderLayout.PAGE_START);
		pp.add(tab, BorderLayout.CENTER);
		pp.add(border, BorderLayout.EAST);
		
		pane.add(pp);		
		return pane;
	}
	
	private static class MyTable extends JTable {
		
	//	HashSet<JButton> set = new HashSet<JButton>();
		
		
		MyTable(Object[][] o, Object[] til) {
			super(o,til);
		}
		
		private static final long serialVersionUID = 1L;

		public TableCellRenderer getCellRenderer(int i, int j) {
			return new TableCellRenderer() {					
				@Override
				public Component getTableCellRendererComponent(JTable arg0, Object arg1,
						boolean arg2, boolean arg3, int arg4, int arg5) {
					if (arg1 instanceof JButton) {
						return (JButton)arg1;
					} else {
						if (arg1 != null) 
							return new JLabel(arg1.toString());
						else
							return new JLabel();
					}
					// TODO Auto-generated method stub
				}
			};
		}
		
	}
	
	private String getTabs(AbstractGraphHandler agh) {
		StringBuffer sb = new StringBuffer();
		sb.append("<table border=\"1\">");
		sb.append("<tr><th>Metric</th><th>Value</th></tr>");		
		sb.append("<tr><td>Number of nodes</td><td>");
		sb.append(agh.getNodeContainers().size());
		sb.append("</td></tr>");
		sb.append("<tr><td>Number of links</td><td>");
		sb.append(agh.getLinkContainers().size());
		sb.append("</td></tr>");
		for (MonoComputerProxy proxy : monoProxies.values()) {
			proxy.comp.setAGH(agh);
			proxy.getComputerTabLine(sb);
		}
		sb.append("</table><br>");
		sb.append("<table border=\"1\">");
		sb.append("<tr>");
		sb.append("<th>Metric</th><th>Mean</th><th>Min</th><th>Max</th><th>Med</th><th>Var</th><th>Most central element</th>");
		sb.append("</tr>");			
		for (MultiComputerProxy proxy : multiProxies.values()) {
			try {
				proxy.comp.setAGH(agh);
				proxy.getComputerTabLine(sb);
			}
			catch (Throwable e) {}
		}
		sb.append("</table>");		
		return sb.toString();
	}	
	
	public void format(AbstractGraphHandler agh, OutputStream ous) throws Exception {				
		java.io.OutputStreamWriter ow = new java.io.OutputStreamWriter(ous);
		String sss = voidPage.toString().replace("#PROPERTIES#", getTabs(agh));
		ow.write(sss);
		ow.flush();			
	}
}

class MonoComputerProxy extends ComputerProxy {
	MonoMetricComputer comp;
	
	StringBuffer getComputerTabLine(StringBuffer sb) {
		sb.append("<tr>");
		sb.append("<td>");
		sb.append(computerName);
		sb.append("</td>");
		sb.append("<td>");
		sb.append(Rounder.roundString(comp.computeMetric(), 3));
		sb.append("</td>");	
		sb.append("</tr>");	
		return sb;			
	}
	
	void getLabels(JPanel panel) {
		panel.add(new JLabel(computerName));
		panel.add(new JLabel(Rounder.roundString(comp.computeMetric(), 3)+""));
	}
}

class MultiComputerProxy extends ComputerProxy {
	MultiMetricComputer comp;
	
	java.awt.Font f = new java.awt.Font("Arial",0,10);
	
	void getLabels(Object[] obj) {
		String[] titles = new String[]{
			computerName,
			"" + Rounder.roundString(comp.computeMean(), 3),
			"" + Rounder.roundString(comp.computeMin(), 3),
			"" + Rounder.roundString(comp.computeMax(), 3),
			"" + Rounder.roundString(comp.computeMed(), 3),
			"" + Rounder.roundString(comp.computeVar(), 3)
		};
		for (int i = 0 ; i < titles.length ; i++) {
			obj[i] = titles[i];
		}	
		if (comp instanceof CentralityComputer) {
		//	JLabel lab = new JLabel("" + ((CentralityComputer)comp).getCentralElement());
		//	lab.setFont(f);			
		//	pane.add(lab);	
			obj[titles.length] = "" + ((CentralityComputer)comp).getCentralElement();				
		} else {
			obj[titles.length] = "";
		//	pane.add(new JLabel(""));					
		}
	}
	
	public double[] getValues() {
		
		
		return comp.computeForAllElements();
	}
	
	StringBuffer getComputerTabLine(StringBuffer sb) {	
		sb.append("<td>");
		sb.append(computerName);
		sb.append("</td>");
		sb.append("<td>");
		sb.append(Rounder.roundString(comp.computeMean(), 3));
		sb.append("</td>");
		sb.append("<td>");
		sb.append(Rounder.roundString(comp.computeMin(), 3));
		sb.append("</td>");			
		sb.append("<td>");
		sb.append(Rounder.roundString(comp.computeMax(), 3));
		sb.append("</td>");	
		sb.append("<td>");
		sb.append(Rounder.roundString(comp.computeMed(), 3));
		sb.append("</td>");	
		sb.append("<td>");
		sb.append(Rounder.roundString(comp.computeVar(), 3));
		sb.append("</td>");
		sb.append("<td>");
		if (comp instanceof CentralityComputer) {
			sb.append(((CentralityComputer)comp).getCentralElement());		
		}
		sb.append("</td>");			
		sb.append("<td>");
			sb.append("<form id=\"hist"+computerName+"\" method=\"POST\" action=\"histogram\">");
			sb.append("<input type=\"hidden\" name=\"vals\" value=\"");
			sb.append(comp.computeAll());
			sb.append("\"><input type=\"button\" value=\"See histogram\" ");
			sb.append("onclick=\"document.getElementById('hist"+computerName+"').submit();\">");				
			sb.append("<input type=\"hidden\" name=\"title\" value=\""+computerName+"\"/>");
			sb.append(" </form>");
		sb.append("</td>");	
		sb.append("</tr>");	
		return sb;			
	}	
}

abstract class ComputerProxy {
	
	String computerName;
	
	abstract StringBuffer getComputerTabLine(StringBuffer sb);
	
/*	StringBuffer getComputerTab() {
		StringBuffer sb = new StringBuffer();
		sb.append("<div class=\"tabbertab\"><h2>");
		sb.append(computerName);
		sb.append("</h2><p>");
		sb.append(getMethodsContent());
		sb.append("</p></div>");
		return sb;
	}*/
	
/*	StringBuffer getMethodsContent() {
		StringBuffer sb = new StringBuffer();
		int index = 0;
		for (Method m : methods) {		
			sb.append("<fieldset style=\"margin: 1em; text-align: left;\">");
			sb.append("<legend>Method : " + m.getName() + "</legend>");		
			sb.append("</fieldset>");
		}
		return sb;		
	}*/	
	
}