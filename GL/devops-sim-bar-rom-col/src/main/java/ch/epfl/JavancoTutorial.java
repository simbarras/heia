package ch.epfl;

import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.base.GraphHandlerFactory;
import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.network.NodeContainer;
import ch.epfl.javanco.ui.MinimalUI;
import ch.epfl.javanco.ui.swing.multiframe.JavancoDefaultGUI;

/**
 * This class acts as a tutorial for new comers in the Javanco world
 *
 * When using Javanco as a library, inside a java class,
 * before all one should be sure to import correctly all the
 * required libraries.
 * 
 * By calling the ant target "ant dist",
 * all required jars and optinal dlls or so files will be placed
 * in the "output/dist/lib" directory. By importing all these files,
 * javanco should work without problems
 */
public class JavancoTutorial {

	/**
	 * Launchs the tutorial
	 */
	public static void main(String[] args) throws Exception {
	/*	tutorial1();
		pause();
		tutorial2();
		pause();
		tutorial3();
		pause();*/
		tutorial4();
	}

	/**
	 * First part of the tutorial
	 */
	public static void tutorial1() throws Exception {
		// First of all, Javanco should be initialised. This command
		// is useful above all to detect the root of the Javanco project (the JAVANCO_HOME)
		// from which many files and settings are deduced, and to load some
		// configuration files.

		// If this command is not called explicitely, it will be call by the
		// Javanco.getDefaultGraphHandler() statement

		Javanco.initJavanco();


		//   remark : it is possible to load other property files using this way.
		//            all read properties will be then accessible in this way :

		System.out.println(System.getProperty("ch.epfl.javanco.testProperty"));

		// Once the properties have been loaded, on can start using
		//   javanco main object : the AbstractGraphHandler. Generally,
		//   AbstractGraphHandler are defined as "agh" variables.

		// AGH's objects cannot be instanciated directly. Two ways exists to
		// get one. The simplest way is the following (more explanations about
		// why there is a "true" will come after)

		AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(false);

		new MinimalUI(agh);

		// Another way to do it : first get the GraphHandlerFactory :

		GraphHandlerFactory ghf = Javanco.getDefaultGraphHandlerFactory();
		AbstractGraphHandler agh2 = ghf.getNewGraphHandler();

		// this last way is usefull if more than one agh must be created

		// Once an agh is created, one can either load an existing file:
		agh.openNetworkFile("default.xml");

		// either use it to create a new network:
		agh2.newNetwork("tutorial_network");

		//   remark : when loading a file, the path can be either relative or
		//            absolute. When using a relative path, the file is searched
		//            in the "default_graphs" directory.

		// Let's add some nodes on the created network
		agh2.newNode(100,100);
		agh2.newNode(50,50);
		agh2.newLink(0,1);

		// and now, one can consult the content of the handlers :

		System.out.println(agh.getNodeContainer(0));
		System.out.println(agh2.getLinkContainer(0,1));

		// it is also possible to remove objects :

		agh2.removeElement(agh2.getLinkContainer(0,1));
		System.out.println("Number of links in agh2 : " + agh2.getLinkContainers().size());

	}

	public static void tutorial2() throws Exception {
		// This section explains how the attribute system is working :

		// One creates first an agh an three objects again :
		//    remark : the properties have been loaded by the method tutorial1,
		//             there is no need to load them again
		//    note :   when creating objects, it is possible to keep
		//             a pointer toward them

		AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(false);
		agh.newNetwork("tutorial_network");
		agh.newNode(100,100);
		NodeContainer node1 = agh.newNode(50,50);
		agh.newLink(0,1);

		// Several attributes are associated to each object (one says container).
		// It is possible to get the value in the following way

		System.out.println("X coord of node : " + node1.attribute("pos_x").intValue());
		//   remark : the method attribute return a NetworkAttribute object.
		//            Then, the attribute can be converted in various formats using
		//            inValue, floatValue, booleanValue etc. To simply convert in String,
		//            getValue() can be used.

		// Attributes can be easily added
		node1.attribute("new_attribute").setValue("new_value");

		// In the facts, the method attribute(String) never returns null. If no
		// existing attribute with the given name is found, a new one is
		// automatically created. Problem : how to test if an object has
		// or has'nt an attribute ?

		System.out.println("This should return null : " + node1.attribute("test", false));

		// Attribute can also be modified :

		node1.attribute("pos_x").setValue(323);

		System.out.println("new X coord of node : " + node1.attribute("pos_x").floatValue());
	}

