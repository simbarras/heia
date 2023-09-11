package ch.epfl.javanco.scripting;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.Map;

import ch.epfl.javanco.base.Javanco;
import ch.epfl.javanco.io.JavancoFile;

class GroovyScriptTools {

	public static void runScript(groovy.lang.Script script, JavancoFile f)
	throws ScriptExecutionException,
	org.codehaus.groovy.control.CompilationFailedException,
	java.io.IOException
	{
		GroovyScriptManager man = (GroovyScriptManager)script.getBinding().getVariable(GroovyScriptManager.PRIVATE_SCRIPT_MANAGER);
		Script newScript = (new GroovyShell()).parse(f);
		man.runMain(newScript, script.getBinding());
	}

	public static void runScript(groovy.lang.Script script, String scriptName)
	throws ScriptExecutionException,
	org.codehaus.groovy.control.CompilationFailedException,
	java.io.IOException
	{
		JavancoFile scriptFile = new JavancoFile(Javanco.getProperty(Javanco.JAVANCO_GROOVY_SCRIPTS_DIR_PROPERTY) + scriptName);
		if (scriptFile.exists()) {
			runScript(script, scriptFile);
		} else {
			throw new java.io.IOException("No file " + scriptFile.getAbsolutePath());
		}
	}

	public static void saveVariable(groovy.lang.Script script, String name) {
		GroovyScriptManager man = (GroovyScriptManager)script.getBinding().getVariable(GroovyScriptManager.PRIVATE_SCRIPT_MANAGER);
		Binding bind = script.getBinding();
		Object toSave = bind.getVariable(name);
		if (toSave != null) {
			man.saveVariable(toSave, name);
		} else {
			throw new NullPointerException();
		}
	}

	@SuppressWarnings("unchecked")
	public static void saveWorkspace(groovy.lang.Script script) {
		GroovyScriptManager man = (GroovyScriptManager)script.getBinding().getVariable(GroovyScriptManager.PRIVATE_SCRIPT_MANAGER);
		Binding bind = script.getBinding();
		if (bind != null) {
			man.saveWorkSpace(bind.getVariables());
		} else {
			throw new NullPointerException("binding undefined");
		}
	}

	public static void loadVariable(groovy.lang.Script script, String name) {
		GroovyScriptManager man = (GroovyScriptManager)script.getBinding().getVariable(GroovyScriptManager.PRIVATE_SCRIPT_MANAGER);
		Binding bind = script.getBinding();
		bind.setVariable(name, man.loadVariable(name));
	}

	public static void loadWorkspace(groovy.lang.Script script) {
		GroovyScriptManager man = (GroovyScriptManager)script.getBinding().getVariable(GroovyScriptManager.PRIVATE_SCRIPT_MANAGER);
		Binding bind = script.getBinding();
		for (Map.Entry<String, Object> entry : man.loadWorkSpace().entrySet()) {
			if (!(entry.getKey().equals(GroovyScriptManager.PRIVATE_SCRIPT_MANAGER))) {
				bind.setVariable(entry.getKey(), entry.getValue());
			}
		}

	}

	public static boolean testVariable(groovy.lang.Script script, String name) {
		GroovyScriptManager man = (GroovyScriptManager)script.getBinding().getVariable(GroovyScriptManager.PRIVATE_SCRIPT_MANAGER);
		return (man.loadVariable(name) != null);
	}

	public static void purgeSavedWorkspace(groovy.lang.Script script) {
		GroovyScriptManager man = (GroovyScriptManager)script.getBinding().getVariable(GroovyScriptManager.PRIVATE_SCRIPT_MANAGER);
		man.purgeSavedWorkspace();
	}

	public static void removeSavedVariable(groovy.lang.Script script, String name) {
		GroovyScriptManager man = (GroovyScriptManager)script.getBinding().getVariable(GroovyScriptManager.PRIVATE_SCRIPT_MANAGER);
		man.removeSavedVariable(name);
	}

	public static void purgeCurrentWorkspace(groovy.lang.Script script) {
		Binding bind = script.getBinding();
		Binding newBind = new Binding();
		Object layers = bind.getVariable(GroovyScriptManager.LAYERS_VAR);
		Object log = bind.getVariable(GroovyScriptManager.LOGBUFFER_VAR);
		Object main = bind.getVariable(GroovyScriptManager.MAINHANDLER_VAR);
		Object out = bind.getVariable(GroovyScriptManager.OUT_VAR);
		Object pri = bind.getVariable(GroovyScriptManager.PRIVATE_SCRIPT_MANAGER);
		newBind.setVariable(GroovyScriptManager.LAYERS_VAR, layers);
		newBind.setVariable(GroovyScriptManager.LOGBUFFER_VAR, log);
		newBind.setVariable(GroovyScriptManager.MAINHANDLER_VAR, main);
		newBind.setVariable(GroovyScriptManager.OUT_VAR, out);
		newBind.setVariable(GroovyScriptManager.PRIVATE_SCRIPT_MANAGER, pri);
		script.setBinding(newBind);

	}


}
