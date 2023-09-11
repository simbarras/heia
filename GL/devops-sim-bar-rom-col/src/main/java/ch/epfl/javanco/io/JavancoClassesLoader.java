package ch.epfl.javanco.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import ch.epfl.general_libraries.logging.Logger;
import ch.epfl.javanco.network.AbstractElement;
import ch.epfl.javanco.network.Layer;
import ch.epfl.javanco.network.Link;
import ch.epfl.javanco.network.Node;
import ch.epfl.javanco.ui.swing.FileChooser;

@SuppressWarnings("unchecked")
public class JavancoClassesLoader extends ClassLoader {
	static Logger logger;

	static {
		//	System.out.println(System.getProperty("log4j.configuration"));
		logger = new Logger(JavancoClassesLoader.class);
		//	logger.info("Logger started");
	}


//	private JavancoFile generatedClassesDir = null;
	private JavancoFile previous = null;

	public JavancoClassesLoader(JavancoFile rootClassDir) {
//		generatedClassesDir = rootClassDir;
		initClassDir(rootClassDir);
		setupLists(rootClassDir);
	}

	public JavancoClassesLoader() {
		setupLists();
	}

	public void initClassDir(JavancoFile generatedClassesDir) {
		try {
			if (generatedClassesDir == null) {
				generatedClassesDir = FileChooser.promptForSelectDir(null, null, "Please locate the directory containing the extra classes");
			} else if (generatedClassesDir.exists() == false) {
				logger.info("GeneratedClassesDir must be created");
				generatedClassesDir.mkdirs();
			}
			if (generatedClassesDir != previous) {
				setupLists(generatedClassesDir);
				logger.info("GeneratedClassesDir is : \r\n-->   " + generatedClassesDir);
				previous = generatedClassesDir;
			}
		}
		catch (Exception e) {
			//e.printStackTrace();
		}
	}

	private Hashtable<String, Class<? extends Layer>> networkClassList = new Hashtable<String, Class<? extends Layer>>();
	private Hashtable<String, Class<? extends Node>> nodeClassList    = new Hashtable<String, Class<? extends Node>>();
	private Hashtable<String, Class<? extends Link>> linkClassList    = new Hashtable<String, Class<? extends Link>>();

	private ArrayList<Class<? extends AbstractElement>> classList = null;

	public void setupLists() {
		networkClassList.put(ch.epfl.javanco.network.DefaultGraphImpl.class.getSimpleName(), ch.epfl.javanco.network.DefaultGraphImpl.class);
		nodeClassList.put(ch.epfl.javanco.network.DefaultNodeImpl.class.getSimpleName(), ch.epfl.javanco.network.DefaultNodeImpl.class);
		linkClassList.put(ch.epfl.javanco.network.DefaultLinkImpl.class.getSimpleName(), ch.epfl.javanco.network.DefaultLinkImpl.class);
	}

