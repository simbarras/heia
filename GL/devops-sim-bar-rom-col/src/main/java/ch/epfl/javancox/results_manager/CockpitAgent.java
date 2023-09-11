package ch.epfl.javancox.results_manager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;
import ch.epfl.javancox.results_manager.standalone.StandaloneReader;

public class CockpitAgent {
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Cockpit agent");
		JMenuBar bar = new JMenuBar();
		JMenu actions = new JMenu("Actions");
		JMenuItem resetDB = new JMenuItem("Reset database");
		
		final SmartDataPointCollector db = new SmartDataPointCollector();
		final AbstractResultsDisplayer dis = DefaultResultDisplayingGUI.displayDefault(db);
				
		resetDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				db.clear();
				dis.refresh();
			}
		});
		actions.add(resetDB);
		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				System.exit(0);
			}
		});
		actions.add(quit);	
		bar.add(actions);
		frame.setJMenuBar(bar);	
		frame.setVisible(true);
		frame.setSize(200,100);
		
		listen(db, dis);
	}
	
	private static void listen(SmartDataPointCollector db, AbstractResultsDisplayer dis) {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(48000);		
			ss.setReuseAddress(true);
			
			int connectionIdx = 0;
			while (true) {
				InputStreamReader read  = null;
				Socket socket = null;
				BufferedReader buf = null;
				try {
					socket = ss.accept();
					connectionIdx++;
					System.out.println("acepted");
					read = new InputStreamReader(socket.getInputStream());
					
					buf = new BufferedReader(read);
					
					while (true) {
						String s = buf.readLine();
						System.out.println(s);
						StandaloneReader.insertLine("connection="+connectionIdx+","+s, db);
						dis.refresh();
					}	
				}
				catch (Exception e) {
				}
				finally {
					try {
						buf.close();
						read.close();
						socket.close();	
					}
					catch(IOException e) {
					}
				}
				System.out.println("End of one connection");	
			}						
		}
		catch (IOException f) {
		}	
		finally {
			if (ss != null)
				try {
					ss.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
}
