package ch.epfl.javancox.experiments.builder.tree_model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import ch.epfl.general_libraries.utils.TypeParser;

public class ObjectDefinition extends AbstractDefinition {
	
	private static final long serialVersionUID = 1L;
	ArrayList<AbstractDefinition> list;
	String constructorDef;
	
	public ObjectDefinition(String className) {	
		this(className, null);
	}

	public ObjectDefinition(String className, String constructorDef) {
		this.def = className;
		this.constructorDef = constructorDef;
	}
	
	private void initList() {
		if (list == null) {
			list = new ArrayList<AbstractDefinition>();
		}
	}
	
	public void addDefinition(AbstractDefinition d) {
		//if (d == null) throw new NullPointerException();
		initList();
		list.add(d);		
	}
	
	public Object build(ClassLoader loader) {
		try {
			Class<?> clazz = getDefinedClass(loader);
			if (constructorDef != null) {
				Class[] paramsTypes = getParameterTypes(loader);
				Object[] paramsObjects = getParameterObjects(loader);
				Constructor<?> con = clazz.getConstructor(paramsTypes);
				return con.newInstance(paramsObjects);
			} else {
				return getPrimitiveTypeObject();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	protected Object getPrimitiveTypeObject()  {
		// s contains the value of the single leaf
		AbstractDefinition ab = list.iterator().next();
		if (ab == null) return null;
		String s = ab.def;
		return TypeParser.parseRawType(def, s);
	}
	
	protected Class getDefinedType(ClassLoader loader) throws ClassNotFoundException {
		if (constructorDef != null) {
			try {
				return Class.forName(def, false, loader);
			}
			catch (ClassNotFoundException e) {
				int index = def.lastIndexOf(".");
				String newName = def.substring(0, index);
				newName = newName + "$" + def.substring(index+1, def.length());
				return Class.forName(newName, false, loader);
			}
		} else {
			return TypeParser.getRawType(def);
		}
	}	
	
	protected Class getDefinedClass(ClassLoader loader) throws ClassNotFoundException {
		if (constructorDef != null) {
			return Class.forName(constructorDef, false, loader);
		} else return getDefinedType(loader);
	}
	
	private Class[] getParameterTypes(ClassLoader loader) throws ClassNotFoundException {
		if (list != null) {
			Class[] types = new Class[list.size()];
			for (int i = 0 ; i < list.size(); i++) {
				ObjectDefinition defin = (ObjectDefinition)list.get(i);
				types[i] = defin.getDefinedType(loader);
			}
			return types;
		} else {
			return new Class[]{};
		}
	}
	
	private Object[] getParameterObjects(ClassLoader loader) {
		if (list != null) {
			Object[] objects = new Object[list.size()];
			for (int i = 0 ; i < list.size(); i++) {
				ObjectDefinition defin = (ObjectDefinition)list.get(i);
				objects[i] = defin.build(loader);
			}
			return objects;
		} else {
			return new Object[]{};
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		localToString(prefix, sb);
		return sb.toString();
	}
	
	public void localToString(String prefix, StringBuilder sb) {
		if (constructorDef != null) {
			sb.append(prefix + "-" + def + " -- " + constructorDef + "\r\n");
		} else {
			sb.append(prefix + "-" + def + "\r\n");
		}
		if (list != null) {
			for (AbstractDefinition d : list) {
				d.localToString(prefix + "  ", sb);
			}
		}
	}
}

class StringDefinition extends AbstractDefinition {

	private static final long serialVersionUID = 1L;

	StringDefinition(String s) {
		def = s;
	}
	
	void localToString(String prefix, StringBuilder sb) {
		sb.append(prefix + "-" + def + "\r\n");
	}
}