package ch.epfl.javanco.scripting;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import ch.epfl.general_libraries.event.RunnerEvent;
import ch.epfl.general_libraries.event.RunnerEventHandler;
import ch.epfl.general_libraries.event.RunnerEventListener;
import ch.epfl.general_libraries.io.MultiplePrintStream;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.event.ElementEvent;
import ch.epfl.javanco.io.JavancoFile;

public class GroovyScriptManager extends RunnerEventHandler implements RunnerEventListener {

	private AbstractGraphHandler handler = null;

	private Hashtable<String, JavancoFile> recentScripts = new Hashtable<String, JavancoFile>();

	private Binding bind = null;

	public GroovyScriptManager(AbstractGraphHandler handler) {
		this.handler = handler;
		for (RunnerEventListener listener : handler.getUIDelegate().getRunnerEventListeners()) {
			addRunnerEventListener(listener);
		}
		addRunnerEventListener(this);
	}

	public void executeScript(String scriptFilePath) throws ScriptExecutionException,
	org.codehaus.groovy.control.CompilationFailedException,
	java.io.IOException
	{
		String dir = JavancoFile.getDefaultGroovyScriptDir();
		executeScript(new JavancoFile(dir + scriptFilePath));
	}

	/**
	 * Call to launch a script. When this method is called for the first time,
	 * a "workspace" for groovy variables is created. This workspace is
	 * deleted when this methos returns, exepted when variable has been saved
	 * (@see ch.epfl.javanco.scripting.GroovyScriptTools).
	 */
	public void executeScript(JavancoFile scriptFile)
	throws ScriptExecutionException,
	org.codehaus.groovy.control.CompilationFailedException,
	java.io.IOException
	{
		recentScripts.put(scriptFile.getName(), scriptFile);
		incrementScriptNumber();
		Script script = null;


		try {
			script = (new GroovyShell()).parse(scriptFile);
		}
		catch (Exception e) {
			handler.displayWarning("Error while running script",e);
			decrementScriptNumber();
			return;
		}
		try {
			runMain(script, bind);
		}
		catch (Exception e) {
			handler.displayWarning("Error while running script",e);
			decrementScriptNumber();
			return;
		}
		try {
			if (script.getClass().getMethod("step", new Class[]{}) != null) {
				Runner scriptRunner = new Runner(script, scriptFile.getName(), bind);
				scriptRunner.init();
			} else {
				decrementScriptNumber();
			}
		}
		catch (NoSuchMethodException e) {
		}
		catch (Exception e) {
			handler.displayWarning("Error while running script",e);
			decrementScriptNumber();
			return;
		}
	}


	/**
	 * <br />
	 * #author Christophe Trefois
	 * @param statementToExecute
	 * @throws ScriptExecutionException
	 * @throws CompilationFailedException
	 */
	public String executeStatement(String statementToExecute) throws ScriptExecutionException, CompilationFailedException {
		try {
			Script script = (new GroovyShell()).parse(statementToExecute);
			bind = new Binding();
			setContext(bind);
			//	System.getSecurityManager().checkExec("test.bat");
			//	Runtime.getRuntime().exec("test.bat");
			//	System.getSecurityManager().checkExit(0);
			//	System.exit(0);

			//	System.setSecurityManager(new org.codehaus.groovy.tools.shell.util.NoExitSecurityManager());
			runMain(script, bind);
			return bind.getVariable(LOGBUFFER_VAR).toString();
		}
		catch (ScriptExecutionException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ScriptExecutionException(e);
		}
		finally {
			decrementScriptNumber();
		}
		/* 	catch (Exception e) {
    		int i = 0;
    		throw new ScriptExecutionException(e);
    	}*/
	}

	Script runMain(Script script, Binding bind)
	throws ScriptExecutionException,
	org.codehaus.groovy.control.CompilationFailedException,
	java.io.IOException

	{
		script.setBinding(bind);
		PrintStream temp = System.out;
		try {
			handler.setModificationEventEnabled(false, null);
			System.setOut((PrintStream)bind.getVariable(OUT_VAR));
			script.run();
		}
		catch (Exception e) {
			throw new ScriptExecutionException(e);
		}
		finally {
			handler.setModificationEventEnabled(true, null);
			handler.fireAllElementsModificationEvent(ElementEvent.getAllElementEvent());
			System.setOut(temp);
		}
		return script;
	}