	public static void tutorial3() throws Exception {
		// This section explains how the containers are connected
		// with the XML.

		// An AGH can be configured to store all the informations directly
		// under the form of an XML DOM tree, or not.

		// When an AGH is loaded from XML file, it keeps the track of the original tree :
		AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(false);
		agh.openNetworkFile("default.xml");
		System.out.println("\r\n\r\n1)   XML version of the graph");
		agh.dumpGraph(System.out);

		// However, if one now add more nodes to the AGH, changes are not reported
		// the DOM :

		agh.newNode(100,120);
		agh.newNode(100,100);
		System.out.println("\r\n\r\n2)   XML version of the graph");
		agh.dumpGraph(System.out);

		// In order to keep the consistence between the AGH and the XML tree, the
		// following method must be called just after getting the emply agh :

		agh = Javanco.getDefaultGraphHandler(false);
		agh.activateMainDataHandler();
		agh.openNetworkFile("default.xml");

		// In this way, new nodes added are listed in the XML version

		NodeContainer node1 = agh.newNode(100,100);
		System.out.println("\r\n\r\n3)   XML version of the graph");
		agh.dumpGraph(System.out);
		System.out.println("remark that this node appears now : " + node1.getIndex());

		// Nevertheless, the node appears alone, without its positions.
		// To keep tracks of the positions also, another method must be called :

		agh.activateGraphicalDataHandler();
		node1 = agh.newNode(200,200);
		System.out.println("\r\n\r\n4)   XML version of the graph");
		agh.dumpGraph(System.out);
		System.out.println("remark that this node appears now, with positions : " + node1.getIndex());

		// One last word about the XML : the linkage of attributes. One creates a
		// new empty agh and one adds one node :
		agh = Javanco.getDefaultGraphHandler(false);
		agh.activateMainDataHandler();
		agh.newNetwork("test");
		node1 = agh.newNode();

		// One adds one attribute to this node :
		node1.attribute("foo").setValue("BAR");

		// when dumping the graph, the attribute does not appear
		System.out.println("\r\n\r\n5)   XML version of the graph");
		agh.dumpGraph(System.out);

		// to make it appear, attribute must be linked to one group of elements. In
		// general, attributes are linked to the "main_description" group :
		node1.linkAttribute("foo","main_description");

		// attribute now appears :
		System.out.println("\r\n\r\n6)   XML version of the graph");
		agh.dumpGraph(System.out);
	}

	public static void tutorial4() throws Exception {
		// This section explains how the GUI works

		// One can easily display start the GUI to display a network		
		JavancoDefaultGUI gui = JavancoDefaultGUI.getAndShowDefaultGUI();

		// From there, if a graph is created with a "true", it is registered in the GUI

		AbstractGraphHandler agh = Javanco.getDefaultGraphHandler(true);

		agh.newNetwork("testtest");
		agh.newNode(100,100);
		
		// Once created, the graph can be removed from the GUI
		pause();
		agh.deregisterGraph();
		pause();
			
		// A graph can also be created first, and added later to the GUI
		agh = Javanco.getDefaultGraphHandler(false);
		agh.newNetwork("je");
		agh.newNode(200,300);
		agh.newNode(100, 100);
		
		System.out.println("Graph created but not registered " + agh);
		pause();		
		
		// Let's add it...
		Javanco.getDefaultGraphHandlerFactory().registerAgh(agh);
		
		// Graphs can also be retrieved from the GUI :
		AbstractGraphHandler fromGui = gui.getCurrentlyActiveAgh();
		fromGui.newNode(400, 400);

	}

	private static void pause() throws Exception {
		System.out.println("\r\n\r\nPress \"enter\" to continue");
		System.in.read();
	}

}