	public void setupLists(JavancoFile rootClassDir) {
		setupLists();
		ArrayList<JavancoFile> classFileList = new ArrayList<JavancoFile>();
		try {
			processDir(rootClassDir, "", classFileList);
			classList = getClasses(classFileList);
			sortClasses(classList);
		}
		catch (IllegalStateException e) {
			logger.error("Additional classes have not been loaded properly",e);
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private void processDir(JavancoFile dir, String prefix, List<JavancoFile> classFileList) {
		if (!(dir.exists())) {
			throw new IllegalStateException("Directory " + dir + " not found");
		}
		JavancoFile[] files = dir.listFiles();
		for (JavancoFile file : files) {
			if (file.isDirectory()) {
				processDir(file, prefix + dir.getName() + ".", classFileList);
			} else if (file.getName().endsWith(".class")) {
				classFileList.add(file);
			}
		}
	}

	private HashSet<JavancoFile> allreadyReadFiles = new HashSet<JavancoFile>();

	private ArrayList<Class<? extends AbstractElement>> getClasses(ArrayList<JavancoFile> classFileList) throws IOException {
		ArrayList<Class<? extends AbstractElement>> classes = new ArrayList<Class<? extends AbstractElement>>();
		for (JavancoFile file : classFileList) {
			if (file.length() > 0) {
				if (allreadyReadFiles.contains(file) == false) {
					byte[] buffer = new byte[(int)file.length()];
					FileInputStream fis = new FileInputStream(file);
					fis.read(buffer);
					try {
						Class<? extends AbstractElement> c = (Class<? extends AbstractElement>)this.defineClass(null,buffer,0,buffer.length);
						Class<? extends AbstractElement> c_copy = null;
						try {
							c_copy = (Class<? extends AbstractElement>)Class.forName(c.getName());
						}
						catch (ClassNotFoundException e) {}
						if (c_copy !=null) {
							classes.add(c_copy);
						} else {
							classes.add(c);
						}
						allreadyReadFiles.add(file);
					}
					catch (java.lang.LinkageError e) {}
					fis.close();
				}
			}
		}
		return classes;
	}

	private void sortClasses(ArrayList<Class<? extends AbstractElement>> classList) {
		if (classList != null) {
			for (Class c : classList) {
				if (ch.epfl.javanco.network.Layer.class.isAssignableFrom(c)) {
					addToWithoutDupl(c, networkClassList);
					logger.trace("Added to networks : " + c);
				}
				if (ch.epfl.javanco.network.Node.class.isAssignableFrom(c)) {
					addToWithoutDupl(c, nodeClassList);
					logger.trace("Added to node" + c);
				}
				if (ch.epfl.javanco.network.Link.class.isAssignableFrom(c)) {
					addToWithoutDupl(c, linkClassList);
					logger.trace("Added to link" + c);
				}
			}
		}
	}

	private void addToWithoutDupl(Class c, Hashtable t) {
		String className = c.getSimpleName();
		int increment = 2;
		while (networkClassList.get(className) != null) {
			className = c.getSimpleName() + " (" + increment + ")";
			increment++;
		}
		t.put(className, c);
	}

	public String[] getNetworkClassesNames() {
		if (networkClassList != null) {
			return networkClassList.keySet().toArray(new String[]{});
		}
		return new String[]{};
	}

	public Class<? extends Layer> getNetworkClass(String simpleName) {
		if (networkClassList != null) {
			return networkClassList.get(simpleName);
		}
		return null;
	}

	public Hashtable<String, Class<? extends Link>> getAllowedLinkClasses(Class networkClass) {
		Hashtable<String,Class<? extends Link>> ht = new Hashtable<String, Class<? extends Link>>();
		ht.put(ch.epfl.javanco.network.DefaultLinkImpl.class.getSimpleName(), ch.epfl.javanco.network.DefaultLinkImpl.class);
		try {
			String[] names = (String[])(networkClass.getDeclaredField("LINKS_TYPES").get(null));
			for (String s : names) {
				for (java.util.Map.Entry<String, Class<? extends Link>> entry : linkClassList.entrySet()) {
					if (entry.getValue().getSimpleName().equals(s)) {
						ht.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		catch (Exception e) {
			logger.error("Layer type : " + networkClass.getSimpleName() +
					" does not define a \"LINKS_TYPES\" field or this field is unaccessible. Only the default link type will therefore be available", e);
		}
		return ht;
	}

	public Class<? extends Link> getLinkClass(String simpleName) {
		if (linkClassList != null) {
			return linkClassList.get(simpleName);
		}
		return null;
	}

	public Hashtable<String, Class<? extends Node>> getAllowedNodeClasses(Class networkClass) {
		Hashtable<String,Class<? extends Node>> ht = new Hashtable<String, Class<? extends Node>>();
		ht.put(ch.epfl.javanco.network.DefaultNodeImpl.class.getSimpleName(), ch.epfl.javanco.network.DefaultNodeImpl.class);
		try {
			String[] names = (String[])(networkClass.getDeclaredField("NODES_TYPES").get(null));
			for (String s : names) {
				for (java.util.Map.Entry<String, Class<? extends Node>> entry : nodeClassList.entrySet()) {
					if (entry.getValue().getSimpleName().equals(s)) {
						ht.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		catch (Exception e) {
			logger.error("Layer type : " + networkClass.getSimpleName() +
					" does not define a \"NODES_TYPES\" field or this field is unaccessible. Only the default node type will therefore be available", e);
		}
		return ht;
	}

	public Class<? extends Node> getNodeClass(String simpleName) {
		if (nodeClassList != null) {
			return nodeClassList.get(simpleName);
		}
		return null;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException  {
		Class toReturn = null;
		if (networkClassList != null) {
			toReturn = networkClassList.get(name);
		}
		if (toReturn == null && nodeClassList != null) {
			toReturn = nodeClassList.get(name);
		}
		if (toReturn == null && linkClassList != null) {
			toReturn = linkClassList.get(name);
		}
		if (toReturn != null) {
			return toReturn;
		} else {
			throw new ClassNotFoundException(name);
		}
	}
}