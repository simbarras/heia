package ch.epfl.javancox.execdeamon;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import java.awt.Container;

import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.results.FileResultWriter;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;
import ch.epfl.javancox.experiments.builder.tree_model.AbstractDefinition;
import ch.epfl.javancox.experiments.builder.tree_model.DefinitionIterator;
import ch.epfl.javancox.experiments.builder.tree_model.ObjectConstuctionTreeModel;
import ch.epfl.javancox.results_manager.SmartDataPointCollector;
import ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI;

public class RemoteExecutionExperimentConfigurator extends ExperimentConfigurationCockpit {

	private static final long serialVersionUID = 1L;
	
	public boolean timeout;
	public long startTime;
	public long endTime;
	public long totalTime = 0;
	static int baseNumber;
	static int numberOfTasksToDo = 0;
	static int completedTasks = 0;
	public static int totalTasks;
	static int connectionCount = 0;
	static ArrayList<String> taskStats = new ArrayList<String>();
	
	
	public static void main(String[] args) {
		try {
			sendMyInfo();
			(new RemoteExecutionExperimentConfigurator()).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	JButton runRemote = new JButton("Run remote");
	JButton checkNumberOfCores = new JButton("Check Number of Cores");
	JTextArea remoteIp = new JTextArea("128.59.65.15");
	JTextArea remoteIp2 = new JTextArea("lightwave5.ee.columbia.edu");
	JTextArea remoteIp3 = new JTextArea("lightwave5.ee.columbia.edu");
	JTextArea remoteIp4 = new JTextArea("lightwave5.ee.columbia.edu");
	JTextArea numberOfThreads = new JTextArea("0");
	JTextArea numberOfThreads2 = new JTextArea("0");
	JTextArea numberOfThreads3 = new JTextArea("0");
	JTextArea numberOfThreads4 = new JTextArea("0");
	JLabel tasksExecuted = new JLabel("Tasks executed ");
	JLabel serverName = new JLabel("Server 1:");
	JLabel serverName2 = new JLabel("Server 2:");
	JLabel serverName3 = new JLabel("Server 3:");
	JLabel serverName4 = new JLabel("Server 4:");
	JLabel tasksExecutedNumber = new JLabel("0");
	ClassServer classServer;
	JFrame frmMain;
	Container pane;
	JProgressBar progBar;
	
	


	public RemoteExecutionExperimentConfigurator() throws IOException,
			InterruptedException {

		// info is the panel that belong to the upper class
		info.add(tasksExecuted);
		info.add(tasksExecutedNumber);
		info.add(runRemote);
		info.add(checkNumberOfCores);
		info.add(serverName);
		info.add(remoteIp);
		info.add(numberOfThreads);
		info.add(serverName2);
		info.add(remoteIp2);
		info.add(numberOfThreads2);
		info.add(serverName3);
		info.add(remoteIp3);
		info.add(numberOfThreads3);
		info.add(serverName4);
		info.add(remoteIp4);
		info.add(numberOfThreads4);

		checkNumberOfCores.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ArrayList<String> j = new ArrayList<String>();
					j.add(remoteIp.getText());
					j.add(remoteIp2.getText());
					j.add(remoteIp3.getText());
					j.add(remoteIp4.getText());
					int l = 0;
					for (String i : j) {
						ArrayList<String> coreRequest = new ArrayList<String>();
						coreRequest.add("2");
						Socket clientSocket = new Socket(i, 6696);
						ObjectOutputStream o = new ObjectOutputStream(clientSocket.getOutputStream());
						o.writeObject(coreRequest);
						BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
						int numberOfCores = inFromServer.read();

						clientSocket.close();

						if (l == 0) {
							numberOfThreads.setText(Integer
									.toString(numberOfCores));
						}

						if (l == 1) {
							numberOfThreads2.setText(Integer
									.toString(numberOfCores));
						}
						
						if (l == 2) {
							numberOfThreads3.setText(Integer
									.toString(numberOfCores));
						}
						if (l == 3) {
							numberOfThreads4.setText(Integer
									.toString(numberOfCores));
						}

						l++;
					}

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

		});

		runRemote.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					completedTasks = 0;
					startTime = System.currentTimeMillis();
					
					ArrayList<Integer> threadCounts = new ArrayList<Integer>();
					threadCounts.add(Integer.parseInt(numberOfThreads.getText()));
					threadCounts.add(Integer.parseInt(numberOfThreads2.getText()));
					threadCounts.add(Integer.parseInt(numberOfThreads3.getText()));
					threadCounts.add(Integer.parseInt(numberOfThreads4.getText()));
					runRemote(remoteIp.getText(), remoteIp2.getText(), remoteIp3.getText(), remoteIp4.getText());
					
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null,
							"Error : " + e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}

		});
	}

	private void runRemote(final String remoteIp, final String remoteIp2, final String remoteIp3, final String remoteIp4)
			throws IOException {
		final SmartDataPointCollector localdb = new SmartDataPointCollector();
		final AbstractResultsDisplayer gui = DefaultResultDisplayingGUI.displayDefault(localdb);
		if (classServer == null) {
			classServer = new ClassServer();
			classServer.start();
		}
		Thread.currentThread().setName("to change");
		DefinitionIterator iterator = ((ObjectConstuctionTreeModel) tree.getModel()).getObjectDefinitionIterator();

		// putting all the definitions into one array
		ArrayList<AbstractDefinition> definitionArray = new ArrayList<AbstractDefinition>();
		for (AbstractDefinition d : iterator.toIterable()) { // this iterates on
																// the "tasks"
																// (for each
																// AbstractDefinition
																// d in
																// Iterable<AbstractDefinition>)
			// the toIterable() makes the object iterator compatible with the
			// for each loop
			definitionArray.add(d); // adds each task
		}

		totalTasks = definitionArray.size();
        frmMain = new JFrame("Execution Progress");		
        frmMain.setSize(300, 100); 
        pane = frmMain.getContentPane();
        pane.setLayout(null); 
        progBar = new JProgressBar(0, definitionArray.size());
        pane.add(progBar);
        progBar.setBounds(10, 10, 280, 20);
        frmMain.setResizable(false);
        frmMain.setVisible(true);
        frmMain.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
		final ArrayList<AbstractDefinition> defArray = definitionArray;

		// retrieve the max number of threads from the gui
		int maxThreads;

		String tempString = numberOfThreads.getText();
		String threadCount = numberOfThreads2.getText();
		String threadCount2 = numberOfThreads3.getText();
		String threadCount3 = numberOfThreads4.getText();


		final int threadCountOne = (Integer.parseInt(tempString));
		final int threadCountTwo = (Integer.parseInt(threadCount));
		final int threadCountThree = (Integer.parseInt(threadCount2));
		final int threadCountFour = (Integer.parseInt(threadCount3));

		if (tempString.equals("max"))
			maxThreads = definitionArray.size();
		else
			maxThreads = threadCountOne + threadCountTwo + threadCountThree + threadCountFour;

		int max = maxThreads;

		if (maxThreads >= definitionArray.size()) {
			max = definitionArray.size();
		}

		for (int k = 0; k < max; k++) {
			final int j = k;
			final int numberOfCores = maxThreads;

			Thread t = new Thread() {
				
				@Override
				public void run() {
					try {
						connectionCount++;
						String ipAddress;
						ipAddress = remoteIp;
						int coresPerThread = (int) Math.ceil(((double) defArray.size() / (double) (threadCountOne + threadCountTwo + threadCountThree + threadCountFour)));
						int finalParameter = 0;
						int initialParameter = 0;
						if (numberOfCores < defArray.size()) {
							if ((j + 1) <= defArray.size() - numberOfCores) {
								initialParameter = coresPerThread * j;
								finalParameter = initialParameter
										+ (coresPerThread);
							}

							if ((j + 1) > defArray.size() - numberOfCores) {
								initialParameter = j + (defArray.size() - numberOfCores);
								finalParameter = initialParameter + 1;
							}
						}

						if (numberOfCores >= defArray.size()) {
							initialParameter = j;
							finalParameter = initialParameter + 1;
						}

						if (j >= threadCountOne) {
							ipAddress = remoteIp2;
						}
						
						if (j>= (threadCountTwo + threadCountOne)) {
							ipAddress = remoteIp3;
						}
						
						if (j>= (threadCountTwo + threadCountOne + threadCountThree)) {
							ipAddress = remoteIp4;
						}
						
						numberOfTasksToDo = defArray.size();
						if (defArray.size() - numberOfCores > numberOfCores) {
							if (j + 1 < defArray.size() - ((coresPerThread - 1) * numberOfCores)) {
								initialParameter = j * coresPerThread;
								finalParameter = initialParameter + coresPerThread;
							}

							if (j + 1 == defArray.size() - ((coresPerThread - 1) * numberOfCores)) {
								initialParameter = j * coresPerThread;
								finalParameter = initialParameter + coresPerThread;
							}
							if (j + 1 > defArray.size() - ((coresPerThread - 1) * numberOfCores)) {
								int baseNumber = (defArray.size() - ((coresPerThread - 1) * numberOfCores)) * coresPerThread;
								int newIncrement = coresPerThread - 1;
								int lastNormalIndex = defArray.size() - ((coresPerThread - 1) * numberOfCores);
								int currentIndex = j;
								initialParameter = baseNumber + ((currentIndex - lastNormalIndex) * newIncrement);
								finalParameter = initialParameter + newIncrement;
							}
						}
						
						final DeamonClient client = new DeamonClient(classServer.getAddress(), ipAddress);
						
						System.out.println((j + 1) + ": " + initialParameter + "," + (finalParameter));
						
						long t1 = System.nanoTime();
						
						for (AbstractDefinition d : defArray.subList( initialParameter, finalParameter)) {
							
							// this
						// iterates on the "tasks"(for each AbstractDefinition d in definitionArray)
							// each Definition "d" is representing a task, it is
							// repeated here because it need to be final
							final AbstractDefinition taskDefinition = d;
							
							Object received = client.send(taskDefinition);
							// Change the tasksExecutedNumber label so that it
							// reflects the progression
							FileResultWriter remotedb = (FileResultWriter) received;
							synchronized (localdb) {
								for (Execution exec : remotedb) {
									localdb.addExecution(exec);
								}
								gui.refresh();
							}
						//	System.out.println(received.getClass());
						//	System.out.println(ipAddress);	
							numberOfTasksToDo--;
							completedTasks++;
						//	System.out.println(completedTasks);
							progBar.setValue(completedTasks);
							progBar.repaint();
							endTime = System.currentTimeMillis();
							totalTime = endTime - startTime; 
							//System.out.print(totalTime);
							ArrayList<Long> times = new ArrayList<Long>(); 
							times.add(totalTime);
							if (completedTasks == defArray.size()) {
								System.out.println("Total Time: " + Collections.max(times));
								frmMain.dispose();
							}
							
						//	System.out.println(Collections.max(times));
							/*
							 * if (Thread.interrupted()) { client.close();
							 * System.out.println("Thread time out"); //throw
							 * new InterruptedException(); }
							 */
						}
						client.close();
						
						t1 = System.nanoTime() - t1;

						Execution exec = new Execution();

						DataPoint dp = new DataPoint();
						dp.addProperty("Experiment range", initialParameter + "," + finalParameter);
						dp.addProperty("Remote server", ipAddress);
						dp.addResultProperty("Execution Time Per Thread", t1);

						exec.addDataPoint(dp);
						synchronized (localdb) {
							localdb.addExecution(exec);
						}
						connectionCount--;
					
					} catch (SocketException e) {
						System.out.println(getName()
								+ " was stopped by the server");
					}

					catch (IOException e) {
						throw new IllegalStateException(e);
					}

					catch (ClassCastException e) {
						e.printStackTrace();
						return;
					} 

					/*
					 * catch (InterruptedException e) {
					 * System.out.println("Thread timed out"); }
					 */

				}
			};
			t.setName("Client thread " + k);
			t.start();
		}	
	}
	public static void sendMyInfo() {
		Thread infoSender = new Thread() {
			public void run() {
				try {
					while (true) {
						taskStats.add("1");
						taskStats.add(Integer.toString(numberOfTasksToDo));
						taskStats.add(Integer.toString(totalTasks));
						taskStats.add(Integer.toString(connectionCount));
						Socket sendInfoSocket = new Socket("128.59.65.38", 6696);
						ObjectOutputStream o = new ObjectOutputStream(sendInfoSocket.getOutputStream());
						o.writeObject(taskStats);
						sendInfoSocket.close();
						Thread.sleep(1000);
						taskStats.clear();

					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};
		infoSender.setName("Send information about server");
		infoSender.start();
	}
	
}