	private int existingScripts = -1;

	private void incrementScriptNumber() {
		if (existingScripts < 0) {
			existingScripts = 1;
			bind = new Binding();
			setContext(bind);
		} else {
			existingScripts++;
		}
	}

	private void decrementScriptNumber() {
		if (existingScripts == 1) {
			bind = null;
			existingScripts = -1;
		} else {
			existingScripts--;
		}
	}

	private Hashtable<String, Object> savedVariables = null;

	void saveVariable(Object o, String name) {
		if (savedVariables == null) {
			savedVariables = new Hashtable<String, Object>();
		}
		savedVariables.put(name, o);
	}

	void saveWorkSpace(Map<String, Object> map) {
		if (savedVariables == null) {
			savedVariables = new Hashtable<String, Object>();
		}
		for (String s : map.keySet()) {
			savedVariables.put(s, map.get(s));
		}
	}

	Object loadVariable(String name) {
		if (savedVariables != null) {
			return savedVariables.get(name);
		}
		return null;
	}

	Map<String, Object> loadWorkSpace() {
		Hashtable<String, Object> ht = new Hashtable<String, Object>();
		if (savedVariables != null) {
			ht.putAll(savedVariables);
		}
		return ht;
	}

	void purgeSavedWorkspace() {
		if (savedVariables != null) {
			savedVariables.clear();
		}
	}

	Object removeSavedVariable(String name) {
		if (savedVariables != null) {
			return savedVariables.remove(name);
		}
		return null;
	}


	public void runnerStateChanged(RunnerEvent evt) {
		GroovyScriptManager.Runner runner = (GroovyScriptManager.Runner)evt.getSource();
		switch (runner.getRunningState()) {
			case DELETED :
				decrementScriptNumber();
				break;
			default:
		}		
	}

	public void runnerSpeedChanged(RunnerEvent evt) {}

	public void runnerProgressed(int percent) {}

	private void setContext(Binding binding) {
		java.io.CharArrayWriter buffer = new java.io.CharArrayWriter();
		MultiplePrintStream wr = new MultiplePrintStream(true);
		wr.addDestination(buffer);
		//   	wr.addDestination(new java.io.PrintWriter(System.out));
		binding.setVariable(LAYERS_VAR, handler.getLayerContainers());
		binding.setVariable(MAINHANDLER_VAR, handler);
		binding.setVariable(GRAPH_VAR, handler);
		binding.setVariable(LOGBUFFER_VAR, buffer);
		binding.setVariable(OUT_VAR, wr);
		binding.setVariable(PRIVATE_SCRIPT_MANAGER, this);
	}


	final public static String LAYERS_VAR = "layers";
	final public static String MAINHANDLER_VAR = "mainHandler";
	final public static String GRAPH_VAR = "graph";
	/**
	 * @deprecated
	 * Access main frame through uiManager
	 */
	@Deprecated
	final public static String BASEFRAME_VAR = "baseFrame";
	final public static String UIMANAGER_VAR = "uiManager";
	final public static String LOGBUFFER_VAR = "logBuffer";
	final public static String OUT_VAR = "out";

	final static String PRIVATE_SCRIPT_MANAGER = Double.valueOf(Math.random()).toString();

	public Map<String, JavancoFile> getRecentScriptsMap() {
		return recentScripts;
	}

	public class Runner extends ch.epfl.general_libraries.gui.Runner {

		private String name = null;

		Script script = null;
		Binding binding = null;
		String key = null;

		private java.io.CharArrayWriter buffer = null;

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public String toString() {
			return "[" + key + "] " + getRunningState() + " : " + name.replaceAll(".groovy", "");
		}

		public String getLog() {
			return buffer.toString();
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void init() {
			super.init();
		}

		private Runner(Script script, String name, Binding binding) {
			super(GroovyScriptManager.this);
			this.name = name;
			this.script = script;
			this.binding = binding;
			this.key = Integer.toHexString(hashCode());
			buffer = (java.io.CharArrayWriter)binding.getVariable("logBuffer");
			out = (java.io.PrintStream)binding.getVariable("out");
		}

		@Override
		public void runnerStep() {
			handler.setModificationEventEnabled(false, null);
			script.invokeMethod("step", null);
			handler.setModificationEventEnabled(true, null);
		}
	}
}
